package com.github.zhuyizhuo.rabbitmq.demo.service;

import com.github.zhuyizhuo.rabbitmq.demo.config.RabbitConfig;
import com.github.zhuyizhuo.rabbitmq.demo.model.MessageDto;
import com.github.zhuyizhuo.rabbitmq.demo.model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息生产者服务
 * 
 * @author zhuyizhuo
 * @version 1.0
 * @date 2025/10/02
 */
@Service
public class MessageProducerService {

    private static final Logger logger = LoggerFactory.getLogger(MessageProducerService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送Direct类型消息
     * 
     * @param messageDto 消息内容
     */
    public void sendDirectMessage(MessageDto messageDto) {
        try {
            messageDto.setMessageId(UUID.randomUUID().toString());
            messageDto.setSendTime(LocalDateTime.now());
            
            CorrelationData correlationData = new CorrelationData(messageDto.getMessageId());
            
            rabbitTemplate.convertAndSend(
                    RabbitConfig.DIRECT_EXCHANGE,
                    RabbitConfig.DIRECT_ROUTING_KEY,
                    messageDto,
                    correlationData
            );
            
            logger.info("Direct消息发送成功: {}", messageDto);
        } catch (Exception e) {
            logger.error("Direct消息发送失败: {}", e.getMessage(), e);
            throw new RuntimeException("消息发送失败", e);
        }
    }

    /**
     * 发送Topic类型消息
     * 
     * @param messageDto 消息内容
     * @param routingKey 路由键
     */
    public void sendTopicMessage(MessageDto messageDto, String routingKey) {
        try {
            messageDto.setMessageId(UUID.randomUUID().toString());
            messageDto.setSendTime(LocalDateTime.now());
            
            CorrelationData correlationData = new CorrelationData(messageDto.getMessageId());
            
            rabbitTemplate.convertAndSend(
                    RabbitConfig.TOPIC_EXCHANGE,
                    routingKey,
                    messageDto,
                    correlationData
            );
            
            logger.info("Topic消息发送成功, routingKey: {}, message: {}", routingKey, messageDto);
        } catch (Exception e) {
            logger.error("Topic消息发送失败: {}", e.getMessage(), e);
            throw new RuntimeException("消息发送失败", e);
        }
    }

    /**
     * 发送用户相关Topic消息
     * 
     * @param messageDto 消息内容
     */
    public void sendUserMessage(MessageDto messageDto) {
        sendTopicMessage(messageDto, "demo.topic.user.info");
    }

    /**
     * 发送订单相关Topic消息
     * 
     * @param messageDto 消息内容
     */
    public void sendOrderMessage(MessageDto messageDto) {
        sendTopicMessage(messageDto, "demo.topic.order.created");
    }

    /**
     * 发送Fanout类型消息（广播消息）
     * 
     * @param messageDto 消息内容
     */
    public void sendFanoutMessage(MessageDto messageDto) {
        try {
            messageDto.setMessageId(UUID.randomUUID().toString());
            messageDto.setSendTime(LocalDateTime.now());
            
            CorrelationData correlationData = new CorrelationData(messageDto.getMessageId());
            
            rabbitTemplate.convertAndSend(
                    RabbitConfig.FANOUT_EXCHANGE,
                    "", // Fanout exchange不需要routing key
                    messageDto,
                    correlationData
            );
            
            logger.info("Fanout消息发送成功（广播）: {}", messageDto);
        } catch (Exception e) {
            logger.error("Fanout消息发送失败: {}", e.getMessage(), e);
            throw new RuntimeException("消息发送失败", e);
        }
    }

    /**
     * 发送Headers类型消息
     * 
     * @param messageDto 消息内容
     * @param type 消息类型标识
     * @param priority 优先级
     */
    public void sendHeadersMessage(MessageDto messageDto, String type, String priority) {
        try {
            messageDto.setMessageId(UUID.randomUUID().toString());
            messageDto.setSendTime(LocalDateTime.now());
            
            CorrelationData correlationData = new CorrelationData(messageDto.getMessageId());
            
            // 设置Headers
            MessageProperties properties = new MessageProperties();
            properties.setHeader("type", type);
            properties.setHeader("priority", priority);
            
            Message message = new Message(rabbitTemplate.getMessageConverter().toMessage(messageDto, properties).getBody(), properties);
            
            rabbitTemplate.send(
                    RabbitConfig.HEADERS_EXCHANGE,
                    "",
                    message,
                    correlationData
            );
            
            logger.info("Headers消息发送成功, type: {}, priority: {}, message: {}", type, priority, messageDto);
        } catch (Exception e) {
            logger.error("Headers消息发送失败: {}", e.getMessage(), e);
            throw new RuntimeException("消息发送失败", e);
        }
    }

    /**
     * 发送延迟消息（使用TTL实现）
     * 
     * @param messageDto 消息内容
     * @param delaySeconds 延迟秒数
     */
    public void sendDelayedMessage(MessageDto messageDto, int delaySeconds) {
        try {
            messageDto.setMessageId(UUID.randomUUID().toString());
            messageDto.setSendTime(LocalDateTime.now());
            
            CorrelationData correlationData = new CorrelationData(messageDto.getMessageId());
            
            rabbitTemplate.convertAndSend(
                    RabbitConfig.DIRECT_EXCHANGE,
                    RabbitConfig.DIRECT_ROUTING_KEY,
                    messageDto,
                    message -> {
                        message.getMessageProperties().setExpiration(String.valueOf(delaySeconds * 1000));
                        return message;
                    },
                    correlationData
            );
            
            logger.info("延迟消息发送成功, 延迟{}秒: {}", delaySeconds, messageDto);
        } catch (Exception e) {
            logger.error("延迟消息发送失败: {}", e.getMessage(), e);
            throw new RuntimeException("消息发送失败", e);
        }
    }

    /**
     * 批量发送消息
     * 
     * @param messages 消息列表
     */
    public void sendBatchMessages(MessageDto... messages) {
        for (MessageDto message : messages) {
            sendDirectMessage(message);
        }
    }
}

