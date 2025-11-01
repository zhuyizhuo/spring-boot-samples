package com.example.redisdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

/**
 * Redis分布式锁工具类
 * 提供分布式环境下的互斥访问控制
 */
@Component
public class RedisLock {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final long DEFAULT_EXPIRE_TIME = 30;
    private static final long DEFAULT_RETRY_INTERVAL = 100;
    private static final int DEFAULT_MAX_RETRY = 3;

    /**
     * 加锁
     * @param key 锁的键
     * @param expireTime 过期时间（秒）
     * @return 锁标识，如果获取失败返回null
     */
    public String lock(String key, long expireTime) {
        // 生成唯一的锁标识
        String lockId = UUID.randomUUID().toString();
        String lockKey = "lock:" + key;
        
        // 设置锁，并设置过期时间
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockId, expireTime, TimeUnit.SECONDS);
        
        return success != null && success ? lockId : null;
    }

    /**
     * 加锁（使用默认过期时间）
     * @param key 锁的键
     * @return 锁标识，如果获取失败返回null
     */
    public String lock(String key) {
        return lock(key, DEFAULT_EXPIRE_TIME);
    }

    /**
     * 尝试获取锁，如果失败则重试
     * @param key 锁的键
     * @param expireTime 过期时间（秒）
     * @param retryTimes 重试次数
     * @param retryInterval 重试间隔（毫秒）
     * @return 锁标识，如果获取失败返回null
     */
    public String tryLock(String key, long expireTime, int retryTimes, long retryInterval) {
        String lockId;
        int count = 0;
        
        while (count < retryTimes) {
            lockId = lock(key, expireTime);
            if (lockId != null) {
                return lockId;
            }
            
            try {
                Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            
            count++;
        }
        
        return null;
    }

    /**
     * 尝试获取锁（使用默认参数）
     * @param key 锁的键
     * @return 锁标识，如果获取失败返回null
     */
    public String tryLock(String key) {
        return tryLock(key, DEFAULT_EXPIRE_TIME, DEFAULT_MAX_RETRY, DEFAULT_RETRY_INTERVAL);
    }

    /**
     * 释放锁（使用Lua脚本保证原子性）
     * @param key 锁的键
     * @param lockId 锁标识
     * @return 是否成功释放锁
     */
    public boolean unlock(String key, String lockId) {
        if (lockId == null) {
            return false;
        }
        
        String lockKey = "lock:" + key;
        
        // Lua脚本，先检查锁标识是否匹配，再删除锁
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockId);
        
        return result != null && result > 0;
    }

    /**
     * 延长锁的过期时间
     * @param key 锁的键
     * @param lockId 锁标识
     * @param expireTime 新的过期时间（秒）
     * @return 是否成功延长
     */
    public boolean renewLock(String key, String lockId, long expireTime) {
        if (lockId == null) {
            return false;
        }
        
        String lockKey = "lock:" + key;
        
        // Lua脚本，先检查锁标识是否匹配，再延长过期时间
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('pexpire', KEYS[1], ARGV[2]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), 
                lockId, TimeUnit.SECONDS.toMillis(expireTime));
        
        return result != null && result > 0;
    }

    /**
     * 检查锁是否存在
     * @param key 锁的键
     * @return 锁是否存在
     */
    public boolean isLocked(String key) {
        String lockKey = "lock:" + key;
        return redisTemplate.hasKey(lockKey);
    }
}