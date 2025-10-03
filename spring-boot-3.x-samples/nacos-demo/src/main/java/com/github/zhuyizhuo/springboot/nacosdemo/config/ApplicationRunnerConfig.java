package com.github.zhuyizhuo.springboot.nacosdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 应用启动时配置类，用于打印访问地址
 */
@Slf4j
@Component
public class ApplicationRunnerConfig implements ApplicationRunner {

    private final Environment environment;

    public ApplicationRunnerConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        printAccessUrls();
    }

    private void printAccessUrls() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String hostAddress = address.getHostAddress();
            String port = environment.getProperty("server.port", "8080");
            String contextPath = environment.getProperty("server.servlet.context-path", "");

            log.info("\n----------------------------------------------------------\n" +
                    "\t应用 '{}' 已启动！\n" +
                    "\t本地访问地址: \thttp://localhost:{}{}\n" +
                    "\t网络访问地址: \thttp://{}:{}{}\n" +
                    "\tSwagger文档: \thttp://localhost:{}{}/swagger-ui.html\n" +
                    "\tAPI文档: \thttp://localhost:{}{}/v3/api-docs\n" +
                    "----------------------------------------------------------",
                    environment.getProperty("spring.application.name"),
                    port, contextPath,
                    hostAddress, port, contextPath,
                    port, contextPath,
                    port, contextPath);
        } catch (UnknownHostException e) {
            log.error("获取主机信息失败", e);
            // 即使获取失败，也打印基本的访问地址
            log.info("\n----------------------------------------------------------\n" +
                    "\t应用 '{}' 已启动！\n" +
                    "\t本地访问地址: \thttp://localhost:{}\n" +
                    "\tSwagger文档: \thttp://localhost:{}/swagger-ui.html\n" +
                    "\tAPI文档: \thttp://localhost:{}/v3/api-docs\n" +
                    "----------------------------------------------------------",
                    environment.getProperty("spring.application.name", "nacos-demo"),
                    environment.getProperty("server.port", "8080"),
                    environment.getProperty("server.port", "8080"),
                    environment.getProperty("server.port", "8080"));
        }
    }
}