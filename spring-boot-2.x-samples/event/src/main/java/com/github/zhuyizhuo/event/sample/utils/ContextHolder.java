package com.github.zhuyizhuo.event.sample.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 上下文工具
 */
@Component
public class ContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ContextHolder.context = applicationContext;
    }

    public static ApplicationContext getContext(){
        return context;
    }
}
