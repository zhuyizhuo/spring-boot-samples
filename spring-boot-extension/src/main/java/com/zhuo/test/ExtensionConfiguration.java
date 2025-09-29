package com.zhuo.test;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot扩展点配置类
 * 整合所有扩展点，演示条件装配等功能
 */
@Configuration
@Import(TestImportBeanDefinitionRegistrar.class)
public class ExtensionConfiguration {
    
    /**
     * 注册条件性Bean - 根据TestCondition判断是否注册
     */
    @Bean
    @Conditional(TestCondition.class)
    public EnhancedFeatureService enhancedFeatureService() {
        System.out.println("[ExtensionConfiguration] 条件满足，注册EnhancedFeatureService");
        return new EnhancedFeatureService();
    }
    
    /**
     * 注册默认服务 - 当EnhancedFeatureService未注册时才注册
     */
    @Bean
    @ConditionalOnMissingBean(EnhancedFeatureService.class)
    public DefaultFeatureService defaultFeatureService() {
        System.out.println("[ExtensionConfiguration] EnhancedFeatureService未注册，注册DefaultFeatureService");
        return new DefaultFeatureService();
    }
    
    /**
     * 注册自定义配置提供者
     */
    @Bean
    public CustomConfigProvider customConfigProvider() {
        return new CustomConfigProvider();
    }
    
    /**
     * 增强功能服务类
     */
    public static class EnhancedFeatureService {
        public void performEnhancedOperation() {
            System.out.println("[EnhancedFeatureService] 执行增强功能操作");
            // 实际项目中这里可以实现增强功能的逻辑
        }
        
        public String getFeatureInfo() {
            return "增强版功能服务，提供高级特性";
        }
    }
    
    /**
     * 默认功能服务类
     */
    public static class DefaultFeatureService {
        public void performBasicOperation() {
            System.out.println("[DefaultFeatureService] 执行基本功能操作");
            // 实际项目中这里可以实现基本功能的逻辑
        }
        
        public String getFeatureInfo() {
            return "基础版功能服务，提供标准特性";
        }
    }
    
    /**
     * 自定义配置提供者
     */
    public static class CustomConfigProvider {
        public String getCustomConfig(String key) {
            // 简化实现，实际项目中可以从多种来源获取配置
            switch (key) {
                case "app.name":
                    return "Spring Boot Extension Demo";
                case "app.version":
                    return "1.0.0";
                case "app.description":
                    return "Spring Boot扩展点示例项目";
                default:
                    return "配置未找到: " + key;
            }
        }
    }
}