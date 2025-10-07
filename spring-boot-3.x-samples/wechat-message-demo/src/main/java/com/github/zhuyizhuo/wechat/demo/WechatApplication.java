package com.github.zhuyizhuo.wechat.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.ApplicationRunner;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class WechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatApplication.class, args);
    }
    
    @Bean
    public ApplicationRunner printTestPageUrl() {
        return args -> {
            try {
                String host = InetAddress.getLocalHost().getHostAddress();
                System.out.println("\n==========================================================");
                System.out.println("应用已成功启动！");
                System.out.println("测试页面地址: http://localhost:8080/");
                System.out.println("局域网访问地址: http://" + host + ":8080/");
                System.out.println("==========================================================\n");
            } catch (UnknownHostException e) {
                System.out.println("\n==========================================================");
                System.out.println("应用已成功启动！");
                System.out.println("测试页面地址: http://localhost:8080/");
                System.out.println("==========================================================\n");
            }
        };
    }

}