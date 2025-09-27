package com.github.zhuyizhuo.sample.mybatis.plus.advanced.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * 最简单的Spring上下文测试类
 */
@SpringBootTest(classes = com.github.zhuyizhuo.sample.mybatis.plus.advanced.MybatisPlusAdvancedApplication.class)
public class SpringContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 测试Spring上下文是否能够正确初始化
     */
    @Test
    public void testApplicationContext() {
        System.out.println("===== 测试Spring上下文 ====");
        if (applicationContext != null) {
            System.out.println("✅ ApplicationContext注入成功！");
            int beanCount = applicationContext.getBeanDefinitionCount();
            System.out.println("Spring容器中的Bean数量: " + beanCount);
            
            // 尝试获取DataSource
            boolean hasDataSource = applicationContext.containsBean("dataSource");
            System.out.println("DataSource Bean存在: " + hasDataSource);
            
            // 输出一些核心Bean的信息
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            System.out.println("前10个Bean名称：");
            for (int i = 0; i < Math.min(10, beanNames.length); i++) {
                System.out.println("- " + beanNames[i]);
            }
        } else {
            System.out.println("❌ ApplicationContext未成功注入！");
        }
    }
}