package com.example.ehcache.controller;

import com.example.ehcache.entity.User;
import com.example.ehcache.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 提供RESTful API接口，用于测试缓存功能
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    /**
     * 测试缓存性能
     * 该接口会连续调用两次getUserById，第一次走数据库，第二次走缓存
     */
    @GetMapping("/test-cache/{id}")
    public ResponseEntity<Map<String, Object>> testCache(@PathVariable Long id, HttpServletResponse response) {
        // 检查缓存是否命中
        boolean isCacheHit = checkUserCache(id);
        response.setHeader("X-Cache-Hit", String.valueOf(isCacheHit));
        
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        
        // 调用服务层方法
        User user = userService.getUserById(id);
        
        // 记录结束时间
        long endTime = System.currentTimeMillis();
        
        // 返回结果，包含执行时间
        return ResponseEntity.ok(Map.of(
                "user", user,
                "executionTime", (endTime - startTime) + "ms",
                "fromCache", isCacheHit
        ));
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id, HttpServletResponse response) {
        // 检查缓存是否命中
        boolean isCacheHit = checkUserCache(id);
        response.setHeader("X-Cache-Hit", String.valueOf(isCacheHit));
        
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * 获取所有用户信息
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(HttpServletResponse response) {
        // 检查缓存是否命中
        boolean isCacheHit = checkUserListCache();
        response.setHeader("X-Cache-Hit", String.valueOf(isCacheHit));
        
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 保存用户信息
     */
    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 删除用户信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 清除所有缓存
     */
    @PostMapping("/clear-cache")
    public ResponseEntity<Map<String, String>> clearCache() {
        userService.clearUserCache();
        return ResponseEntity.ok(Map.of("message", "缓存清除成功"));
    }
    
    /**
     * 检查用户缓存是否存在
     */
    private boolean checkUserCache(Long id) {
        Cache cache = cacheManager.getCache("userCache");
        if (cache != null) {
            return cache.get(id) != null;
        }
        return false;
    }
    
    /**
     * 检查用户列表缓存是否存在
     */
    private boolean checkUserListCache() {
        Cache cache = cacheManager.getCache("userListCache");
        if (cache != null) {
            // 简单判断，实际中可能需要根据具体的缓存键来判断
            return cache.get("allUsers") != null;
        }
        return false;
    }
}