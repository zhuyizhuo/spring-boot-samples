package com.github.zhuyizhuo.minio.demo.config;

import io.minio.MinioClient;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置类
 * 从配置文件中读取MinIO服务器连接信息
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {
    
    private static final Logger log = LoggerFactory.getLogger(MinioConfig.class);
    
    // MinIO服务器地址
    private String url;
    
    // MinIO访问密钥
    private String accessKey;
    
    // MinIO密钥
    private String secretKey;
    
    // 默认存储桶名称
    private String bucketName;
    
    // 区域设置
    private String region;
    
    // 启用SSL
    private boolean secure;
    
    /**
     * 创建MinIO客户端实例
     * @return MinIO客户端
     */
    @Bean
    public MinioClient minioClient() {
        // 检查必要配置是否存在
        validateMinioConfig();
        
        MinioClient.Builder builder = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey);
        
        // 如果配置了区域，则设置区域
        if (region != null && !region.isEmpty()) {
            builder.region(region);
        }
        
        // SSL配置已通过URL协议(http/https)自动处理
        // MinIO客户端会根据URL中的协议自动设置secure属性
        
        return builder.build();
    }
    
    /**
     * 验证MinIO配置是否有效
     */
    private void validateMinioConfig() {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("MinIO URL is not configured. Please set minio.url in application-minio.yml or MINIO_URL environment variable.");
        }
        
        if (accessKey == null || accessKey.isEmpty()) {
            throw new IllegalArgumentException("MinIO access key is not configured. Please set minio.accessKey in application-minio.yml or MINIO_ACCESS_KEY environment variable.");
        }
        
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("MinIO secret key is not configured. Please set minio.secretKey in application-minio.yml or MINIO_SECRET_KEY environment variable.");
        }
        
        if (bucketName == null || bucketName.isEmpty()) {
            log.warn("MinIO bucket name is not configured. Using default bucket name: 'minio-demo'");
            bucketName = "minio-demo";
        }
    }
}