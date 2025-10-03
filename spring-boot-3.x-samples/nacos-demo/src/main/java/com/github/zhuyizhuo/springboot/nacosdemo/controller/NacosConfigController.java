package com.github.zhuyizhuo.springboot.nacosdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 示例控制器
 */
@Slf4j
@RestController
public class NacosConfigController {

    /**
     * 健康检查接口
     * @return 健康状态
     */
    @GetMapping("/api/health")
    public Map<String, String> healthCheck() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "nacos-demo");
        result.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return result;
    }

    /**
     * 服务信息接口
     * @return 服务信息
     */
    @GetMapping("/api/service")
    public Map<String, Object> getServiceInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("serviceName", "nacos-demo");
        result.put("description", "Spring Boot 3.x 示例项目");
        result.put("features", new String[]{
            "健康检查",
            "服务信息展示",
            "Swagger API文档"
        });
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return result;
    }
}