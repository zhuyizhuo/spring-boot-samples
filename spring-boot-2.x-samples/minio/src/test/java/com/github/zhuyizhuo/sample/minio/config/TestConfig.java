package com.github.zhuyizhuo.sample.minio.config;

import io.minio.MinioClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public MinioClient minioClient() {
        // 创建MinioClient的Mock对象，用于单元测试
        return Mockito.mock(MinioClient.class);
    }
    
    @Bean
    @Primary
    public MinioConfig minioConfig() {
        // 创建测试用的MinioConfig对象
        MinioConfig minioConfig = new MinioConfig();
        minioConfig.setEndpoint("http://localhost:9000");
        minioConfig.setAccessKey("minioadmin");
        minioConfig.setSecretKey("minioadmin");
        minioConfig.setBucketName("test");
        minioConfig.setDomain("http://localhost:9000/");
        minioConfig.setDirPrefix("upload/");
        return minioConfig;
    }
}