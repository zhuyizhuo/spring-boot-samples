package com.zhuo;

import com.github.zhuo.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DemoApplication {

    @Autowired
    private DemoService demoService;

    public static void main(String[] args) throws InterruptedException {

        ConfigurableApplicationContext run = SpringApplication.run(DemoApplication.class, args);
        Thread.sleep(Long.MAX_VALUE);

    }

    @PostConstruct
    public void say(){
        demoService.sayHello();
    }
}
