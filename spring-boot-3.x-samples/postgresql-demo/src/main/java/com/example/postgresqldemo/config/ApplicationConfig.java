package com.example.postgresqldemo.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 应用程序配置类，用于在启动时打印访问地址
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public ApplicationRunner appInfoPrinter(Environment environment, ServerProperties serverProperties) {
        return args -> {
            // 获取服务器端口
            int port = serverProperties.getPort();
            
            // 获取上下文路径
            String contextPath = serverProperties.getServlet().getContextPath();
            if (contextPath == null || contextPath.isEmpty()) {
                contextPath = "/";
            }
            
            try {
                // 获取本机地址
                InetAddress address = InetAddress.getLocalHost();
                String hostAddress = address.getHostAddress();
                String hostName = address.getHostName();
                
                // 构建访问URL
                String baseUrl = "http://" + hostAddress + ":" + port + contextPath;
                
                // 打印访问信息
                System.out.println("===========================================");
                System.out.println("应用程序已成功启动！");
                System.out.println("访问地址：");
                System.out.println("  - 本地地址: http://localhost:" + port + contextPath);
                System.out.println("  - 本机地址: " + baseUrl);
                System.out.println("===========================================");
                
            } catch (UnknownHostException e) {
                System.out.println("===========================================");
                System.out.println("应用程序已成功启动！");
                System.out.println("访问地址：http://localhost:" + port + contextPath);
                System.out.println("===========================================");
            }
        };
    }
}