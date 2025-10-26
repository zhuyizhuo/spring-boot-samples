package com.github.zhuyizhuo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Sentinel 示例应用入口
 */
@SpringBootApplication
public class SentinelApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SentinelApplication.class, args);
        printAccessUrl(context);
    }
    
    /**
     * 打印应用访问地址
     */
    private static void printAccessUrl(ConfigurableApplicationContext context) {
        Environment env = context.getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            
            System.out.println();
            System.out.println("==================================================");
            System.out.println("                  Sentinel 演示应用                  ");
            System.out.println("==================================================");
            System.out.println("前端测试页面访问地址:");
            System.out.println("  http://localhost:" + port + contextPath + "/");
            System.out.println("  http://" + host + ":" + port + contextPath + "/");
            System.out.println();
            System.out.println("API 接口测试地址:");
            System.out.println("  用户列表: http://localhost:" + port + contextPath + "/api/users");
            System.out.println("  用户详情: http://localhost:" + port + contextPath + "/api/users/{userId}");
            System.out.println("  注解限流: http://localhost:" + port + contextPath + "/api/users/annotation/{userId}");
            System.out.println("  编程限流: http://localhost:" + port + contextPath + "/api/users/programmatic/{userId}");
            System.out.println("==================================================");
            System.out.println();
        } catch (UnknownHostException e) {
            // 如果无法获取本机IP，只打印localhost地址
            System.out.println();
            System.out.println("==================================================");
            System.out.println("                  Sentinel 演示应用                  ");
            System.out.println("==================================================");
            System.out.println("前端测试页面访问地址:");
            System.out.println("  http://localhost:" + port + contextPath + "/");
            System.out.println();
            System.out.println("API 接口测试地址:");
            System.out.println("  用户列表: http://localhost:" + port + contextPath + "/api/users");
            System.out.println("  用户详情: http://localhost:" + port + contextPath + "/api/users/{userId}");
            System.out.println("  注解限流: http://localhost:" + port + contextPath + "/api/users/annotation/{userId}");
            System.out.println("  编程限流: http://localhost:" + port + contextPath + "/api/users/programmatic/{userId}");
            System.out.println("==================================================");
            System.out.println();
        }
    }

}