package com.example.mapstructdemo.controller;

import com.example.mapstructdemo.dto.UserDTO;
import com.example.mapstructdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 首页 - 显示测试页面
    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    // API: 获取所有用户
    @GetMapping("/api/users")
    @ResponseBody
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // API: 根据ID获取用户
    @GetMapping("/api/users/{id}")
    @ResponseBody
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // API: 创建用户
    @PostMapping("/api/users")
    @ResponseBody
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    // API: 更新用户
    @PutMapping("/api/users/{id}")
    @ResponseBody
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    // API: 删除用户
    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public boolean deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    // 测试页面 - 展示MapStruct使用
    @GetMapping("/test")
    public String testPage(Model model) {
        List<UserDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "test";
    }
}