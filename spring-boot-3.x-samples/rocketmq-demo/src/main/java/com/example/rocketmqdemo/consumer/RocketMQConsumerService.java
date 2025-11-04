package com.example.rocketmqdemo.consumer;

import com.example.rocketmqdemo.entity.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * RocketMQ消息消费者服务
 * 监听普通主题消息
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "demo-topic",
        consumerGroup = "demo-consumer-group",
        consumeMode = org.apache.rocketmq.spring.annotation.ConsumeMode.CONCURRENTLY,
        messageModel = org.apache.rocketmq.spring.annotation.MessageModel.CLUSTERING
)
public class RocketMQConsumerService implements RocketMQListener<MessageDTO> {

    @Override
    public void onMessage(MessageDTO message) {
        try {
            log.info("收到消息：主题=demo-topic, 消息ID={}, 内容={}, 类型={}, 发送时间={}",
                    message.getId(), message.getContent(), message.getMessageType(), message.getSendTime());
            
            // 这里可以根据消息类型进行不同的业务处理
            processMessage(message);
            
            log.info("消息处理成功：消息ID={}", message.getId());
        } catch (Exception e) {
            log.error("消息处理失败：消息ID={}", message.getId(), e);
            // 如果需要重试，可以抛出异常，RocketMQ会根据配置进行重试
            throw new RuntimeException("消息处理失败", e);
        }
    }
    
    /**
     * 根据消息类型处理消息
     */
    private void processMessage(MessageDTO message) {
        switch (message.getMessageType()) {
            case "TEXT" -> {
                // 处理文本消息
                log.info("处理文本消息：{}", message.getContent());
                // TODO: 执行文本消息相关的业务逻辑
            }
            case "NOTIFICATION" -> {
                // 处理通知消息
                log.info("处理通知消息：{}", message.getContent());
                // TODO: 执行通知消息相关的业务逻辑
            }
            case "ORDER" -> {
                // 处理订单消息
                log.info("处理订单消息：{}", message.getContent());
                // TODO: 执行订单消息相关的业务逻辑
            }
            default -> {
                // 处理未知类型消息
                log.warn("收到未知类型消息：类型={}, 内容={}", message.getMessageType(), message.getContent());
            }
        }
    }
}