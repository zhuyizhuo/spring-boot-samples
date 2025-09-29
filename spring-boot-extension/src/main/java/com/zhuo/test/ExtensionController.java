package com.zhuo.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 扩展点示例控制器
 * 提供简单的HTTP端点，解决Whitelabel Error Page问题
 */
@RestController
public class ExtensionController {
    
    /**
     * 根端点 - 提供应用信息
     */
    @GetMapping("/")
    public String home() {
        return "<h1>Spring Boot 扩展示例应用</h1>\n" +
               "<p>这是一个展示Spring Boot各种扩展点功能的示例应用</p>\n" +
               "<p>可访问的端点:</p>\n" +
               "<ul>\n" +
               "  <li><a href='/extension/hello'>/extension/hello</a> - 简单的问候消息</li>\n" +
               "  <li><a href='/extension/status'>/extension/status</a> - 应用状态信息</li>\n" +
               "</ul>";
    }
    
    /**
     * 简单的问候端点
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot Extension!";
    }
    
    /**
     * 应用状态端点
     */
    @GetMapping("/status")
    public String status() {
        return "{\"status\": \"UP\", \"message\": \"Spring Boot Extension is running normally\", \"version\": \"1.1.0\"}";
    }
}