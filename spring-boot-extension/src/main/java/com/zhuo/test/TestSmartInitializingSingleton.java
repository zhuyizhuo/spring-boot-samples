package com.zhuo.test;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.zhuo.test.TestBeanDefinitionRegistryPostProcessor.CustomDynamicService;

/**
 * SmartInitializingSingleton扩展点优化
 * 在Spring容器中所有单例Bean（非懒加载）初始化完成后执行
 * 当前实现：获取并使用动态注册的Bean，执行初始化后业务处理
 */
@Component
public class TestSmartInitializingSingleton implements SmartInitializingSingleton {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Override
    public void afterSingletonsInstantiated() {
        System.out.println("[TestSmartInitializingSingleton] 所有单例Bean初始化完成，开始执行后置处理");
        
        // 检查并使用之前动态注册的服务
        if (applicationContext.containsBean("customDynamicService")) {
            // 由于在TestBeanFactoryPostProcessor中将其修改为prototype作用域，每次获取都是新实例
            CustomDynamicService service1 = applicationContext.getBean("customDynamicService", CustomDynamicService.class);
            CustomDynamicService service2 = applicationContext.getBean("customDynamicService", CustomDynamicService.class);
            
            System.out.println("[TestSmartInitializingSingleton] 获取动态注册的服务信息: " + service1.getServiceInfo());
            System.out.println("[TestSmartInitializingSingleton] 验证prototype作用域: service1 == service2 ? " + (service1 == service2));
        }
        
        // 获取自定义环境属性
        String appName = applicationContext.getEnvironment().getProperty("extension.application.name");
        String appVersion = applicationContext.getEnvironment().getProperty("extension.application.version");
        
        System.out.println("[TestSmartInitializingSingleton] 从环境中获取的应用信息: 名称=" + appName + ", 版本=" + appVersion);
        
        // 示例：可以在这里执行一些初始化后的业务逻辑，如数据预热、缓存加载等
        System.out.println("[TestSmartInitializingSingleton] 单例Bean初始化后置处理完成");
    }
}
