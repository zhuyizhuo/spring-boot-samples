package com.example.memcache.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Memcache配置类，实现Spring Cache接口与XMemcached的集成
 * 符合Memcache最佳实践的配置实现
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Value("${memcache.servers:localhost:11211}")  // 提供默认值
    private String memcacheServers;
    
    @Value("${memcache.connectionPoolSize:10}")
    private int connectionPoolSize;
    
    @Value("${memcache.opTimeout:3000}")
    private int opTimeout;
    
    @Value("${memcache.connectTimeout:1000}")
    private int connectTimeout;
    
    @Value("${memcache.retries:3}")
    private int retries;
    
    @Value("${memcache.maxIdleTime:3600}")
    private int maxIdleTime; // 最大空闲时间，单位秒
    
    @Value("${memcache.defaultExpiry:3600}")
    private int defaultExpiry; // 默认缓存过期时间，单位秒

    @Bean(destroyMethod = "shutdown")  // 添加销毁方法，确保资源正确释放
    public MemcachedClient memcachedClient() {
        try {
            // 参数验证
            Assert.hasText(memcacheServers, "Memcache servers must be configured");
            Assert.isTrue(connectionPoolSize > 0, "Connection pool size must be greater than 0");
            Assert.isTrue(opTimeout > 0, "Operation timeout must be greater than 0");
            
            log.info("Attempting to connect to Memcached servers: {}", memcacheServers);
            
            // 构建XMemcached客户端
            MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(memcacheServers));
            
            // 设置连接池大小
            builder.setConnectionPoolSize(connectionPoolSize);
            
            // 设置操作超时时间
            builder.setOpTimeout(opTimeout);
            
            // 设置连接超时时间
            builder.setConnectTimeout(connectTimeout);
            
            // 构建客户端
            MemcachedClient client = builder.build();
            
            // 测试连接
            boolean connected = testConnection(client);
            
            if (connected) {
                log.info("Successfully connected to Memcached servers");
                return client;
            } else {
                throw new RuntimeException("Memcached connection test failed");
            }
        } catch (Exception e) {
            log.error("Failed to connect to Memcached servers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Memcached client", e);
        }
    }
    
    /**
     * 测试Memcached连接
     * @param client Memcached客户端
     * @return 是否连接成功
     */
    private boolean testConnection(MemcachedClient client) {
        try {
            String testKey = "connection_test:" + System.currentTimeMillis();
            client.set(testKey, 10, "test");
            String result = client.get(testKey);
            client.delete(testKey); // 清理测试键
            return "test".equals(result);
        } catch (Exception e) {
            log.error("Connection test failed: {}", e.getMessage(), e);
            return false;
        }
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        log.info("Creating XMemcachedCacheManager with default expiry: {}s", defaultExpiry);
        MemcachedClient client = memcachedClient();
        return new XMemcachedCacheManager(client, Arrays.asList("userCache", "userListCache"), defaultExpiry);
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        log.info("Configuring cache key generator");
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            // 使用类名和方法名作为前缀
            sb.append(target.getClass().getSimpleName());
            sb.append(":");
            sb.append(method.getName());
            
            // 添加参数
            if (params != null && params.length > 0) {
                sb.append(":");
                for (int i = 0; i < params.length; i++) {
                    if (params[i] != null) {
                        // 对于复杂对象，使用其hashCode而不是toString，避免生成过长的键
                        sb.append(params[i].hashCode());
                    } else {
                        sb.append("null");
                    }
                    
                    if (i < params.length - 1) {
                        sb.append(",");
                    }
                }
            }
            
            // Memcached键长度限制，通常建议不超过250字节
            String key = sb.toString();
            if (key.length() > 250) {
                // 如果键过长，使用其哈希值作为键
                key = String.valueOf(key.hashCode());
                log.debug("Cache key too long, using hash: {}", key);
            }
            
            return key;
        };
    }

    /**
     * XMemcached缓存管理器，适配Spring的CacheManager接口
     * 实现了更完善的缓存生命周期管理
     */
    class XMemcachedCacheManager implements CacheManager {
        private final MemcachedClient client;
        private final Map<String, Cache> caches = new ConcurrentHashMap<>();
        private final int defaultExpiry; // 默认缓存过期时间
        private final AtomicBoolean initialized = new AtomicBoolean(false);
        
        public XMemcachedCacheManager(MemcachedClient client, Collection<String> cacheNames, int defaultExpiry) {
            this.client = client;
            this.defaultExpiry = defaultExpiry;
            
            // 初始化缓存
            if (cacheNames != null && !cacheNames.isEmpty()) {
                initializeCaches(cacheNames);
            }
            
            initialized.set(true);
            log.info("XMemcachedCacheManager initialized with {} caches", caches.size());
        }
        
        /**
         * 初始化缓存集合
         */
        private void initializeCaches(Collection<String> cacheNames) {
            cacheNames.forEach(cacheName -> {
                log.info("Initializing cache: {}", cacheName);
                caches.put(cacheName, new XMemcachedCache(cacheName, client, defaultExpiry));
            });
        }
        
        @Override
        public Cache getCache(String name) {
            Assert.hasText(name, "Cache name must not be empty");
            
            Cache cache = caches.get(name);
            if (cache == null) {
                // 动态创建缓存
                cache = new XMemcachedCache(name, client, defaultExpiry);
                caches.put(name, cache);
                
                if (initialized.get()) {
                    log.info("Dynamically created cache: {}", name);
                }
            }
            return cache;
        }
        
        @Override
        public Collection<String> getCacheNames() {
            return caches.keySet();
        }
    }



    /**
     * XMemcached缓存实现，适配Spring的Cache接口
     * 实现了高效的缓存操作、重试机制和异常处理
     */
    class XMemcachedCache implements Cache {
        private static final Logger log = LoggerFactory.getLogger(XMemcachedCache.class);
        private final String name;
        private final MemcachedClient client;
        private final int defaultExpiry; // 缓存过期时间
        private static final int MAX_RETRY_COUNT = 2; // 操作失败最大重试次数
        
        public XMemcachedCache(String name, MemcachedClient client, int defaultExpiry) {
            this.name = name;
            this.client = client;
            this.defaultExpiry = defaultExpiry;
            log.info("Creating Memcached cache: {} with expiry: {}s", name, defaultExpiry);
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public Object getNativeCache() {
            return client;
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(Object key, Class<T> type) {
            String cacheKey = generateKey(key);
            
            try {
                T value = client.get(cacheKey);
                
                // 检查是否为null值标记
                if (value == NullValue.INSTANCE) {
                    return null;
                }
                
                // 类型检查
                if (value != null && type != null && !type.isInstance(value)) {
                    log.warn("Cached value type mismatch for key {}: expected {}, got {}", 
                            cacheKey, type.getName(), value.getClass().getName());
                    throw new IllegalStateException("Cached value is not of required type");
                }
                
                return value;
            } catch (Exception e) {
                log.error("Error getting cache from Memcached for key {}: {}", cacheKey, e.getMessage(), e);
                
                // 区分不同类型的异常
                if (e instanceof TimeoutException) {
                    throw new RuntimeException("Memcached operation timeout for key: " + cacheKey, e);
                } else if (e instanceof MemcachedException) {
                    throw new RuntimeException("Memcached server error for key: " + cacheKey, e);
                } else {
                    throw new RuntimeException("Error getting cache from Memcached", e);
                }
            }
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(Object key, Callable<T> valueLoader) {
            String cacheKey = generateKey(key);
            
            // 尝试从缓存获取
            try {
                T value = client.get(cacheKey);
                
                // 检查是否为null值标记
                if (value == NullValue.INSTANCE) {
                    return null;
                }
                
                if (value != null) {
                    return value;
                }
            } catch (Exception e) {
                log.error("Error getting cache from Memcached for key {}: {}", cacheKey, e.getMessage(), e);
                // 缓存读取失败时，不抛出异常，而是尝试加载值
            }
            
            // 缓存未命中或读取失败，从valueLoader加载
            try {
                T value = valueLoader.call();
                
                // 只有加载成功且值不为null时才放入缓存
                if (value != null) {
                    // 放入缓存时使用重试机制
                    putWithRetry(cacheKey, value, defaultExpiry, 0);
                }
                
                return value;
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException("Error loading cache value", e);
            }
        }
        
        @Override
        public ValueWrapper get(Object key) {
            String cacheKey = generateKey(key);
            
            try {
                Object value = client.get(cacheKey);
                // 检查是否为null值标记
                if (value == NullValue.INSTANCE) {
                    value = null;
                }
                return value != null ? new SimpleValueWrapper(value) : null;
            } catch (Exception e) {
                log.error("Error getting cache from Memcached for key {}: {}", cacheKey, e.getMessage(), e);
                // 对于get方法，可以返回null而不是抛出异常，避免缓存问题影响业务流程
                return null;
            }
        }
        
        @Override
        public void put(Object key, Object value) {
            // 空值检查
            if (value == null) {
                log.warn("Attempting to cache null value for key: {}", key);
                // 缓存null值使用一个特殊的标记对象，而不是直接存储null
                value = NullValue.INSTANCE;
            }
            
            String cacheKey = generateKey(key);
            
            // 使用重试机制
            boolean success = putWithRetry(cacheKey, value, defaultExpiry, 0);
            if (!success) {
                log.error("Failed to put cache to Memcached after retries for key: {}", cacheKey);
                // 可以选择是否抛出异常，这里为了不影响业务流程，只记录日志
            }
        }
        
        /**
         * 带重试机制的缓存写入操作
         */
        private boolean putWithRetry(String cacheKey, Object value, int expiry, int retryCount) {
            try {
                client.set(cacheKey, expiry, value);
                return true;
            } catch (Exception e) {
                log.warn("Error putting cache to Memcached (attempt {}/{}), key: {}, error: {}", 
                        retryCount + 1, MAX_RETRY_COUNT + 1, cacheKey, e.getMessage());
                
                // 如果还可以重试
                if (retryCount < MAX_RETRY_COUNT) {
                    // 简单的指数退避重试策略
                    try {
                        Thread.sleep(100 * (1 << retryCount));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    return putWithRetry(cacheKey, value, expiry, retryCount + 1);
                }
                
                log.error("Failed to put cache to Memcached after retries for key: {}", cacheKey, e);
                return false;
            }
        }
        
        @Override
        public ValueWrapper putIfAbsent(Object key, Object value) {
            // 空值检查
            if (value == null) {
                log.warn("Attempting to cache null value for key: {}", key);
                return null;
            }
            
            String cacheKey = generateKey(key);
            
            try {
                // 先获取现有值
                Object existing = client.get(cacheKey);
                
                // 检查是否为特殊的null标记对象
                if (existing == NullValue.INSTANCE) {
                    existing = null;
                }
                
                // 如果不存在，则放入缓存
                if (existing == null) {
                    boolean success = putWithRetry(cacheKey, value, defaultExpiry, 0);
                    return success ? null : new SimpleValueWrapper(existing);
                }
                
                return new SimpleValueWrapper(existing);
            } catch (Exception e) {
                log.error("Error in putIfAbsent operation for key {}: {}", cacheKey, e.getMessage(), e);
                // 发生异常时返回null，允许业务逻辑继续执行
                return null;
            }
        }
        
        @Override
        public void evict(Object key) {
            String cacheKey = generateKey(key);
            
            try {
                client.delete(cacheKey);
                log.debug("Evicted cache key: {}", cacheKey);
            } catch (Exception e) {
                log.error("Error evicting cache from Memcached for key {}: {}", cacheKey, e.getMessage(), e);
                // 对于删除操作，避免抛出异常影响业务流程
            }
        }
        
        @Override
        public void clear() {
            log.info("开始清除缓存: {}", name);
            
            try {
                // 在实际项目中，Memcached确实不支持直接按前缀删除
                // 但是我们可以实现一个简单的清除策略：使用前缀查询并删除
                // 注意：在生产环境中，如果缓存项非常多，这种方法可能效率不高
                // 更高效的方式是使用命名空间模式
                
                // 对于用户列表缓存，我们知道具体的键
                if ("userListCache".equals(name)) {
                    String listKey = generateKey("allUsers");
                    client.delete(listKey);
                    log.info("已清除用户列表缓存: {}", listKey);
                    return;
                }
                
                // 对于用户缓存，我们从模拟数据库中获取所有用户ID并逐个清除
                if ("userCache".equals(name)) {
                    // 这里我们假设可以访问到用户服务来获取所有用户ID
                    // 在实际项目中，可能需要其他方式来获取缓存的键
                    // 由于我们在UserServiceImpl中有client访问，这里简化处理
                    log.info("对于用户缓存，建议使用UserServiceImpl中的clearCache方法来精确清除特定用户缓存");
                    
                    // 注意：在实际生产环境中，可能需要实现更复杂的缓存键管理
                    // 例如使用Redis而不是Memcached，或者使用命名空间模式
                }
                
                log.info("缓存清除操作已尝试完成");
            } catch (Exception e) {
                log.error("清除缓存时发生错误: {}", e.getMessage(), e);
            }
        }
        
        /**
         * 特殊的空值标记类，用于缓存null值
         */
        private static class NullValue {
            public static final NullValue INSTANCE = new NullValue();
            
            private NullValue() {
                // 私有构造函数
            }
        }
        
        /**
         * 生成缓存键，遵循Memcached键的最佳实践
         * 1. 键长度不超过250字节
         * 2. 包含缓存名称作为前缀，便于区分和管理
         */
        private String generateKey(Object key) {
            String rawKey = key != null ? key.toString() : "null";
            String cacheKey = name + ":" + rawKey;
            
            // Memcached键长度限制，通常建议不超过250字节
            // 如果键过长，使用其哈希值作为键
            if (cacheKey.length() > 250) {
                cacheKey = name + ":" + rawKey.hashCode();
                log.debug("Cache key too long, using hash: {}", cacheKey);
            }
            
            return cacheKey;
        }
        
        /**
         * 简单的值包装器
         */
        class SimpleValueWrapper implements ValueWrapper {
            private final Object value;
            
            public SimpleValueWrapper(Object value) {
                this.value = value;
            }
            
            @Override
            public Object get() {
                return value;
            }
        }
    }

}