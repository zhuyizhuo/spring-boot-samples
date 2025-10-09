package com.github.zhuyizhuo.datajpa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(
            @Value("${server.port}") String serverPort,
            @Value("${server.servlet.context-path}") String contextPath) {
        return args -> {
            System.out.println("\n==========================================");
            System.out.println("Spring Boot Data JPA 项目启动成功！");
            System.out.println("访问地址:");
            System.out.println("  首页: http://localhost:" + serverPort + contextPath + "/");
            System.out.println("  用户API: http://localhost:" + serverPort + contextPath + "/api/users");
            System.out.println("  Swagger UI: http://localhost:" + serverPort + contextPath + "/swagger-ui.html");
            System.out.println("  API文档: http://localhost:" + serverPort + contextPath + "/api-docs");
            System.out.println("==========================================\n");
        };
    }

}