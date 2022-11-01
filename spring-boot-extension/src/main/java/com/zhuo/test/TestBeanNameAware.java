package com.zhuo.test;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

/**
 * TODO
 */
@Component
public class TestBeanNameAware implements BeanNameAware {
    @Override
    public void setBeanName(String s) {
        System.out.println("[TestBeanNameAware] start.");
    }
}
