package com.example.ehcache.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 * 提供测试页面的访问入口
 */
@Controller
public class HomeController {

    /**
     * 访问首页，展示测试页面
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}