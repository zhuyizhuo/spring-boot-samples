package com.zhuo.test;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * TODO
 */
@Component
public class TestFactoryBean implements FactoryBean<TestFactoryBean.InnerBean> {
    @Override
    public InnerBean getObject() throws Exception {
        System.out.println("[TestFactoryBean] start.");
        return new InnerBean();
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }

    public static class InnerBean{

    }
}
