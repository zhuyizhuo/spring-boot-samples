package com.github.zhuyizhuo.sample.mybatis.plus.advanced;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
@MapperScan("com.github.zhuyizhuo.sample.mybatis.plus.advanced.mapper")
public class MybatisPlusAdvancedApplication implements ApplicationListener<WebServerInitializedEvent> {

    private static int serverPort;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MybatisPlusAdvancedApplication.class);
        app.setBannerMode(Banner.Mode.CONSOLE);
        app.run(args);
        
        // 打印应用访问地址信息
        System.out.println("\n========================================");
        System.out.println("应用已成功启动！");
        System.out.println("API测试页面：http://localhost:" + serverPort + "/api-test.html");
        System.out.println("RESTful API接口：http://localhost:" + serverPort + "/api/users");
        System.out.println("========================================\n");
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        serverPort = event.getWebServer().getPort();
    }
}