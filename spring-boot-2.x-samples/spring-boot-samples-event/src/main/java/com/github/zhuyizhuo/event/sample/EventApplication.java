package com.github.zhuyizhuo.event.sample;

import com.github.zhuyizhuo.event.sample.event.MyEvent;
import com.github.zhuyizhuo.event.sample.utils.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@SpringBootApplication
public class EventApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(EventApplication.class, args);
        //发布自定义事件
        MyEvent helloWorld = new MyEvent("hello world");
        ApplicationContext context = ContextHolder.getContext();
        helloWorld.setName("1235");
        context.publishEvent(helloWorld);
        log.info("event published.");
    }

}
