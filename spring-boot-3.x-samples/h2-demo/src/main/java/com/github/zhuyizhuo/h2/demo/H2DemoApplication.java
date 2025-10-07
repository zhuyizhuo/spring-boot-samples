package com.github.zhuyizhuo.h2.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class H2DemoApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(H2DemoApplication.class);
        application.run(args);
    }

    // 打印测试页面访问地址
    @org.springframework.context.annotation.Bean
    public ApplicationListener<ContextRefreshedEvent> printTestPageUrl() {
        return event -> {
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                String ipAddress = localHost.getHostAddress();
                int port = 8080; // 默认端口，可根据实际配置调整

                System.out.println("\n=== H2 Demo 应用启动成功 ===");
                System.out.println("测试页面访问地址:");
                System.out.println("  - Local: http://localhost:" + port + "/");
                System.out.println("  - Network: http://" + ipAddress + ":" + port + "/");
                System.out.println("H2 控制台访问地址: http://localhost:" + port + "/h2-console");
                System.out.println("用户名: sa, 密码: 空");
                System.out.println("============================\n");
            } catch (UnknownHostException e) {
                System.out.println("\n=== H2 Demo 应用启动成功 ===");
                System.out.println("测试页面访问地址: http://localhost:8080/");
                System.out.println("H2 控制台访问地址: http://localhost:8080/h2-console");
                System.out.println("用户名: sa, 密码: 空");
                System.out.println("(无法获取局域网IP地址: " + e.getMessage() + ")");
                System.out.println("============================\n");
            }
        };
    }
}