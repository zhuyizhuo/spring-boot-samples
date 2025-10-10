package com.github.zhuyizhuo.mybatisplus.controller;

import com.github.zhuyizhuo.mybatisplus.model.User;
import com.github.zhuyizhuo.mybatisplus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/user-management")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String userManagement(Model model) {
        // 查询所有用户
        List<User> users = userService.list();
        model.addAttribute("users", users);
        return "user-management";
    }

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user-form";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user) {
        // 设置创建和更新时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(1); // 默认启用
        userService.save(user);
        return "redirect:/user-management";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "user-form";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user) {
        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);
        return "redirect:/user-management";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return "redirect:/user-management";
    }

    @GetMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Long id) {
        User user = userService.getById(id);
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);
        return "redirect:/user-management";
    }
}