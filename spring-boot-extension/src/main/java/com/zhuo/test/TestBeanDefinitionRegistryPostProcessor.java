package com.zhuo.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 这个接口在读取项目中的beanDefinition之后执行，提供一个补充的扩展点
 *
 * 使用场景：你可以在这里动态注册自己的beanDefinition，可以加载classpath之外的bean
 */
@Component
public class TestBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        System.out.println("[TestBeanDefinitionRegistryPostProcessor] start 1.");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        System.out.println("[TestBeanDefinitionRegistryPostProcessor] start 2.");
    }
}
