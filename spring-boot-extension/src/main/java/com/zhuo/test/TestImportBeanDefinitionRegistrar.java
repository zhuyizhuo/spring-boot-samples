package com.zhuo.test;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * ImportBeanDefinitionRegistrar扩展点实现
 * 通过注解方式动态注册Bean定义
 * 当前实现：注册一些实用工具类的Bean定义
 */
public class TestImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        System.out.println("[TestImportBeanDefinitionRegistrar] 开始注册Bean定义");
        
        // 注册自定义工具类Bean
        registerUtilityBeans(registry);
        
        // 注册示例服务Bean
        registerExampleServiceBeans(registry);
        
        System.out.println("[TestImportBeanDefinitionRegistrar] Bean定义注册完成");
    }
    
    /**
     * 注册实用工具类Bean
     */
    private void registerUtilityBeans(BeanDefinitionRegistry registry) {
        // 注册字符串工具Bean
        registerBean(registry, "stringUtils", StringUtils.class);
        
        // 注册日期工具Bean
        registerBean(registry, "dateUtils", DateUtils.class);
        
        // 注册配置工具Bean
        registerBean(registry, "configUtils", ConfigUtils.class);
    }
    
    /**
     * 注册示例服务Bean
     */
    private void registerExampleServiceBeans(BeanDefinitionRegistry registry) {
        // 注册示例日志服务Bean
        registerBean(registry, "exampleLogService", ExampleLogService.class);
        
        // 注册示例监控服务Bean
        registerBean(registry, "exampleMonitorService", ExampleMonitorService.class);
    }
    
    /**
     * 注册Bean的通用方法
     */
    private void registerBean(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass) {
        if (!registry.containsBeanDefinition(beanName)) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass);
            
            // 设置Bean的一些属性
            beanDefinition.setLazyInit(false);
            beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
            
            // 注册Bean定义
            registry.registerBeanDefinition(beanName, beanDefinition);
            
            System.out.println("[TestImportBeanDefinitionRegistrar] 已注册Bean: " + beanName + " (" + beanClass.getName() + ")");
        } else {
            System.out.println("[TestImportBeanDefinitionRegistrar] Bean已存在，跳过注册: " + beanName);
        }
    }
    
    /**
     * 字符串工具类
     */
    public static class StringUtils {
        public boolean isEmpty(String str) {
            return str == null || str.isEmpty();
        }
        
        public boolean isNotEmpty(String str) {
            return !isEmpty(str);
        }
        
        public String trim(String str) {
            return str != null ? str.trim() : null;
        }
    }
    
    /**
     * 日期工具类
     */
    public static class DateUtils {
        public long getCurrentTimestamp() {
            return System.currentTimeMillis();
        }
        
        public String getCurrentDateString() {
            return String.valueOf(getCurrentTimestamp());
        }
    }
    
    /**
     * 配置工具类
     */
    public static class ConfigUtils {
        public String getConfigValue(String key, String defaultValue) {
            // 简化实现，实际项目中可以从环境中获取配置
            return defaultValue;
        }
    }
    
    /**
     * 示例日志服务
     */
    public static class ExampleLogService {
        public void logInfo(String message) {
            System.out.println("[ExampleLogService] INFO: " + message);
        }
        
        public void logError(String message, Throwable throwable) {
            System.err.println("[ExampleLogService] ERROR: " + message);
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
    }
    
    /**
     * 示例监控服务
     */
    public static class ExampleMonitorService {
        public void recordMetric(String metricName, long value) {
            System.out.println("[ExampleMonitorService] Metric: " + metricName + " = " + value);
        }
        
        public void incrementCounter(String counterName) {
            System.out.println("[ExampleMonitorService] Counter incremented: " + counterName);
        }
    }
}