package com.example.ehcache.service.impl;

import com.example.ehcache.entity.User;
import com.example.ehcache.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户服务实现类
 * 使用内存Map模拟数据库，并实现缓存管理
 */
@Service
public class UserServiceImpl implements UserService {

    // 模拟数据库存储
    private final Map<Long, User> userRepository = new ConcurrentHashMap<>();
    private long nextId = 1;

    // 初始化一些测试数据
    public UserServiceImpl() {
        saveUser(new User(nextId++, "张三", "zhangsan@example.com", 25));
        saveUser(new User(nextId++, "李四", "lisi@example.com", 30));
        saveUser(new User(nextId++, "王五", "wangwu@example.com", 28));
    }

    /**
     * 使用@Cacheable注解，将结果缓存到userCache缓存中
     * key指定缓存的键为用户ID
     */
    @Override
    @Cacheable(value = "userCache", key = "#id")
    public User getUserById(Long id) {
        // 模拟数据库查询延迟
        simulateSlowService();
        System.out.println("从数据库查询用户: " + id);
        return userRepository.get(id);
    }

    /**
     * 使用@Cacheable注解，将所有用户列表缓存
     */
    @Override
    @Cacheable(value = "userListCache", key = "'allUsers'")
    public List<User> getAllUsers() {
        simulateSlowService();
        System.out.println("从数据库查询所有用户");
        return new ArrayList<>(userRepository.values());
    }

    /**
     * 保存用户，同时清除列表缓存
     */
    @Override
    @Caching(
        put = @CachePut(value = "userCache", key = "#result.id"),
        evict = @CacheEvict(value = "userListCache", key = "'allUsers'")
    )
    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        userRepository.put(user.getId(), user);
        System.out.println("保存用户: " + user);
        return user;
    }

    /**
     * 更新用户，使用@CachePut更新缓存，使用@CacheEvict清除列表缓存
     */
    @Override
    @Caching(
        put = @CachePut(value = "userCache", key = "#user.id"),
        evict = @CacheEvict(value = "userListCache", key = "'allUsers'")
    )
    public User updateUser(User user) {
        if (!userRepository.containsKey(user.getId())) {
            throw new IllegalArgumentException("用户不存在: " + user.getId());
        }
        userRepository.put(user.getId(), user);
        System.out.println("更新用户: " + user);
        return user;
    }

    /**
     * 删除用户信息
     * 使用@Caching注解，同时删除userCache中的指定用户和清除userListCache
     */
    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "userCache", key = "#id"),
            @CacheEvict(value = "userListCache", key = "'allUsers'")
        }
    )
    public boolean deleteUser(Long id) {
        // 检查用户是否存在
        if (!userRepository.containsKey(id)) {
            return false;
        }
        // 删除用户
        userRepository.remove(id);
        return true;
    }

    /**
     * 清除所有用户相关的缓存
     */
    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "userCache", allEntries = true),
            @CacheEvict(value = "userListCache", allEntries = true)
        }
    )
    public void clearUserCache() {
        System.out.println("清除所有用户缓存");
    }

    /**
     * 模拟慢服务调用
     */
    private void simulateSlowService() {
        try {
            Thread.sleep(300); // 模拟300ms的延迟
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}