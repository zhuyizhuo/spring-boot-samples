package com.github.zhuyizhuo.dingtalk.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DingTalkMessageApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DingTalkMessageApplication.class);
        
        // 添加启动完成监听器，输出访问页面信息
        app.addListeners((ApplicationListener<ApplicationStartedEvent>) event -> {
            Environment env = event.getApplicationContext().getEnvironment();
            String protocol = "http";
            String port = env.getProperty("server.port", "8080");
            String contextPath = env.getProperty("server.servlet.context-path", "");
            
            System.out.println("\n----------------------------------------------------------");
            System.out.println("\t应用已成功启动！访问地址：");
            System.out.println("\tSwagger API文档: \t" + protocol + "://localhost:" + port + contextPath + "/swagger-ui.html");
            System.out.println("\t前端测试页面: \t" + protocol + "://localhost:" + port + contextPath + "/dingtalk-message-test.html");
            System.out.println("----------------------------------------------------------\n");
        });
        
        app.run(args);
    }
}