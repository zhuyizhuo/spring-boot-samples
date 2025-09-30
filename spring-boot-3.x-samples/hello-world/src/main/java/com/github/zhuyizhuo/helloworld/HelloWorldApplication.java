package com.github.zhuyizhuo.helloworld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HelloWorldApplication {

    @Value("${server.port}")
    private String serverPort;
    
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }
    
    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println("\n===========================================");
            System.out.println("Spring Boot 3.x 示例应用已启动成功！");
            System.out.println("===========================================");
            System.out.println("访问地址：");
            System.out.println("  - 测试页面: http://localhost:" + serverPort + contextPath + "/index.html");
            System.out.println("  - API文档: http://localhost:" + serverPort + contextPath + "/swagger-ui.html");
            System.out.println("===========================================");
        };
    }

}