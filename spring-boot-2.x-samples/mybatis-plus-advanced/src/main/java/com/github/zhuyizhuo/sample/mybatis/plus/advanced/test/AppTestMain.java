package com.github.zhuyizhuo.sample.mybatis.plus.advanced.test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.github.zhuyizhuo.sample.mybatis.plus.advanced.MybatisPlusAdvancedApplication;

/**
 * 简单的主类用于直接测试应用程序启动
 * 绕过测试框架，直接启动Spring Boot应用
 */
public class AppTestMain {

    public static void main(String[] args) {
        System.out.println("----- 开始启动Spring Boot应用程序 -----\n");
        
        try {
            // 启动Spring Boot应用
            ConfigurableApplicationContext context = SpringApplication.run(
                MybatisPlusAdvancedApplication.class, args
            );
            
            System.out.println("\nSpring Boot应用程序启动成功！\n");
            
            // 打印应用上下文信息
            System.out.println("ApplicationContext类型: " + context.getClass().getName());
            System.out.println("Bean定义数量: " + context.getBeanDefinitionCount());
            
            // 检查数据源是否存在
            String[] dataSourceBeans = context.getBeanNamesForType(javax.sql.DataSource.class);
            if (dataSourceBeans.length > 0) {
                System.out.println("\n已找到DataSource Bean:");
                for (String beanName : dataSourceBeans) {
                    System.out.println("- " + beanName + " (" + context.getType(beanName).getName() + ")");
                }
            } else {
                System.out.println("\n没有找到DataSource Bean");
            }
            
            // 检查JdbcTemplate是否存在
            String[] jdbcTemplateBeans = context.getBeanNamesForType(org.springframework.jdbc.core.JdbcTemplate.class);
            if (jdbcTemplateBeans.length > 0) {
                System.out.println("\n已找到JdbcTemplate Bean:");
                for (String beanName : jdbcTemplateBeans) {
                    System.out.println("- " + beanName + " (" + context.getType(beanName).getName() + ")");
                }
            } else {
                System.out.println("\n没有找到JdbcTemplate Bean");
            }
            
            System.out.println("\n----- 应用程序启动测试完成 -----\n");
            
            // 关闭应用上下文
            context.close();
            
        } catch (Exception e) {
            System.out.println("\n应用程序启动失败！\n");
            e.printStackTrace();
        }
    }
}