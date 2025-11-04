package com.example.rocketmqdemo.consumer;

import com.example.rocketmqdemo.entity.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * RocketMQ延时消息消费者服务
 * 监听延时主题消息
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "demo-delay-topic",
        consumerGroup = "demo-delay-consumer-group",
        consumeMode = org.apache.rocketmq.spring.annotation.ConsumeMode.CONCURRENTLY,
        messageModel = org.apache.rocketmq.spring.annotation.MessageModel.CLUSTERING
)
public class RocketMQDelayConsumerService implements RocketMQListener<MessageDTO> {

    @Override
    public void onMessage(MessageDTO message) {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            long sendTimeMillis = message.getSendTime() != null ? 
                java.time.ZonedDateTime.of(message.getSendTime(), java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : 
                0;
            long delayTimeSeconds = (currentTimeMillis - sendTimeMillis) / 1000;
            
            log.info("收到延时消息：主题=demo-delay-topic, 消息ID={}, 内容={}, 类型={}, 发送时间={}, 实际延时={}秒",
                    message.getId(), message.getContent(), message.getMessageType(), message.getSendTime(), delayTimeSeconds);
            
            // 处理延时消息
            processDelayMessage(message, delayTimeSeconds);
            
            log.info("延时消息处理成功：消息ID={}", message.getId());
        } catch (Exception e) {
            log.error("延时消息处理失败：消息ID={}", message.getId(), e);
            throw new RuntimeException("延时消息处理失败", e);
        }
    }
    
    /**
     * 处理延时消息
     */
    private void processDelayMessage(MessageDTO message, long actualDelaySeconds) {
        // 根据实际业务需求处理延时消息
        // 例如：订单超时处理、定时通知等
        log.info("执行延时任务，消息ID={}, 预计延时级别={}, 实际延时={}秒", 
                message.getId(), message.getMessageType(), actualDelaySeconds);
        
        // TODO: 执行延时消息相关的业务逻辑
    }
}