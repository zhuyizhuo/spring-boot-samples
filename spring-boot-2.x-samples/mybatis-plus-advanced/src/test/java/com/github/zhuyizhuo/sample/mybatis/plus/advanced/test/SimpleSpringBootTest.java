package com.github.zhuyizhuo.sample.mybatis.plus.advanced.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 简单的Spring Boot测试类
 * 用于验证Spring Boot容器是否能够正确启动和配置
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = com.github.zhuyizhuo.sample.mybatis.plus.advanced.MybatisPlusAdvancedApplication.class)
public class SimpleSpringBootTest {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 测试Spring Boot容器是否正确启动
     */
    @Test
    public void testApplicationContext() {
        System.out.println("----- 测试Spring Boot容器启动 -----\n");
        
        // 检查ApplicationContext是否成功注入
        if (applicationContext == null) {
            System.out.println("错误: ApplicationContext未成功注入");
            return;
        }
        
        System.out.println("Spring Boot容器已成功启动");
        System.out.println("ApplicationContext类型: " + applicationContext.getClass().getName());
        
        // 打印Spring容器中的Bean定义数量
        int beanDefinitionCount = applicationContext.getBeanDefinitionCount();
        System.out.println("Spring容器中的Bean定义数量: " + beanDefinitionCount);
        
        // 打印部分Bean名称
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        System.out.println("\n前10个Bean名称:");
        for (int i = 0; i < Math.min(10, beanNames.length); i++) {
            System.out.println("- " + beanNames[i]);
        }
        
        // 检查是否有DataSource Bean
        String[] dataSourceBeans = applicationContext.getBeanNamesForType(javax.sql.DataSource.class);
        if (dataSourceBeans.length > 0) {
            System.out.println("\nDataSource Bean存在:");
            for (String beanName : dataSourceBeans) {
                System.out.println("- " + beanName + " (" + applicationContext.getType(beanName).getName() + ")");
            }
        } else {
            System.out.println("\n没有找到DataSource Bean");
        }
        
        // 检查是否有JdbcTemplate Bean
        String[] jdbcTemplateBeans = applicationContext.getBeanNamesForType(org.springframework.jdbc.core.JdbcTemplate.class);
        if (jdbcTemplateBeans.length > 0) {
            System.out.println("\nJdbcTemplate Bean存在:");
            for (String beanName : jdbcTemplateBeans) {
                System.out.println("- " + beanName + " (" + applicationContext.getType(beanName).getName() + ")");
            }
        } else {
            System.out.println("\n没有找到JdbcTemplate Bean");
        }
        
        System.out.println("\n----- Spring Boot容器测试完成 -----\n");
    }
}