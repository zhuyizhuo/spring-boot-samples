package com.test.mq;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@SpringBootApplication
public class ProducerApplication {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public static void main(String[] args){
        SpringApplication.run(ProducerApplication.class, args);
    }

    @PostConstruct
    public void sendMsg() {
        rocketMQTemplate.getProducer().setSendMsgTimeout(60000);
        rocketMQTemplate.convertAndSend(MQConstant.TOPIC, "Hello, World!");
        Message<String> build = MessageBuilder.withPayload("Hello, World! I'm from spring message").build();

        rocketMQTemplate.send(MQConstant.TOPIC, build);
    }

}
