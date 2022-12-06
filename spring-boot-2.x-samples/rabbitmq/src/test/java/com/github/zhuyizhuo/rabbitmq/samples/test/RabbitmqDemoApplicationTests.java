package com.github.zhuyizhuo.rabbitmq.samples.test;

import com.github.zhuyizhuo.rabbitmq.samples.MsgProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitmqDemoApplicationTests {
    @Autowired
    private MsgProducer msgProducer;
    @Test
    public void test() throws InterruptedException {
        System.out.println("1");
        msgProducer.sendMsg("hello,this is my msg");
        System.out.println(2);
        Thread.sleep(Long.MAX_VALUE);
    }
}
