package com.zhuo.test;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

/**
 * BeanNameAware扩展点优化
 * 在Bean初始化过程中获取Bean的名称
 * 当前实现：使用Bean名称进行日志记录和Bean标识
 */
@Component
public class TestBeanNameAware implements BeanNameAware {
    
    private String beanName;
    
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
        System.out.println("[TestBeanNameAware] 获取到Bean名称: " + beanName);
        
        // 使用Bean名称执行特定操作
        performBeanNameSpecificOperation();
    }
    
    /**
     * 根据Bean名称执行特定操作
     */
    private void performBeanNameSpecificOperation() {
        if (beanName != null) {
            // 示例：根据Bean名称前缀执行不同操作
            if (beanName.startsWith("test")) {
                System.out.println("[TestBeanNameAware] 处理测试类型的Bean: " + beanName);
            } else if (beanName.endsWith("Service")) {
                System.out.println("[TestBeanNameAware] 处理服务类型的Bean: " + beanName);
            } else {
                System.out.println("[TestBeanNameAware] 处理其他类型的Bean: " + beanName);
            }
        }
    }
    
    /**
     * 获取Bean名称
     */
    public String getBeanName() {
        return beanName;
    }
}
