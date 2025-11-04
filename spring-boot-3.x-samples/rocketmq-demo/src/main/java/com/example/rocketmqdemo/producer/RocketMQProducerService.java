package com.example.rocketmqdemo.producer;

import com.example.rocketmqdemo.entity.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RocketMQ消息生产者服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RocketMQProducerService {

    private final RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.group}")
    private String producerGroup;
    
    // 重载方法：接受MessageDTO对象
    public String sendSyncMessage(String topic, MessageDTO messageDTO) {
        sendSyncMessage(topic, messageDTO.getContent(), messageDTO.getMessageType());
        return "OK";
    }
    
    // 重载方法：接受MessageDTO对象
    public void sendAsyncMessage(String topic, MessageDTO messageDTO) {
        sendAsyncMessage(topic, messageDTO.getContent(), messageDTO.getMessageType());
    }
    
    // 重载方法：接受MessageDTO对象
    public void sendOnewayMessage(String topic, MessageDTO messageDTO) {
        sendOnewayMessage(topic, messageDTO.getContent(), messageDTO.getMessageType());
    }
    
    // 重载方法：接受MessageDTO对象
    public String sendDelayMessage(String topic, MessageDTO messageDTO, int delayLevel) {
        sendDelayMessage(topic, messageDTO.getContent(), messageDTO.getMessageType(), delayLevel);
        return "OK";
    }

    /**
     * 发送同步消息
     * @param topic 主题
     * @param content 消息内容
     * @param messageType 消息类型
     */
    public void sendSyncMessage(String topic, String content, String messageType) {
        try {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(UUID.randomUUID().toString());
            messageDTO.setContent(content);
            messageDTO.setSendTime(LocalDateTime.now());
            messageDTO.setMessageType(messageType);

            Message<MessageDTO> message = MessageBuilder.withPayload(messageDTO).build();
            rocketMQTemplate.syncSend(topic, message);
            log.info("同步消息发送成功，主题：{}，消息ID：{}", topic, messageDTO.getId());
        } catch (Exception e) {
            log.error("同步消息发送失败，主题：{}", topic, e);
            throw e;
        }
    }

    /**
     * 发送异步消息
     * @param topic 主题
     * @param content 消息内容
     * @param messageType 消息类型
     */
    public void sendAsyncMessage(String topic, String content, String messageType) {
        try {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(UUID.randomUUID().toString());
            messageDTO.setContent(content);
            messageDTO.setSendTime(LocalDateTime.now());
            messageDTO.setMessageType(messageType);

            Message<MessageDTO> message = MessageBuilder.withPayload(messageDTO).build();
            rocketMQTemplate.asyncSend(topic, message, new org.apache.rocketmq.client.producer.SendCallback() {
                @Override
                public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {
                    log.info("异步消息发送成功，主题：{}，消息ID：{}，发送结果：{}", topic, messageDTO.getId(), sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    log.error("异步消息发送失败，主题：{}，消息ID：{}", topic, messageDTO.getId(), e);
                }
            });
            log.info("异步消息发送请求已提交，主题：{}，消息ID：{}", topic, messageDTO.getId());
        } catch (Exception e) {
            log.error("异步消息发送请求提交失败，主题：{}", topic, e);
            throw e;
        }
    }

    /**
     * 发送单向消息（不关心发送结果）
     * @param topic 主题
     * @param content 消息内容
     * @param messageType 消息类型
     */
    public void sendOnewayMessage(String topic, String content, String messageType) {
        try {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(UUID.randomUUID().toString());
            messageDTO.setContent(content);
            messageDTO.setSendTime(LocalDateTime.now());
            messageDTO.setMessageType(messageType);

            Message<MessageDTO> message = MessageBuilder.withPayload(messageDTO).build();
            rocketMQTemplate.sendOneWay(topic, message);
            log.info("单向消息发送成功（不等待确认），主题：{}，消息ID：{}", topic, messageDTO.getId());
        } catch (Exception e) {
            log.error("单向消息发送失败，主题：{}", topic, e);
            throw e;
        }
    }

    /**
     * 发送延时消息
     * @param topic 主题
     * @param content 消息内容
     * @param messageType 消息类型
     * @param delayLevel 延时级别（1-18）
     */
    public void sendDelayMessage(String topic, String content, String messageType, int delayLevel) {
        try {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(UUID.randomUUID().toString());
            messageDTO.setContent(content);
            messageDTO.setSendTime(LocalDateTime.now());
            messageDTO.setMessageType(messageType);

            Message<MessageDTO> message = MessageBuilder.withPayload(messageDTO).build();
            rocketMQTemplate.syncSend(topic, message, 3000, delayLevel);
            log.info("延时消息发送成功，主题：{}，消息ID：{}，延时级别：{}", topic, messageDTO.getId(), delayLevel);
        } catch (Exception e) {
            log.error("延时消息发送失败，主题：{}", topic, e);
            throw e;
        }
    }

    /**
     * 发送批量消息
     * @param topic 主题
     * @param messages 消息列表
     */
    public void sendBatchMessage(String topic, java.util.List<MessageDTO> messages) {
        try {
            // 设置消息ID和发送时间
            for (MessageDTO message : messages) {
                if (message.getId() == null) {
                    message.setId(UUID.randomUUID().toString());
                }
                if (message.getSendTime() == null) {
                    message.setSendTime(LocalDateTime.now());
                }
            }

            // 对每条消息单独发送，确保与其他发送方法保持一致的序列化/反序列化方式
            for (MessageDTO message : messages) {
                Message<MessageDTO> springMessage = MessageBuilder.withPayload(message).build();
                rocketMQTemplate.syncSend(topic, springMessage, 3000);
                log.info("批量消息单条发送成功，主题：{}，消息ID：{}", topic, message.getId());
            }
            
            log.info("批量消息全部发送成功，主题：{}，消息数量：{}", topic, messages.size());
        } catch (Exception e) {
            log.error("批量消息发送失败，主题：{}", topic, e);
            throw e;
        }
    }
}