package com.zhuo.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

/**
 * BeanDefinitionRegistryPostProcessor扩展点优化
 * 在读取项目中所有beanDefinition之后执行，允许动态注册或修改Bean定义
 * 当前实现：动态注册一个自定义的服务Bean
 */
@Component
public class TestBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("[TestBeanDefinitionRegistryPostProcessor] 开始处理Bean定义注册表");
        
        // 动态注册一个自定义服务Bean
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CustomDynamicService.class);
        builder.addPropertyValue("serviceName", "动态注册的服务");
        builder.addPropertyValue("version", "1.0.0");
        
        registry.registerBeanDefinition("customDynamicService", builder.getBeanDefinition());
        System.out.println("[TestBeanDefinitionRegistryPostProcessor] 成功注册自定义服务Bean: customDynamicService");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("[TestBeanDefinitionRegistryPostProcessor] 开始处理BeanFactory");
        
        // 可以在这里对已注册的Bean定义进行修改
        // 例如修改某个Bean的作用域或属性值
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        System.out.println("[TestBeanDefinitionRegistryPostProcessor] 当前已注册的Bean数量: " + beanDefinitionNames.length);
    }
    
    /**
     * 动态注册的自定义服务类
     */
    public static class CustomDynamicService {
        private String serviceName;
        private String version;
        
        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }
        
        public String getServiceInfo() {
            return "服务名称: " + serviceName + ", 版本: " + version;
        }
    }
}
