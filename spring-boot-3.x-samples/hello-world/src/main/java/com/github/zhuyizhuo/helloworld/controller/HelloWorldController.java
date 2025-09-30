package com.github.zhuyizhuo.helloworld.controller;

import com.github.zhuyizhuo.helloworld.dto.UserInfo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot 3.x!";
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", required = false) String name) {
        if (name == null || name.trim().isEmpty()) {
            name = "Guest";
        }
        return String.format("Hello, %s! Welcome to Spring Boot 3.x!", name);
    }
    
    /**
     * 测试多个参数传递
     */
    @GetMapping("/multi-params")
    public String multiParams(
            @RequestParam(value = "name", required = false, defaultValue = "Unknown") String name,
            @RequestParam(value = "age", required = false, defaultValue = "0") int age,
            @RequestParam(value = "city", required = false, defaultValue = "Unknown") String city) {
        return String.format("Hello %s! You are %d years old and from %s.", name, age, city);
    }
    
    /**
     * 测试对象参数传递（GET请求，参数自动绑定到对象）
     */
    @GetMapping("/user-info-get")
    public String userInfoGet(UserInfo userInfo) {
        if (userInfo.getUsername() == null || userInfo.getUsername().trim().isEmpty()) {
            userInfo.setUsername("Guest");
        }
        return String.format("User Info: %s", userInfo);
    }
    
    /**
     * 测试对象参数传递（POST请求，JSON格式）
     */
    @PostMapping("/user-info-post")
    public String userInfoPost(@RequestBody UserInfo userInfo) {
        if (userInfo.getUsername() == null || userInfo.getUsername().trim().isEmpty()) {
            userInfo.setUsername("Guest");
        }
        return String.format("Received User Info (POST): %s", userInfo);
    }

}