package com.github.zhuyizhuo.sample.minio.config;

import com.github.zhuyizhuo.sample.minio.service.MinioService;
import com.github.zhuyizhuo.sample.minio.service.impl.MinioServiceMockImpl;
import com.github.zhuyizhuo.sample.minio.service.impl.MinioServiceImpl;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO服务配置类
 * 用于配置使用实际的MinIO服务还是模拟的MinIO服务
 */
@Configuration
public class MinioServiceConfig {

    private static final Logger logger = LoggerFactory.getLogger(MinioServiceConfig.class);

    @Autowired
    private MinioConfig minioConfig;

    /**
     * 配置实际的MinIO服务实现
     * 当minio.mock.enabled=false时使用
     */
    @Bean("minioService")
    @ConditionalOnProperty(name = "minio.mock.enabled", havingValue = "false", matchIfMissing = true)
    public MinioService minioServiceImpl(MinioClient minioClient) {
        // 尝试连接MinIO服务器，检查连接是否正常
        boolean isConnected = checkMinioConnection(minioClient);
        if (isConnected) {
            logger.info("使用实际的MinIO服务实现，已成功连接到MinIO服务器: {}", minioConfig.getEndpoint());
            return new MinioServiceImpl();
        } else {
            // 如果连接失败，自动切换到模拟实现
            logger.warn("无法连接到MinIO服务器: {}, 自动切换到模拟实现", minioConfig.getEndpoint());
            return new MinioServiceMockImpl();
        }
    }

    /**
     * 配置模拟的MinIO服务实现
     * 当minio.mock.enabled=true时使用
     */
    @Bean("minioService")
    @ConditionalOnProperty(name = "minio.mock.enabled", havingValue = "true")
    public MinioService minioServiceMockImpl() {
        logger.info("使用模拟的MinIO服务实现");
        return new MinioServiceMockImpl();
    }

    /**
     * 检查MinIO服务器连接是否正常
     */
    private boolean checkMinioConnection(MinioClient minioClient) {
        try {
            // 尝试调用MinIO客户端的简单方法来验证连接
            minioClient.listBuckets();
            logger.debug("MinIO服务器连接检查成功");
            return true;
        } catch (Exception e) {
            logger.error("MinIO服务器连接检查失败: {}", e.getMessage());
            return false;
        }
    }
}