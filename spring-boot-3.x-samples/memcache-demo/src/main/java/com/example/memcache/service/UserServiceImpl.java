package com.example.memcache.service;

import com.example.memcache.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    
    // 模拟数据库存储
    private final Map<Long, User> userRepository = new ConcurrentHashMap<>();
    private Long nextId = 1L;
    
    @Autowired
    private CacheManager cacheManager;

    // 初始化一些测试数据
    public UserServiceImpl() {
        initTestData();
    }

    private void initTestData() {
        addUser(new User(nextId++, "张三", "zhangsan@example.com", 25));
        addUser(new User(nextId++, "李四", "lisi@example.com", 30));
        addUser(new User(nextId++, "王五", "wangwu@example.com", 28));
        System.out.println("初始化用户数据完成");
    }

    @Override
    @Cacheable(value = "userCache", key = "#id")
    public User getUserById(Long id) {
        // 模拟数据库查询延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        User user = userRepository.get(id);
        System.out.println("从数据库获取用户: " + user);
        return user;
    }

    @Override
    @Cacheable(value = "userListCache", key = "'allUsers'")
    public List<User> getAllUsers() {
        // 模拟数据库查询延迟
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        List<User> users = new ArrayList<>(userRepository.values());
        System.out.println("从数据库获取所有用户，共 " + users.size() + " 个");
        return users;
    }

    @Override
    @CachePut(value = "userCache", key = "#user.id")
    @CacheEvict(value = "userListCache", key = "'allUsers'")
    public User addUser(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        user.setCreateTime(LocalDateTime.now());
        userRepository.put(user.getId(), user);
        System.out.println("添加用户: " + user);
        
        // 添加用户后清除用户列表缓存
        clearUserListCache();
        
        return user;
    }

    @Override
    @CachePut(value = "userCache", key = "#user.id")
    public User updateUser(User user) {
        if (userRepository.containsKey(user.getId())) {
            userRepository.put(user.getId(), user);
            System.out.println("更新用户: " + user);
            
            // 更新用户后清除用户列表缓存
            clearUserListCache();
            
            return user;
        }
        return null;
    }

    @Override
    @CacheEvict(value = "userCache", key = "#id")
    public boolean deleteUser(Long id) {
        User removed = userRepository.remove(id);
        boolean deleted = removed != null;
        System.out.println("删除用户: " + id + ", 结果: " + deleted);
        
        // 删除用户后清除用户列表缓存
        clearUserListCache();
        
        return deleted;
    }

    @Override
    @CacheEvict(value = {"userCache", "userListCache"}, allEntries = true)
    public void clearCache(Long id) {
        try {
            if (id != null) {
                // 清除特定用户缓存
                Cache userCache = cacheManager.getCache("userCache");
                if (userCache != null) {
                    // Spring Cache的@Cacheable注解中的key会经过KeyGenerator处理
                    // 我们直接使用evict方法，让CacheManager处理键的生成
                    userCache.evict(id);
                    log.info("清除用户{}的缓存成功", id);
                }
                
                // 同时清除用户列表缓存，确保获取所有用户时能看到最新数据
                Cache userListCache = cacheManager.getCache("userListCache");
                if (userListCache != null) {
                    userListCache.evict("allUsers");
                    log.info("清除用户列表缓存成功");
                }
            } else {
                // 清除所有缓存
                // 使用Spring Cache注解的allEntries=true已经会尝试清除所有缓存
                // 这里额外记录日志并确保清除用户列表缓存
                Cache userListCache = cacheManager.getCache("userListCache");
                if (userListCache != null) {
                    userListCache.evict("allUsers");
                    log.info("清除用户列表缓存成功");
                }
                
                log.info("清除所有缓存操作已执行");
            }
        } catch (Exception e) {
            log.error("清除缓存失败: {}", e.getMessage(), e);
            // 即使出错也继续，Spring Cache注解仍然会尝试清除缓存
        }
    }

    /**
     * 清除用户列表缓存
     */
    @CacheEvict(value = "userListCache", key = "'allUsers'")
    private void clearUserListCache() {
        // 方法体为空，仅使用注解清除缓存
    }
}