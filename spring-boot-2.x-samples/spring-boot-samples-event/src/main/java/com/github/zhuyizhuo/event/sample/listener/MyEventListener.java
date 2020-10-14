package com.github.zhuyizhuo.event.sample.listener;

import com.github.zhuyizhuo.event.sample.event.MyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 自定义事件监听器
 */
@Slf4j
@Component
public class MyEventListener {

    /***
     * 事件监听默认为同步执行
     * 使用 @Async 注解，将同步变为异步
     * @param myEvent
     */
    @EventListener
    @Async
    public void onApplicationEvent(MyEvent myEvent) throws InterruptedException {
        log.info("监听到事件 start:{}" , myEvent);
        Thread.sleep(2000);
        log.info("监听到事件 end:{}" , myEvent);
    }

}
