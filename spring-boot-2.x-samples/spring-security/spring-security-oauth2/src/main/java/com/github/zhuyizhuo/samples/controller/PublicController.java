package com.github.zhuyizhuo.samples.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共API控制器
 * 无需认证即可访问
 * @author zhuo
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from public API!";
    }

    @GetMapping("/info")
    public String info() {
        return "This is public information.";
    }
}