package com.test.mq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class ConsumerApplication{

//    public static void main(String[] args){
//        SpringApplication.run(ConsumerApplication.class, args);
//    }

    @Service
    @RocketMQMessageListener(topic = MQConstant.TOPIC, consumerGroup = "my-consumer_" + MQConstant.TOPIC)
    public static class MyConsumer implements RocketMQListener<String> {
        public void onMessage(String message) {
            System.out.println("received message: " + message);
        }
    }
}
