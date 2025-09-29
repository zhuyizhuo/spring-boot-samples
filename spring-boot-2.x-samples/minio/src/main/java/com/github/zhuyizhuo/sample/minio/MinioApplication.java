package com.github.zhuyizhuo.sample.minio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot集成MinIO示例应用主入口
 */
@SpringBootApplication
public class MinioApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MinioApplication.class, args);
    }
    
    /**
     * 应用启动成功后在控制台打印前端访问地址
     */
    @Bean
    public CommandLineRunner printFrontendUrl() {
        return args -> {
            System.out.println("\n=========================================");
            System.out.println("\tMinIO文件服务已启动成功！");
            System.out.println("\t前端访问地址: http://localhost:8081/index.html");
            System.out.println("=========================================\n");
        };
    }
}