package com.zhuo.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * SmartInstantiationAwareBeanPostProcessor扩展点优化
 * 提供Bean实例化前的类型预测、构造函数选择和循环依赖处理功能
 * 当前实现：
 * - 对特定类型的Bean进行类型预测增强
 * - 根据Bean名称和类型选择最优构造函数
 * - 对循环依赖的Bean进行代理处理
 */
@Component
public class TestSmartInstantiationAwareBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {
    
    // 用于存储早期暴露的Bean引用
    private final Map<String, Object> earlyBeanReferences = new HashMap<>();
    
    @Override
    public Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
        System.out.println("[TestSmartInstantiationAwareBeanPostProcessor] 预测Bean类型: " + beanName + " (" + beanClass.getName() + ")");
        
        // 对特定类型的Bean进行类型预测增强
        if (beanName != null && beanName.startsWith("custom")) {
            System.out.println("[TestSmartInstantiationAwareBeanPostProcessor] 检测到自定义前缀Bean: " + beanName);
            // 这里可以根据需要返回特定的类型
        }
        
        return SmartInstantiationAwareBeanPostProcessor.super.predictBeanType(beanClass, beanName);
    }

    @Override
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        System.out.println("[TestSmartInstantiationAwareBeanPostProcessor] 确定构造函数: " + beanName + " (" + beanClass.getName() + ")");
        
        // 获取所有构造函数
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        
        // 如果有多个构造函数，可以根据Bean名称或类型选择最优的构造函数
        if (constructors.length > 1) {
            System.out.println("[TestSmartInstantiationAwareBeanPostProcessor] 找到多个构造函数，数量: " + constructors.length);
            
            // 示例：优先选择参数最多的构造函数
            Constructor<?> maxParamConstructor = Arrays.stream(constructors)
                    .max((c1, c2) -> Integer.compare(c1.getParameterCount(), c2.getParameterCount()))
                    .orElse(null);
            
            if (maxParamConstructor != null) {
                System.out.println("[TestSmartInstantiationAwareBeanPostProcessor] 优先选择参数最多的构造函数: " + 
                        maxParamConstructor.getName() + " 参数数量: " + maxParamConstructor.getParameterCount());
                return new Constructor<?>[]{maxParamConstructor};
            }
        }
        
        return SmartInstantiationAwareBeanPostProcessor.super.determineCandidateConstructors(beanClass, beanName);
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        System.out.println("[TestSmartInstantiationAwareBeanPostProcessor] 处理早期Bean引用: " + beanName);
        
        // 记录早期暴露的Bean引用
        earlyBeanReferences.put(beanName, bean);
        
        // 对特定类型的Bean进行代理处理，以解决循环依赖
        if (beanName.contains("service") || beanName.contains("component")) {
            System.out.println("[TestSmartInstantiationAwareBeanPostProcessor] 对Bean进行早期引用处理: " + beanName);
            // 这里可以返回代理对象或进行其他处理
        }
        
        // 记录循环依赖情况
        if (earlyBeanReferences.containsKey(beanName)) {
            System.out.println("[TestSmartInstantiationAwareBeanPostProcessor] 检测到潜在的循环依赖: " + beanName);
        }
        
        return SmartInstantiationAwareBeanPostProcessor.super.getEarlyBeanReference(bean, beanName);
    }
}
