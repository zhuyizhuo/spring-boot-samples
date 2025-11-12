package com.github.zhuyizhuo.sentry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * Spring Boot 3.x 集成 Sentry 示例应用
 */
@SpringBootApplication
public class SentryDemoApplication implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(SentryDemoApplication.class);
    
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(SentryDemoApplication.class, args);
    }
    
    @Override
    public void run(ApplicationArguments args) {
        // 获取端口号，默认为8080
        String port = environment.getProperty("server.port", "8080");
        // 获取上下文路径，默认为空
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        
        // 构建访问地址
        String baseUrl = "http://localhost:" + port + contextPath;
        String indexUrl = baseUrl + "/";
        String apiUrl = baseUrl + "/api/sentry/";
        
        logger.info("========================================");
        logger.info("应用已成功启动！");
        logger.info("访问页面地址: {}", indexUrl);
        logger.info("API接口基础地址: {}", apiUrl);
        logger.info("可访问的接口示例:");
        logger.info("- 正常接口: {}{}", apiUrl, "normal");
        logger.info("- 异常测试: {}{}", apiUrl, "exception");
        logger.info("- 手动异常: {}{}", apiUrl, "manual-exception");
        logger.info("- 性能测试: {}{}", apiUrl, "performance");
        logger.info("========================================");
    }

}