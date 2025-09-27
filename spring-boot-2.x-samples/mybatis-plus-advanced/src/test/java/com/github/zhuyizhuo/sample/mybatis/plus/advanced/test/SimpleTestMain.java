package com.github.zhuyizhuo.sample.mybatis.plus.advanced.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 简单的测试主类，用于直接运行测试而不依赖测试框架
 */
public class SimpleTestMain {
    public static void main(String[] args) {
        System.out.println("开始运行简单测试...");
        
        try {
            // 创建Spring Boot应用
            SpringApplication app = new SpringApplication(com.github.zhuyizhuo.sample.mybatis.plus.advanced.MybatisPlusAdvancedApplication.class);
            app.setWebApplicationType(WebApplicationType.NONE); // 不启动Web服务器
            
            // 启动Spring上下文
            ConfigurableApplicationContext context = app.run(args);
            
            System.out.println("✅ Spring上下文启动成功!");
            System.out.println("Spring容器中的Bean数量: " + context.getBeanDefinitionCount());
            
            // 检查关键Bean是否存在
            boolean hasDataSource = context.containsBean("dataSource");
            boolean hasUserMapper = context.containsBean("userMapper");
            boolean hasUserService = context.containsBean("userServiceImpl");
            
            System.out.println("数据源Bean存在: " + hasDataSource);
            System.out.println("UserMapper存在: " + hasUserMapper);
            System.out.println("UserService存在: " + hasUserService);
            
            // 关闭上下文
            context.close();
            System.out.println("✅ Spring上下文已关闭");
            
        } catch (Exception e) {
            System.out.println("❌ Spring上下文启动失败!");
            e.printStackTrace();
        }
        
        System.out.println("测试完成。");
    }
}