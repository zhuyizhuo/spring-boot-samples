package com.zhuo.test;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 这是整个spring容器在刷新之前初始化ConfigurableApplicationContext的回调接口，简单来说，就是在容器刷新之前调用此类的initialize方法。这个点允许被用户自己扩展。用户可以在整个spring容器还没被初始化之前做一些事情。
 *
 * 可以想到的场景可能为，在最开始激活一些配置，或者利用这时候class还没被类加载器加载的时机，进行动态字节码注入等操作。
 *
 * 因为这时候spring容器还没被初始化，所以想要自己的扩展的生效，有以下三种方式：
 *
 * 在启动类中用springApplication.addInitializers(new TestApplicationContextInitializer())语句加入
 * 配置文件配置context.initializer.classes=com.zhuo.test.TestApplicationContextInitializer
 * Spring SPI扩展，在spring.factories中加入org.springframework.context.ApplicationContextInitializer=com.zhuo.test.TestApplicationContextInitializer
 */
public class TestApplicationContextInitializer implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        System.out.println("[TestApplicationContextInitializer] start.");
    }
}
