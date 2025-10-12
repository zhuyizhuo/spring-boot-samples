package com.github.zhuyizhuo.minio.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 主页控制器，用于将根路径重定向到测试页面
 */
@Controller
public class HomeController {

    /**
     * 处理根路径请求，重定向到MinIO测试页面
     * @return 重定向到minio-test.html
     */
    @GetMapping("/")
    public String redirectToTestPage() {
        return "redirect:/minio-test.html";
    }
}