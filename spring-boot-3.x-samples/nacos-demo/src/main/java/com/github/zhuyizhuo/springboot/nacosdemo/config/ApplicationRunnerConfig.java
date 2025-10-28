package com.github.zhuyizhuo.springboot.nacosdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 应用启动时配置类，用于打印访问地址和Nacos配置信息
 */
@Slf4j
@Component
public class ApplicationRunnerConfig implements ApplicationRunner {

    private final Environment environment;
    private final NacosDemoConfig nacosDemoConfig;

    public ApplicationRunnerConfig(Environment environment, NacosDemoConfig nacosDemoConfig) {
        this.environment = environment;
        this.nacosDemoConfig = nacosDemoConfig;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        printAccessUrls();
        printNacosConfigInfo();
    }

    private void printAccessUrls() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String hostAddress = address.getHostAddress();
            String port = environment.getProperty("server.port", "8080");
            String contextPath = environment.getProperty("server.servlet.context-path", "");
            String appName = environment.getProperty("spring.application.name", "nacos-demo");

            log.info("\n=========================================================\n");
            log.info("🎉 应用 '{}' 启动成功！", appName);
            log.info("🌐 访问地址：");
            log.info("  - 本地访问: http://localhost:{}{}", port, contextPath);
            log.info("  - 网络访问: http://{}:{}{}", hostAddress, port, contextPath);
            log.info("  - Swagger文档: http://localhost:{}{}/swagger-ui.html", port, contextPath);
            log.info("  - API文档: http://localhost:{}{}/v3/api-docs", port, contextPath);
            log.info("=========================================================\n");
        } catch (UnknownHostException e) {
            log.warn("无法获取本机IP地址: {}", e.getMessage());
            String port = environment.getProperty("server.port", "8080");
            String appName = environment.getProperty("spring.application.name", "nacos-demo");
            
            log.info("\n=========================================================\n");
            log.info("🎉 应用 '{}' 启动成功！", appName);
            log.info("🌐 访问地址：http://localhost:{}", port);
            log.info("=========================================================\n");
        }
    }
    
    private void printNacosConfigInfo() {
        log.info("\n📊 Nacos配置中心信息：");
        log.info("  - 配置名称: {}", nacosDemoConfig.getName());
        log.info("  - 版本: {}", nacosDemoConfig.getVersion());
        log.info("  - 描述: {}", nacosDemoConfig.getDescription());
        log.info("  - 启用状态: {}", nacosDemoConfig.isEnabled() ? "已启用" : "已禁用");
        log.info("  - 超时时间: {}秒", nacosDemoConfig.getTimeout());
        log.info("  - 最大连接数: {}", nacosDemoConfig.getMaxConnections());
        log.info("\nℹ️  功能说明：");
        log.info("  - 本示例演示Nacos配置中心的使用");
        log.info("  - 支持配置动态刷新 @RefreshScope");
        log.info("  - 已禁用Nacos服务注册发现功能");
        log.info("\n💡 测试提示：");
        log.info("  - 在Nacos服务器上修改配置后，刷新页面即可看到最新配置");
        log.info("  - 配置项示例：nacos.demo.config.timeout=60");
        log.info("  - 访问首页可直观查看配置信息和动态变化");
        log.info("=========================================================\n");
    }
}