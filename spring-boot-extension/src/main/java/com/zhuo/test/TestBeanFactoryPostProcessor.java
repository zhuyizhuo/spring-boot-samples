package com.zhuo.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * BeanFactoryPostProcessor扩展点优化
 * 在Spring读取所有beanDefinition之后，实例化Bean之前执行
 * 当前实现：修改特定Bean的属性值，设置默认属性
 */
@Component
public class TestBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("[TestBeanFactoryPostProcessor] 开始处理BeanFactory");
        
        // 获取所有Bean定义名称
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        
        // 遍历所有Bean定义，对特定类型的Bean进行处理
        for (String beanName : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            
            // 示例：为所有Service结尾的Bean设置延迟初始化
            if (beanName.endsWith("Service") || beanName.endsWith("service")) {
                if (!beanDefinition.isLazyInit()) {
                    beanDefinition.setLazyInit(true);
                    System.out.println("[TestBeanFactoryPostProcessor] 设置Bean [" + beanName + "] 为延迟初始化");
                }
            }
            
            // 示例：修改作用域
            if ("customDynamicService".equals(beanName)) {
                if (!"prototype".equals(beanDefinition.getScope())) {
                    beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
                    System.out.println("[TestBeanFactoryPostProcessor] 修改Bean [" + beanName + "] 的作用域为原型(prototype)");
                }
            }
        }
        
        // 检查是否存在特定Bean并获取其BeanDefinition
        if (beanFactory.containsBeanDefinition("testSmartInitializingSingleton")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("testSmartInitializingSingleton");
            System.out.println("[TestBeanFactoryPostProcessor] 找到Bean testSmartInitializingSingleton，作用域: " + beanDefinition.getScope());
        }
    }
}
