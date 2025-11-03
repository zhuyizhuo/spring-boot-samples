package com.example.mapstructdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupListener implements ApplicationRunner {

    @Value("${server.port:8080}")
    private String port;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Override
    public void run(ApplicationArguments args) {
        String baseUrl = "http://localhost:" + port + contextPath;
        System.out.println("======================================================================");
        System.out.println("应用已成功启动！");
        System.out.println("访问地址：");
        System.out.println("首页：" + baseUrl);
        System.out.println("演示页面：" + baseUrl + "/test");
        System.out.println("======================================================================");
    }
}