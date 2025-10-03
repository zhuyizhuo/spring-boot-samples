package com.github.zhuyizhuo.springai.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringAiApplication {

    @Value("${server.port}")
    private String serverPort;

    public static void main(String[] args) {
        SpringApplication.run(SpringAiApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println("Spring AI Demo Application Started!");
            System.out.println("Server running on port: " + serverPort);
            System.out.println("API Documentation: http://localhost:" + serverPort + "/swagger-ui.html");
        };
    }
}