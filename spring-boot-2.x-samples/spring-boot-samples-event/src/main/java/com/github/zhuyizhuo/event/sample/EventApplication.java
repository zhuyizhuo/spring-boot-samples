package com.github.zhuyizhuo.event.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class EventApplication {
    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext run = SpringApplication.run(EventApplication.class, args);
        MyEvent helloWorld = new MyEvent("hello world");
        ApplicationContext context = ContextHolder.getContext();
        helloWorld.setName("1235");
        context.publishEvent(helloWorld);
    }
}
