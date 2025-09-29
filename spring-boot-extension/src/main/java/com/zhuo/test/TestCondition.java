package com.zhuo.test;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * Condition扩展点实现
 * 用于条件装配，根据条件决定是否注册Bean
 * 当前实现：根据环境属性判断是否启用特定功能
 */
public class TestCondition implements Condition {
    
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        System.out.println("[TestCondition] 开始评估条件");
        
        // 获取环境属性
        String featureEnabled = context.getEnvironment().getProperty("extension.features.enhanced", "false");
        
        // 检查系统属性
        String systemProperty = System.getProperty("extension.debug", "false");
        
        // 检查类路径中是否存在特定类
        boolean hasRequiredClass = hasRequiredClass("org.springframework.web.client.RestTemplate");
        
        // 组合条件判断
        boolean conditionMet = Boolean.parseBoolean(featureEnabled) || Boolean.parseBoolean(systemProperty) || hasRequiredClass;
        
        System.out.println("[TestCondition] 条件评估结果: " + conditionMet + ", featureEnabled: " + featureEnabled + ", systemProperty: " + systemProperty + ", hasRequiredClass: " + hasRequiredClass);
        
        return conditionMet;
    }
    
    /**
     * 检查类路径中是否存在指定的类
     */
    private boolean hasRequiredClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}