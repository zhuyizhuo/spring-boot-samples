package com.github.zhuyizhuo.async.sample.domain;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncDomain {

    @Async
    public void asyncTest() {
        System.out.println("hello ,this is async method! threadName:" + Thread.currentThread().getName());
    }
}
