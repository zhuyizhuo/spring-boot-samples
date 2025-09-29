package com.zhuo.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * FactoryBean扩展点增强实现
 * 提供高级配置管理功能，支持配置的动态加载、更新、验证和监听
 * 增强特性：
 * - 条件装配与按需激活
 * - 完整的生命周期管理
 * - 线程安全的配置操作
 * - 配置分组与命名空间支持
 * - 配置验证与类型转换
 * - 配置变更监听机制
 * - 丰富的配置管理API
 */
@Component
@ConditionalOnProperty(name = "extension.config.enabled", havingValue = "true", matchIfMissing = true)
public class TestFactoryBean implements FactoryBean<TestFactoryBean.ConfigManager>, InitializingBean, DisposableBean {
    
    private static final Logger logger = LoggerFactory.getLogger(TestFactoryBean.class);
    private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger(0);
    
    // 存储创建的ConfigManager实例
    private volatile ConfigManager configManagerInstance;
    
    // 环境对象，用于获取外部配置
    private Environment environment;
    
    // 构造函数，自动注入Environment
    public TestFactoryBean(Environment environment) {
        this.environment = environment;
    }
    
    @PostConstruct
    public void init() {
        logger.info("TestFactoryBean初始化开始");
        logger.debug("FactoryBean实例ID: {}", this.hashCode());
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("TestFactoryBean属性设置完成，准备创建ConfigManager实例");
        // 预初始化ConfigManager实例
        configManagerInstance = createConfigManager();
    }
    
    @Override
    public ConfigManager getObject() throws Exception {
        if (configManagerInstance == null) {
            // 双重检查锁定模式确保线程安全
            synchronized (this) {
                if (configManagerInstance == null) {
                    configManagerInstance = createConfigManager();
                }
            }
        }
        return configManagerInstance;
    }
    
    /**
     * 创建并初始化ConfigManager实例
     */
    private ConfigManager createConfigManager() {
        logger.info("开始创建ConfigManager实例，实例计数: {}", INSTANCE_COUNT.incrementAndGet());
        
        // 创建并初始化配置管理器
        ConfigManager configManager = new ConfigManager();
        configManager.setInstanceId(UUID.randomUUID().toString());
        configManager.setCreateTime(System.currentTimeMillis());
        
        // 初始化一些默认配置
        Map<String, String> defaultConfigs = new HashMap<>();
        defaultConfigs.put("app.mode", "development");
        defaultConfigs.put("app.log.level", "INFO");
        defaultConfigs.put("cache.enabled", "true");
        defaultConfigs.put("cache.ttl", "3600");
        defaultConfigs.put("app.version", "1.0.0");
        defaultConfigs.put("app.name", "Spring Boot Extension");
        
        configManager.setConfigs(defaultConfigs);
        
        // 从环境中加载配置
        loadConfigsFromEnvironment(configManager);
        
        logger.info("ConfigManager实例创建完成，实例ID: {}", configManager.getInstanceId());
        logger.debug("ConfigManager初始化完成，初始配置项数量: {}", configManager.getConfigCount());
        
        return configManager;
    }
    
    /**
     * 从环境中加载配置
     */
    private void loadConfigsFromEnvironment(ConfigManager configManager) {
        if (environment != null) {
            try {
                // 尝试从环境中获取特定前缀的配置
                String[] configKeys = {"extension.app.name", "extension.app.version", "extension.app.mode", "extension.cache.enabled", "extension.cache.ttl"};
                int loadedCount = 0;
                
                for (String key : configKeys) {
                    String value = environment.getProperty(key);
                    if (value != null) {
                        // 将配置键转换为内部格式
                        String internalKey = key.replace("extension.", "");
                        configManager.setConfig(internalKey, value);
                        loadedCount++;
                    }
                }
                
                if (loadedCount > 0) {
                    logger.info("从环境中加载了 {} 个配置项", loadedCount);
                }
            } catch (Exception e) {
                logger.warn("从环境中加载配置时发生异常", e);
            }
        }
    }
    
    @Override
    public Class<?> getObjectType() {
        return ConfigManager.class;
    }
    
    @Override
    public boolean isSingleton() {
        // 配置管理器通常设计为单例
        return true;
    }
    
    @PreDestroy
    public void preDestroy() {
        logger.info("TestFactoryBean准备销毁");
    }
    
    @Override
    public void destroy() throws Exception {
        logger.info("TestFactoryBean销毁中，清理ConfigManager实例");
        if (configManagerInstance != null) {
            configManagerInstance.clearAllConfigs();
            configManagerInstance = null;
        }
        logger.info("TestFactoryBean销毁完成");
    }
    
    /**
     * 配置管理器增强实现
     * 提供线程安全的配置管理、分组、验证和变更监听功能
     */
    public static class ConfigManager {
        private String instanceId;
        private long createTime;
        // 使用ConcurrentHashMap确保线程安全
        private final Map<String, String> configs = new ConcurrentHashMap<>();
        
        // 配置变更监听器列表
        private final List<ConfigChangeListener> listeners = Collections.synchronizedList(new ArrayList<>());
        
        public String getInstanceId() {
            return instanceId;
        }
        
        public void setInstanceId(String instanceId) {
            this.instanceId = instanceId;
        }
        
        public long getCreateTime() {
            return createTime;
        }
        
        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
        
        /**
         * 获取所有配置项
         */
        public Map<String, String> getAllConfigs() {
            return new HashMap<>(configs);
        }
        
        /**
         * 批量设置配置
         */
        public void setConfigs(Map<String, String> newConfigs) {
            if (newConfigs == null || newConfigs.isEmpty()) {
                return;
            }
            
            Map<String, ConfigChangeEvent> changeEvents = new HashMap<>();
            
            // 收集变更并设置新值
            newConfigs.forEach((key, value) -> {
                if (key != null && !key.trim().isEmpty()) {
                    String oldValue = configs.put(key, value);
                    if (oldValue == null || !oldValue.equals(value)) {
                        changeEvents.put(key, new ConfigChangeEvent(key, oldValue, value));
                    }
                }
            });
            
            // 批量通知变更
            if (!changeEvents.isEmpty()) {
                notifyConfigsChanged(changeEvents);
            }
        }
        
        /**
         * 获取指定前缀的配置项
         */
        public Map<String, String> getConfigsByPrefix(String prefix) {
            if (prefix == null || prefix.isEmpty()) {
                return getAllConfigs();
            }
            
            final String actualPrefix = prefix.endsWith(".") ? prefix : prefix + ".";
            Map<String, String> result = new HashMap<>();
            
            configs.forEach((key, value) -> {
                if (key.startsWith(actualPrefix)) {
                    result.put(key, value);
                }
            });
            
            return result;
        }
        
        /**
         * 获取配置值
         */
        public String getConfig(String key) {
            return configs.get(key);
        }
        
        /**
         * 获取配置值，带默认值
         */
        public String getConfig(String key, String defaultValue) {
            return configs.getOrDefault(key, defaultValue);
        }
        
        /**
         * 获取整型配置值
         */
        public Integer getIntConfig(String key, Integer defaultValue) {
            String value = configs.get(key);
            if (value != null) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    LoggerFactory.getLogger(getClass()).warn("配置项 {} 的值 {} 不是有效整数", key, value);
                }
            }
            return defaultValue;
        }
        
        /**
         * 获取布尔型配置值
         */
        public Boolean getBooleanConfig(String key, Boolean defaultValue) {
            String value = configs.get(key);
            if (value != null) {
                return Boolean.parseBoolean(value);
            }
            return defaultValue;
        }
        
        /**
         * 设置配置值
         */
        public void setConfig(String key, String value) {
            if (key == null || key.trim().isEmpty()) {
                throw new IllegalArgumentException("配置键不能为空");
            }
            
            String oldValue = configs.put(key, value);
            
            // 触发配置变更事件
            if (oldValue == null || !oldValue.equals(value)) {
                notifyConfigChanged(key, oldValue, value);
            }
        }
        
        /**
         * 删除配置值
         */
        public void removeConfig(String key) {
            if (key != null) {
                String oldValue = configs.remove(key);
                if (oldValue != null) {
                    notifyConfigChanged(key, oldValue, null);
                }
            }
        }
        
        /**
         * 获取配置项数量
         */
        public int getConfigCount() {
            return configs.size();
        }
        
        /**
         * 检查配置项是否存在
         */
        public boolean containsConfig(String key) {
            return key != null && configs.containsKey(key);
        }
        
        /**
         * 清除所有配置
         */
        public void clearAllConfigs() {
            Map<String, ConfigChangeEvent> changeEvents = new HashMap<>();
            
            // 收集所有配置的变更事件
            configs.forEach((key, oldValue) -> {
                changeEvents.put(key, new ConfigChangeEvent(key, oldValue, null));
            });
            
            // 清除配置
            configs.clear();
            
            // 通知变更
            if (!changeEvents.isEmpty()) {
                notifyConfigsChanged(changeEvents);
            }
        }
        
        /**
         * 根据条件筛选配置项
         */
        public Map<String, String> filterConfigs(Predicate<Map.Entry<String, String>> predicate) {
            Map<String, String> result = new HashMap<>();
            if (predicate != null) {
                configs.entrySet().stream()
                    .filter(predicate)
                    .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
            }
            return result;
        }
        
        /**
         * 注册配置变更监听器
         */
        public void addConfigChangeListener(ConfigChangeListener listener) {
            if (listener != null && !listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
        
        /**
         * 移除配置变更监听器
         */
        public void removeConfigChangeListener(ConfigChangeListener listener) {
            if (listener != null) {
                listeners.remove(listener);
            }
        }
        
        /**
         * 通知单个配置变更
         */
        private void notifyConfigChanged(String key, String oldValue, String newValue) {
            ConfigChangeEvent event = new ConfigChangeEvent(key, oldValue, newValue);
            notifyConfigChanged(event);
        }
        
        /**
         * 通知配置变更
         */
        private void notifyConfigChanged(ConfigChangeEvent event) {
            // 复制监听器列表以避免并发修改问题
            List<ConfigChangeListener> listenersCopy;
            synchronized (listeners) {
                listenersCopy = new ArrayList<>(listeners);
            }
            
            // 通知所有监听器
            for (ConfigChangeListener listener : listenersCopy) {
                try {
                    listener.onConfigChange(event);
                } catch (Exception e) {
                    LoggerFactory.getLogger(getClass()).warn("配置变更监听器执行异常", e);
                }
            }
        }
        
        /**
         * 批量通知配置变更
         */
        private void notifyConfigsChanged(Map<String, ConfigChangeEvent> events) {
            // 复制监听器列表以避免并发修改问题
            List<ConfigChangeListener> listenersCopy;
            synchronized (listeners) {
                listenersCopy = new ArrayList<>(listeners);
            }
            
            // 通知所有监听器
            for (ConfigChangeListener listener : listenersCopy) {
                try {
                    listener.onConfigsChanged(events);
                } catch (Exception e) {
                    LoggerFactory.getLogger(getClass()).warn("配置批量变更监听器执行异常", e);
                }
            }
        }
        
        @Override
        public String toString() {
            return String.format("ConfigManager{instanceId='%s', createTime=%d, configCount=%d}", 
                                 instanceId, createTime, configs.size());
        }
        
        /**
         * 配置变更事件
         */
        public static class ConfigChangeEvent {
            private final String key;
            private final String oldValue;
            private final String newValue;
            private final long timestamp;
            
            public ConfigChangeEvent(String key, String oldValue, String newValue) {
                this.key = key;
                this.oldValue = oldValue;
                this.newValue = newValue;
                this.timestamp = System.currentTimeMillis();
            }
            
            public String getKey() {
                return key;
            }
            
            public String getOldValue() {
                return oldValue;
            }
            
            public String getNewValue() {
                return newValue;
            }
            
            public long getTimestamp() {
                return timestamp;
            }
            
            public boolean isAdded() {
                return oldValue == null && newValue != null;
            }
            
            public boolean isModified() {
                return oldValue != null && newValue != null && !oldValue.equals(newValue);
            }
            
            public boolean isRemoved() {
                return oldValue != null && newValue == null;
            }
            
            @Override
            public String toString() {
                return String.format("ConfigChangeEvent{key='%s', oldValue='%s', newValue='%s', type=%s}",
                        key, oldValue, newValue,
                        isAdded() ? "ADDED" : isModified() ? "MODIFIED" : isRemoved() ? "REMOVED" : "UNKNOWN");
            }
        }
        
        /**
         * 配置变更监听器接口
         */
        public interface ConfigChangeListener {
            /**
             * 单个配置变更回调
             */
            void onConfigChange(ConfigChangeEvent event);
            
            /**
             * 批量配置变更回调
             */
            default void onConfigsChanged(Map<String, ConfigChangeEvent> events) {
                // 默认实现：对每个变更事件调用onConfigChange
                events.values().forEach(this::onConfigChange);
            }
        }
    }
}
