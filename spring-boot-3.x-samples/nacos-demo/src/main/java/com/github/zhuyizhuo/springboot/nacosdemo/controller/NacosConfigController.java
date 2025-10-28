package com.github.zhuyizhuo.springboot.nacosdemo.controller;

import com.github.zhuyizhuo.springboot.nacosdemo.config.NacosDemoConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 示例控制器
 * 演示Nacos配置中心的动态刷新功能
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RefreshScope
public class NacosConfigController {

    private final NacosDemoConfig nacosDemoConfig;

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
        result.put("configCenter", "Nacos Config");
        result.put("configEnabled", String.valueOf(nacosDemoConfig.isEnabled()));
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
        result.put("description", nacosDemoConfig.getDescription());
        result.put("features", new String[]{
            "健康检查",
            "服务信息展示",
            "Swagger API文档",
            "Nacos配置中心集成"
        });
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.put("version", nacosDemoConfig.getVersion());
        return result;
    }

    /**
     * 获取Nacos配置信息
     */
    @GetMapping("/api/config")
    public Map<String, Object> getConfigInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", nacosDemoConfig.getName());
        result.put("description", nacosDemoConfig.getDescription());
        result.put("version", nacosDemoConfig.getVersion());
        result.put("enabled", nacosDemoConfig.isEnabled());
        result.put("timeout", nacosDemoConfig.getTimeout());
        result.put("maxConnections", nacosDemoConfig.getMaxConnections());
        log.info("获取Nacos配置信息: {}", result);
        return result;
    }

    /**
     * 获取单个配置项
     */
    @GetMapping("/api/config/timeout")
    public Map<String, Object> getTimeoutConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("timeout", nacosDemoConfig.getTimeout());
        result.put("timestamp", System.currentTimeMillis());
        log.info("获取超时配置: {}", nacosDemoConfig.getTimeout());
        return result;
    }

    /**
     * 获取连接池配置
     */
    @GetMapping("/api/config/connections")
    public Map<String, Object> getConnectionConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("maxConnections", nacosDemoConfig.getMaxConnections());
        result.put("enabled", nacosDemoConfig.isEnabled());
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}