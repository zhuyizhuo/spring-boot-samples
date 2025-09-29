package com.github.zhuyizhuo.sample.oss;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.github.zhuyizhuo.sample.oss.config.QiniuOssConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 七牛云OSS接入示例启动类
 */
@SpringBootApplication
@EnableConfigurationProperties({QiniuOssConfig.class})
public class QiniuOssApplication {

    public static void main(String[] args) {
        SpringApplication.run(QiniuOssApplication.class, args);
    }

    /**
     * 应用启动前检查七牛云配置
     */
    @Bean
    public CommandLineRunner checkQiniuConfig(QiniuOssConfig qiniuOssConfig) {
        return args -> {
            System.out.println("\n正在检查七牛云OSS配置...");
            
            // 检查配置文件是否存在
            String configFile = System.getProperty("user.dir") + "/src/main/resources/application.properties";
            
            // 检查关键配置参数
            boolean configValid = true;
            if (qiniuOssConfig.getAccessKey() == null || qiniuOssConfig.getAccessKey().trim().isEmpty()) {
                System.out.println("[警告] 七牛云Access Key未配置！");
                configValid = false;
            }
            if (qiniuOssConfig.getSecretKey() == null || qiniuOssConfig.getSecretKey().trim().isEmpty()) {
                System.out.println("[警告] 七牛云Secret Key未配置！");
                configValid = false;
            }
            if (qiniuOssConfig.getBucketName() == null || qiniuOssConfig.getBucketName().trim().isEmpty()) {
                System.out.println("[警告] 七牛云存储空间名称未配置！");
                configValid = false;
            }
            if (qiniuOssConfig.getDomain() == null || qiniuOssConfig.getDomain().trim().isEmpty()) {
                System.out.println("[警告] 七牛云域名未配置！");
                configValid = false;
            }
            
            if (!configValid) {
                System.out.println("\n[重要提示] 请按照以下步骤配置七牛云OSS：");
                System.out.println("1. 复制 src/main/resources/application.properties.example 并重命名为 application.properties");
                System.out.println("2. 填写您的七牛云Access Key、Secret Key、存储空间名称和域名");
                System.out.println("3. 重新启动应用\n");
            }
        };
    }

    /**
     * 监听Web服务器初始化事件，打印应用访问地址
     */
    @Component
    public static class ServerInfoLogger implements ApplicationListener<WebServerInitializedEvent> {

        @Override
        public void onApplicationEvent(WebServerInitializedEvent event) {
            try {
                String hostAddress = InetAddress.getLocalHost().getHostAddress();
                int port = event.getWebServer().getPort();
                String contextPath = event.getApplicationContext().getApplicationName();
                
                System.out.println();
                System.out.println("==============================================================");
                System.out.println("七牛云OSS接入示例应用已成功启动！");
                System.out.println("访问地址：");
                System.out.println("  本地访问：http://localhost:" + port + contextPath + "/upload.html");
                System.out.println("  网络访问：http://" + hostAddress + ":" + port + contextPath + "/upload.html");
                System.out.println("  API接口：http://localhost:" + port + contextPath + "/api/qiniu-oss");
                System.out.println("==============================================================");
                System.out.println();
            } catch (UnknownHostException e) {
                System.out.println("无法获取服务器地址信息：" + e.getMessage());
            }
        }
    }
}