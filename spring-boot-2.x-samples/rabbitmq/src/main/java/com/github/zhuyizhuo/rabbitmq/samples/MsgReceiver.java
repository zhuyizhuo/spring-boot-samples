package com.github.zhuyizhuo.rabbitmq.samples;

import com.github.zhuyizhuo.rabbitmq.samples.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitConfig.QUEUE_A)
public class MsgReceiver {

    @RabbitHandler
    public void process(String content) {
        System.out.println("接收处理队列A当中的消息： " + content);
    }
}
