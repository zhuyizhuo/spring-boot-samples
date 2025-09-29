package com.github.zhuyizhuo.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zhuo
 */
@SpringBootApplication
public class SpringSecurityOAuth2Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringSecurityOAuth2Application.class, args);
        System.out.println("\n----------------------------------------------------------");
        System.out.println("\tspring-security-oauth2 已启动成功！");
        System.out.println("----------------------------------------------------------");
        System.out.println("\n访问地址:");
        System.out.println("\t测试页面: \thttp://localhost:8085/oauth2/oauth2-test.html");
        System.out.println("----------------------------------------------------------\n");
    }
}