package com.example.memcache.controller;

import com.example.memcache.model.User;
import com.example.memcache.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 跳转到测试页面
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    // 获取单个用户
    @GetMapping("/get/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        User user = userService.getUserById(id);
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        if (user != null) {
            response.put("success", true);
            response.put("data", user);
            response.put("message", "获取用户成功");
        } else {
            response.put("success", false);
            response.put("message", "用户不存在");
        }
        response.put("time", endTime - startTime);
        
        // 添加缓存状态头信息（模拟，实际应该从拦截器或AOP中获取）
        response.put("fromCache", endTime - startTime < 50); // 假设小于50ms表示从缓存获取
        
        return ResponseEntity.ok(response);
    }

    // 获取所有用户
    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        long startTime = System.currentTimeMillis();
        List<User> users = userService.getAllUsers();
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", users);
        response.put("message", "获取用户列表成功");
        response.put("time", endTime - startTime);
        
        // 添加缓存状态头信息（模拟）
        response.put("fromCache", endTime - startTime < 100); // 假设小于100ms表示从缓存获取
        
        return ResponseEntity.ok(response);
    }

    // 添加用户
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody User user) {
        User savedUser = userService.addUser(user);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", savedUser);
        response.put("message", "添加用户成功");
        return ResponseEntity.ok(response);
    }

    // 更新用户
    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        Map<String, Object> response = new HashMap<>();
        if (updatedUser != null) {
            response.put("success", true);
            response.put("data", updatedUser);
            response.put("message", "更新用户成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("success", true);
            response.put("message", "删除用户成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 清除缓存
    @PostMapping("/clear-cache")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCache(@RequestParam(required = false) Long id) {
        userService.clearCache(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", id != null ? "用户缓存已清除" : "所有缓存已清除");
        return ResponseEntity.ok(response);
    }

    // 缓存性能测试
    @GetMapping("/performance-test/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> performanceTest(@PathVariable Long id) {
        // 第一次调用（应该从数据库获取）
        long firstStartTime = System.currentTimeMillis();
        User firstUser = userService.getUserById(id);
        long firstEndTime = System.currentTimeMillis();
        
        // 第二次调用（应该从缓存获取）
        long secondStartTime = System.currentTimeMillis();
        User secondUser = userService.getUserById(id);
        long secondEndTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
                "firstCallTime", firstEndTime - firstStartTime,
                "secondCallTime", secondEndTime - secondStartTime,
                "improvement", (firstEndTime - firstStartTime) - (secondEndTime - secondStartTime),
                "improvementPercentage", (firstEndTime - firstStartTime) > 0 ? 
                        String.format("%.2f%%", 100 - ((double)(secondEndTime - secondStartTime) / (firstEndTime - firstStartTime) * 100)) : "0%"
        ));
        response.put("message", "性能测试完成");
        
        return ResponseEntity.ok(response);
    }
}