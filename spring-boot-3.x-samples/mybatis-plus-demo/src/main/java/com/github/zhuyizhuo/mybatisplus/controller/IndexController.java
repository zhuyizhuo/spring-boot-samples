package com.github.zhuyizhuo.mybatisplus.controller;

import com.github.zhuyizhuo.mybatisplus.model.User;
import com.github.zhuyizhuo.mybatisplus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model) {
        // 查询前10个用户用于展示
        List<User> users = userService.getUsersByPage(1, 10);
        model.addAttribute("users", users);
        
        // 查询所有用户及其角色信息
        List<User> usersWithRoles = userService.getAllUsersWithRoles();
        model.addAttribute("usersWithRoles", usersWithRoles);
        
        // 添加一些统计信息
        long totalUsers = userService.count();
        model.addAttribute("totalUsers", totalUsers);
        
        return "index";
    }

    @GetMapping("/api")
    public String apiDocs() {
        // 重定向到Swagger UI页面
        return "redirect:/mybatis-plus/swagger-ui.html";
    }

    @GetMapping("/h2")
    public String h2Console() {
        // 重定向到H2控制台
        return "redirect:/mybatis-plus/h2-console";
    }

}