package com.github.zhuyizhuo.minio.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class MinioDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinioDemoApplication.class, args);
    }

    // 打印测试页面访问地址
    @org.springframework.context.annotation.Bean
    public ApplicationListener<ContextRefreshedEvent> printTestPageUrl() {
        return event -> {
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                String ipAddress = localHost.getHostAddress();
                int port = 8080; // 默认端口，可根据实际配置调整

                System.out.println("\n=== MinIO Demo 应用启动成功 ===");
                System.out.println("测试页面访问地址:");
                System.out.println("  - Local: http://localhost:" + port + "/");
                System.out.println("  - Network: http://" + ipAddress + ":" + port + "/");
                System.out.println("Swagger API文档: http://localhost:" + port + "/swagger-ui.html");
                System.out.println("请确保已正确配置MinIO服务器连接信息");
                System.out.println("============================\n");
            } catch (UnknownHostException e) {
                System.out.println("\n=== MinIO Demo 应用启动成功 ===");
                System.out.println("测试页面访问地址: http://localhost:8080/");
                System.out.println("Swagger API文档: http://localhost:8080/swagger-ui.html");
                System.out.println("请确保已正确配置MinIO服务器连接信息");
                System.out.println("(无法获取局域网IP地址: " + e.getMessage() + ")");
                System.out.println("============================\n");
            }
        };
    }
}