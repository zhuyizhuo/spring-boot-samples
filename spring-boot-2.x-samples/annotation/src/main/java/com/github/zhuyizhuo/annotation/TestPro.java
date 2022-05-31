package com.github.zhuyizhuo.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "test.demo")
public class TestPro {

    public void sayHello(){
        System.out.println("TestPro hello!");
    }
}
