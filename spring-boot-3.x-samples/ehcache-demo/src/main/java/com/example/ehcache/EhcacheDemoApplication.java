package com.example.ehcache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Spring Boot应用程序入口类
 * 启用缓存并配置应用启动时打印访问地址
 */
@SpringBootApplication
@EnableCaching
public class EhcacheDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EhcacheDemoApplication.class, args);
    }

    /**
     * 应用启动后执行，打印访问地址
     */
    @Bean
    public ApplicationRunner applicationRunner(Environment environment) {
        return args -> {
            try {
                String hostAddress = InetAddress.getLocalHost().getHostAddress();
                String port = environment.getProperty("server.port", "8080");
                String contextPath = environment.getProperty("server.servlet.context-path", "");
                
                System.out.println();
                System.out.println("===========================================");
                System.out.println("应用已启动成功！");
                System.out.println("访问地址：http://" + hostAddress + ":" + port + contextPath);
                System.out.println("测试页面：http://" + hostAddress + ":" + port + contextPath + "/");
                System.out.println("API文档：http://" + hostAddress + ":" + port + contextPath + "/api/users");
                System.out.println("===========================================");
                System.out.println();
            } catch (UnknownHostException e) {
                // 如果获取主机地址失败，使用localhost
                String port = environment.getProperty("server.port", "8080");
                String contextPath = environment.getProperty("server.servlet.context-path", "");
                
                System.out.println();
                System.out.println("===========================================");
                System.out.println("应用已启动成功！");
                System.out.println("访问地址：http://localhost:" + port + contextPath);
                System.out.println("测试页面：http://localhost:" + port + contextPath + "/");
                System.out.println("API文档：http://localhost:" + port + contextPath + "/api/users");
                System.out.println("===========================================");
                System.out.println();
            }
        };
    }
}