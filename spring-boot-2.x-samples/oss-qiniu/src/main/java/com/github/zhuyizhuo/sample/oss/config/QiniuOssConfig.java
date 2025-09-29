package com.github.zhuyizhuo.sample.oss.config;

import com.qiniu.storage.Region;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 注意：避免与Spring的Configuration注解冲突，这里使用完全限定名引用七牛云配置类

/**
 * 七牛云OSS配置类
 */
@Configuration
@ConfigurationProperties(prefix = "qiniu.oss")
@Data
public class QiniuOssConfig {

    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String domain;
    private String region;

    /**
     * 创建七牛云OSS客户端配置
     */
    @Bean
    public com.qiniu.storage.Configuration qiniuConfiguration() {
        // 根据配置的区域创建对应的Region对象
        Region qiniuRegion;
        switch (region.toLowerCase()) {
            case "huadong":
                qiniuRegion = Region.huadong();
                break;
            case "huabei":
                qiniuRegion = Region.huabei();
                break;
            case "huanan":
                qiniuRegion = Region.huanan();
                break;
            case "beimei":
                qiniuRegion = Region.beimei();
                break;
            case "xinjiapo":
                qiniuRegion = Region.xinjiapo();
                break;
            default:
                // 默认使用华东区域
                qiniuRegion = Region.huadong();
        }
        return new com.qiniu.storage.Configuration(qiniuRegion);
    }

    /**
     * 创建七牛云认证对象
     */
    @Bean
    public Auth qiniuAuth() {
        // 验证必要的配置参数
        if (accessKey == null || accessKey.trim().isEmpty()) {
            throw new IllegalArgumentException("七牛云Access Key不能为空！请在application.properties中配置qiniu.oss.access-key");
        }
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("七牛云Secret Key不能为空！请在application.properties中配置qiniu.oss.secret-key");
        }
        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new IllegalArgumentException("七牛云存储空间名称不能为空！请在application.properties中配置qiniu.oss.bucket-name");
        }
        if (domain == null || domain.trim().isEmpty()) {
            throw new IllegalArgumentException("七牛云域名不能为空！请在application.properties中配置qiniu.oss.domain");
        }
        
        return Auth.create(accessKey, secretKey);
    }

    /**
     * 构建公开空间文件URL
     */
    public String getFileUrl(String fileName) {
        return domain + "/" + fileName;
    }
}