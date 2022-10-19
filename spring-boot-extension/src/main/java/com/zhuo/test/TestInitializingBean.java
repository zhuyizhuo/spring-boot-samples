package com.zhuo.test;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * TODO
 */
@Component
public class TestInitializingBean implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("[TestInitializingBean] start.");
    }
}
