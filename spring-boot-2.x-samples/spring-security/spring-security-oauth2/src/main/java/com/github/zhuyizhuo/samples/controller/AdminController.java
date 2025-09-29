package com.github.zhuyizhuo.samples.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员API控制器
 * 需要ADMIN角色才能访问
 * @author zhuo
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Admin dashboard data.";
    }

    @GetMapping("/settings")
    public String settings() {
        return "System settings configuration.";
    }
}