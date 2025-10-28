package com.github.zhuyizhuo.springboot.nacosdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Nacos配置示例类
 * 使用@RefreshScope注解支持动态刷新配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "nacos.demo.config")
@RefreshScope
public class NacosDemoConfig {
    private String name;
    private String description;
    private String version;
    private boolean enabled;
    private int timeout;
    private int maxConnections;
}