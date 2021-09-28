package com.github.zhuo;

public class DemoService {

    private DemoProperties demoProperties;

    public DemoService(DemoProperties demoProperties) {
        this.demoProperties = demoProperties;
    }

    public void sayHello(){
        System.out.println("hello world," + demoProperties.getName());
    }
}
