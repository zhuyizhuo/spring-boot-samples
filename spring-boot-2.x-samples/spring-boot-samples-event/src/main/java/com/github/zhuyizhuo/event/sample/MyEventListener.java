package com.github.zhuyizhuo.event.sample;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MyEventListener {

    @EventListener
    public void onApplicationEvent(MyEvent myEvent) {
        System.out.println("监听到事件:" + myEvent);
    }

}
