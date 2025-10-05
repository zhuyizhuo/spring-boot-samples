package com.github.zhuyizhuo.rabbitmq.demo.service;

import com.github.zhuyizhuo.rabbitmq.demo.config.RabbitConfig;
import com.github.zhuyizhuo.rabbitmq.demo.model.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 消息消费者服务
 * 
 * @author zhuyizhuo
 * @version 1.0
 * @date 2025/10/02
 */
@Service
public class MessageConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumerService.class);

    /**
     * 消费Direct队列消息
     * 
     * @param messageDto 消息内容
     * @param message 原始消息
     */
    @RabbitListener(queues = RabbitConfig.DIRECT_QUEUE)
    @RabbitHandler
    public void handleDirectMessage(@Payload MessageDto messageDto, Message message) {
        try {
            logger.info("接收到Direct消息: {}", messageDto);
            logger.info("消息属性: deliveryTag={}, redelivered={}", 
                    message.getMessageProperties().getDeliveryTag(),
                    message.getMessageProperties().getRedelivered());
            
            // 模拟业务处理
            processBusinessLogic(messageDto, "DIRECT");
            
            logger.info("Direct消息处理完成: {}", messageDto.getMessageId());
        } catch (Exception e) {
            logger.error("Direct消息处理失败: {}", e.getMessage(), e);
            // 这里可以根据业务需要决定是否重新抛出异常进行重试
            throw new RuntimeException("消息处理失败", e);
        }
    }

    /**
     * 消费Topic队列用户消息
     * 
     * @param messageDto 消息内容
     */
    @RabbitListener(queues = RabbitConfig.TOPIC_QUEUE_USER)
    @RabbitHandler
    public void handleTopicUserMessage(@Payload MessageDto messageDto) {
        try {
            logger.info("接收到Topic用户消息: {}", messageDto);
            
            // 模拟用户相关业务处理
            processBusinessLogic(messageDto, "TOPIC_USER");
            
            logger.info("Topic用户消息处理完成: {}", messageDto.getMessageId());
        } catch (Exception e) {
            logger.error("Topic用户消息处理失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 消费Topic队列订单消息
     * 
     * @param messageDto 消息内容
     */
    @RabbitListener(queues = RabbitConfig.TOPIC_QUEUE_ORDER)
    @RabbitHandler
    public void handleTopicOrderMessage(@Payload MessageDto messageDto) {
        try {
            logger.info("接收到Topic订单消息: {}", messageDto);
            
            // 模拟订单相关业务处理
            processBusinessLogic(messageDto, "TOPIC_ORDER");
            
            logger.info("Topic订单消息处理完成: {}", messageDto.getMessageId());
        } catch (Exception e) {
            logger.error("Topic订单消息处理失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 消费Fanout队列邮件消息
     * 
     * @param messageDto 消息内容
     */
    @RabbitListener(queues = RabbitConfig.FANOUT_QUEUE_EMAIL)
    @RabbitHandler
    public void handleFanoutEmailMessage(@Payload MessageDto messageDto) {
        try {
            logger.info("接收到Fanout邮件消息: {}", messageDto);
            
            // 模拟邮件发送业务处理
            processBusinessLogic(messageDto, "FANOUT_EMAIL");
            
            logger.info("Fanout邮件消息处理完成: {}", messageDto.getMessageId());
        } catch (Exception e) {
            logger.error("Fanout邮件消息处理失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 消费Fanout队列短信消息
     * 
     * @param messageDto 消息内容
     */
    @RabbitListener(queues = RabbitConfig.FANOUT_QUEUE_SMS)
    @RabbitHandler
    public void handleFanoutSmsMessage(@Payload MessageDto messageDto) {
        try {
            logger.info("接收到Fanout短信消息: {}", messageDto);
            
            // 模拟短信发送业务处理
            processBusinessLogic(messageDto, "FANOUT_SMS");
            
            logger.info("Fanout短信消息处理完成: {}", messageDto.getMessageId());
        } catch (Exception e) {
            logger.error("Fanout短信消息处理失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 消费Fanout队列推送消息
     * 
     * @param messageDto 消息内容
     */
    @RabbitListener(queues = RabbitConfig.FANOUT_QUEUE_PUSH)
    @RabbitHandler
    public void handleFanoutPushMessage(@Payload MessageDto messageDto) {
        try {
            logger.info("接收到Fanout推送消息: {}", messageDto);
            
            // 模拟推送业务处理
            processBusinessLogic(messageDto, "FANOUT_PUSH");
            
            logger.info("Fanout推送消息处理完成: {}", messageDto.getMessageId());
        } catch (Exception e) {
            logger.error("Fanout推送消息处理失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 消费Headers队列消息
     * 
     * @param messageDto 消息内容
     * @param headers 消息头信息
     */
    @RabbitListener(queues = RabbitConfig.HEADERS_QUEUE)
    @RabbitHandler
    public void handleHeadersMessage(@Payload MessageDto messageDto, 
                                   @Headers Map<String, Object> headers) {
        try {
            logger.info("接收到Headers消息: {}", messageDto);
            logger.info("消息头信息: {}", headers);
            
            String type = (String) headers.get("type");
            String priority = (String) headers.get("priority");
            
            // 根据header信息进行不同的业务处理
            processBusinessLogic(messageDto, "HEADERS_" + type + "_" + priority);
            
            logger.info("Headers消息处理完成: {}", messageDto.getMessageId());
        } catch (Exception e) {
            logger.error("Headers消息处理失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 消费死信队列消息
     * 
     * @param messageDto 消息内容
     * @param message 原始消息
     */
    @RabbitListener(queues = RabbitConfig.DLX_QUEUE)
    @RabbitHandler
    public void handleDeadLetterMessage(@Payload MessageDto messageDto, Message message) {
        try {
            logger.warn("接收到死信消息: {}", messageDto);
            logger.warn("死信原因: {}", message.getMessageProperties().getHeaders());
            
            // 死信消息的特殊处理逻辑
            processDeadLetterMessage(messageDto);
            
            logger.info("死信消息处理完成: {}", messageDto.getMessageId());
        } catch (Exception e) {
            logger.error("死信消息处理失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 模拟业务处理逻辑
     * 
     * @param messageDto 消息内容
     * @param processorType 处理器类型
     */
    private void processBusinessLogic(MessageDto messageDto, String processorType) {
        try {
            // 模拟业务处理时间
            Thread.sleep(100);
            
            logger.info("业务处理完成 - 处理器类型: {}, 消息ID: {}, 消息类型: {}", 
                    processorType, messageDto.getMessageId(), messageDto.getMessageType());
                    
            // 这里可以添加具体的业务逻辑
            switch (messageDto.getMessageType()) {
                case USER_MESSAGE:
                    handleUserBusinessLogic(messageDto);
                    break;
                case ORDER:
                    handleOrderBusinessLogic(messageDto);
                    break;
                case EMAIL:
                    handleEmailBusinessLogic(messageDto);
                    break;
                case SMS:
                    handleSmsBusinessLogic(messageDto);
                    break;
                default:
                    handleDefaultBusinessLogic(messageDto);
                    break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("业务处理被中断", e);
        } catch (Exception e) {
            logger.error("业务处理异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 处理用户相关业务逻辑
     */
    private void handleUserBusinessLogic(MessageDto messageDto) {
        logger.info("处理用户业务逻辑: {}", messageDto.getContent());
        // 用户相关业务处理
    }

    /**
     * 处理订单相关业务逻辑
     */
    private void handleOrderBusinessLogic(MessageDto messageDto) {
        logger.info("处理订单业务逻辑: {}", messageDto.getContent());
        // 订单相关业务处理
    }

    /**
     * 处理邮件相关业务逻辑
     */
    private void handleEmailBusinessLogic(MessageDto messageDto) {
        logger.info("处理邮件业务逻辑: {}", messageDto.getContent());
        // 邮件发送业务处理
    }

    /**
     * 处理短信相关业务逻辑
     */
    private void handleSmsBusinessLogic(MessageDto messageDto) {
        logger.info("处理短信业务逻辑: {}", messageDto.getContent());
        // 短信发送业务处理
    }

    /**
     * 处理默认业务逻辑
     */
    private void handleDefaultBusinessLogic(MessageDto messageDto) {
        logger.info("处理默认业务逻辑: {}", messageDto.getContent());
        // 默认业务处理
    }

    /**
     * 处理死信消息
     */
    private void processDeadLetterMessage(MessageDto messageDto) {
        logger.warn("处理死信消息，可能需要人工介入: {}", messageDto);
        
        // 死信消息的特殊处理，比如：
        // 1. 记录到数据库
        // 2. 发送报警通知
        // 3. 人工处理队列
        // 4. 重新投递到其他队列
    }
}

