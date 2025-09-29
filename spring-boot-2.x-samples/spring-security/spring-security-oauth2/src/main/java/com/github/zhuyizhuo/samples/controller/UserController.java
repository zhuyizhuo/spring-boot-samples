package com.github.zhuyizhuo.samples.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户API控制器
 * 需要USER角色才能访问
 * @author zhuo
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    public Authentication profile() {
        // 获取当前认证用户信息
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/data")
    public String userData() {
        return "This is user-specific data.";
    }
}