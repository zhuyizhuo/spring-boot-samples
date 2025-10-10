package com.github.zhuyizhuo.mybatisplus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.github.zhuyizhuo.mybatisplus.mapper")
public class MyBatisPlusDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MyBatisPlusDemoApplication.class, args);
        
        // 获取应用程序的端口号
        String port = context.getEnvironment().getProperty("server.port", "8080");
        String contextPath = context.getEnvironment().getProperty("server.servlet.context-path", "");
        
        // 打印访问地址
        System.out.println("\n==================== 应用启动成功 ====================");
        System.out.println("访问首页: http://localhost:" + port + contextPath + "/");
        System.out.println("用户管理: http://localhost:" + port + contextPath + "/user-management");
        System.out.println("API文档: http://localhost:" + port + contextPath + "/swagger-ui.html");
        System.out.println("H2控制台: http://localhost:" + port + contextPath + "/h2-console");
        System.out.println("====================================================\n");
    }

}