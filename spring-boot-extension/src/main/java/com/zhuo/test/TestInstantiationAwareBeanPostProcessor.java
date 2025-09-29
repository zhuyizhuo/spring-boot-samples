package com.zhuo.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * InstantiationAwareBeanPostProcessor扩展点优化
 * 在Bean实例化前后及属性设置前后执行的处理器
 * 当前实现：监控Bean的实例化过程，对特定类型Bean进行属性注入和修改
 */
@Component
public class TestInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
    
    // 需要特殊处理的Bean类型
    private static final Set<String> SPECIAL_BEANS = new HashSet<>(Arrays.asList(
            "testSmartInitializingSingleton",
            "testCommandLineRunner"
    ));
    
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        System.out.println("[TestInstantiationAwareBeanPostProcessor] Bean实例化前处理: " + beanName + " (" + beanClass.getSimpleName() + ")");
        
        // 这里可以返回代理对象来替代原始Bean的实例化
        // 示例：如果需要对特定Bean进行代理，可以在这里创建代理对象并返回
        
        return null; // 返回null表示继续原始的实例化过程
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        System.out.println("[TestInstantiationAwareBeanPostProcessor] Bean实例化后处理: " + beanName + " (" + bean.getClass().getSimpleName() + ")");
        
        // 返回true表示继续属性注入，返回false表示跳过属性注入
        return true;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        System.out.println("[TestInstantiationAwareBeanPostProcessor] Bean属性设置前处理: " + beanName);
        
        // 可以在这里修改属性值，或者添加新的属性值
        // 例如：如果是特殊Bean，可以添加自定义属性
        if (SPECIAL_BEANS.contains(beanName)) {
            System.out.println("[TestInstantiationAwareBeanPostProcessor] 对特殊Bean " + beanName + " 进行属性处理");
        }
        
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("[TestInstantiationAwareBeanPostProcessor] Bean初始化前处理: " + beanName);
        
        // 示例：对ConfigManager类型的Bean进行初始化前处理
        if (bean instanceof TestFactoryBean.ConfigManager) {
            TestFactoryBean.ConfigManager configManager = (TestFactoryBean.ConfigManager) bean;
            System.out.println("[TestInstantiationAwareBeanPostProcessor] 检测到ConfigManager Bean，配置项数量: " + 
                              configManager.getConfigCount());
        }
        
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("[TestInstantiationAwareBeanPostProcessor] Bean初始化后处理: " + beanName);
        
        // 示例：为Bean添加一个额外的标识属性
        if (SPECIAL_BEANS.contains(beanName)) {
            try {
                Field processedField = bean.getClass().getDeclaredField("processedByInstantiationAware");
                processedField.setAccessible(true);
                processedField.set(bean, true);
            } catch (Exception e) {
                // 忽略反射异常
            }
        }
        
        return bean;
    }
}
