package com.github.zhuyizhuo.kafka.demo.service;

import com.github.zhuyizhuo.kafka.demo.model.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Kafka消息生产者服务
 */
@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka-demo.topics.demo-topic}")
    private String demoTopic;
    
    @Value("${kafka-demo.topics.json-topic}")
    private String jsonTopic;
    
    @Value("${kafka-demo.topics.batch-topic}")
    private String batchTopic;
    
    /**
     * 发送简单字符串消息
     */
    public void sendStringMessage(String message) {
        sendMessageWithCallback(demoTopic, null, message);
    }
    
    /**
     * 发送JSON格式消息
     */
    public void sendJsonMessage(KafkaMessage message) {
        sendMessageWithCallback(jsonTopic, message.getId(), message);
    }
    
    /**
     * 批量发送消息
     */
    public boolean sendBatchMessages(List<KafkaMessage> messages, int timeoutSeconds) {
        CountDownLatch latch = new CountDownLatch(messages.size());
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (KafkaMessage message : messages) {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(batchTopic, message.getId(), message);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    successCount.incrementAndGet();
                } else {
                    System.err.println("消息发送失败: " + ex.getMessage());
                }
                latch.countDown();
            });
        }
        
        try {
            latch.await(timeoutSeconds, TimeUnit.SECONDS);
            return successCount.get() == messages.size();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * 发送带回调的消息
     */
    private void sendMessageWithCallback(String topic, String key, Object message) {
        CompletableFuture<SendResult<String, Object>> future;
        
        if (key != null) {
            future = kafkaTemplate.send(topic, key, message);
        } else {
            future = kafkaTemplate.send(topic, message);
        }
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("消息发送成功: " + result.getRecordMetadata());
            } else {
                System.err.println("消息发送失败: " + ex.getMessage());
            }
        });
    }
}