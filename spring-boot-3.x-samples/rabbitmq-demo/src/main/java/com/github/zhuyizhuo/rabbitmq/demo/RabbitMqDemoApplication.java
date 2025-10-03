package com.github.zhuyizhuo.rabbitmq.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

/**
 * Spring Boot 3.x RabbitMQ 集成示例应用
 * 
 * @author zhuyizhuo
 * @version 1.0
 * @date 2025/10/02
 */
@SpringBootApplication
public class RabbitMqDemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqDemoApplication.class);

    @Value("${server.port:8080}")
    private String port;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqDemoApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(Environment environment) {
        return args -> {
            try {
                String host = InetAddress.getLocalHost().getHostAddress();
                String serverPort = environment.getProperty("server.port", "8080");
                String context = environment.getProperty("server.servlet.context-path", "");
                
                // 确保 context-path 以 / 开头
                if (!context.startsWith("/") && !context.isEmpty()) {
                    context = "/" + context;
                }
                
                String baseUrl = "http://localhost:" + serverPort + context;
                String networkUrl = "http://" + host + ":" + serverPort + context;
                
                logger.info("\n" +
                    "==================================================\n" +
                    "🚀 RabbitMQ Demo 应用启动成功！\n" +
                    "==================================================\n" +
                    "📱 测试页面 (推荐): \n" +
                    "   本地访问: {}\n" +
                    "   网络访问: {}\n" +
                    "\n" +
                    "📖 API 文档 (Swagger): \n" +
                    "   本地访问: {}/swagger-ui.html\n" +
                    "   网络访问: {}/swagger-ui.html\n" +
                    "\n" +
                    "💊 健康检查: \n" +
                    "   本地访问: {}/actuator/health\n" +
                    "   网络访问: {}/actuator/health\n" +
                    "\n" +
                    "🔗 API 基础路径: \n" +
                    "   本地访问: {}/api/v1/messages\n" +
                    "   网络访问: {}/api/v1/messages\n" +
                    "\n" +
                    "💡 提示: \n" +
                    "   - 测试页面提供了完整的可视化界面\n" +
                    "   - API 文档包含详细的接口说明\n" +
                    "   - 健康检查可监控应用和 RabbitMQ 状态\n" +
                    "==================================================",
                    baseUrl, networkUrl,
                    baseUrl, networkUrl,
                    baseUrl, networkUrl,
                    baseUrl, networkUrl
                );
                
                // 在控制台也输出简化版本
                System.out.println("\n" +
                    "🌟 RabbitMQ Demo 启动成功！\n" +
                    "📱 测试页面: " + baseUrl + "\n" +
                    "📖 API 文档: " + baseUrl + "/swagger-ui.html\n" +
                    "💊 健康检查: " + baseUrl + "/actuator/health\n"
                );
                
            } catch (Exception e) {
                logger.error("获取网络地址失败", e);
                logger.info("\n" +
                    "==================================================\n" +
                    "🚀 RabbitMQ Demo 应用启动成功！\n" +
                    "==================================================\n" +
                    "📱 测试页面: http://localhost:{}{}\n" +
                    "📖 API 文档: http://localhost:{}{}/swagger-ui.html\n" +
                    "💊 健康检查: http://localhost:{}{}/actuator/health\n" +
                    "==================================================",
                    port, contextPath, port, contextPath, port, contextPath
                );
            }
        };
    }
}

