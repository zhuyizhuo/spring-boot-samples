package com.github.zhuyizhuo.springboot.redispubsub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 应用启动配置类，用于在应用启动时打印访问地址信息
 * 
 * @author zhuo
 */
@Configuration
public class ApplicationRunnerConfig {
    
    @Value("${server.port:8080}")
    private String port;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    
    @Bean
    public ApplicationRunner printApplicationInfo() {
        return (ApplicationArguments args) -> {
            // 构建基础URL
            String baseUrl = "http://localhost:" + port + contextPath;
            
            // 打印访问地址信息
            System.out.println("\n==========================================");
            System.out.println("应用已成功启动！");
            System.out.println("REST API前缀: " + baseUrl + "/api");
            System.out.println("HTML测试页面: " + baseUrl + "/redis-pubsub-test.html");
            System.out.println("Swagger文档地址: " + baseUrl + "/swagger-ui.html");
            System.out.println("==========================================\n");
        };
    }
}