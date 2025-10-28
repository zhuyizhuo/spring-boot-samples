package com.github.zhuyizhuo.kafka.demo.listener;

import com.github.zhuyizhuo.kafka.demo.model.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Kafka消费者监听器
 */
@Component
public class KafkaConsumerListener {

    // 用于存储接收到的消息计数
    private final ConcurrentHashMap<String, AtomicInteger> messageCountMap = new ConcurrentHashMap<>();
    
    @Value("${kafka-demo.topics.demo-topic}")
    private String demoTopic;
    
    @Value("${kafka-demo.topics.json-topic}")
    private String jsonTopic;
    
    @Value("${kafka-demo.topics.batch-topic}")
    private String batchTopic;
    
    /**
     * 监听简单字符串消息
     */
    @KafkaListener(topics = "${kafka-demo.topics.demo-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenStringMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            String message = record.value();
            System.out.println("接收到字符串消息: " + message + ", 分区: " + record.partition() + ", 偏移量: " + record.offset());
            
            // 更新消息计数
            messageCountMap.computeIfAbsent("stringMessage", k -> new AtomicInteger(0)).incrementAndGet();
            
            // 手动确认消息
            acknowledgment.acknowledge();
        } catch (Exception e) {
            System.err.println("处理字符串消息异常: " + e.getMessage());
        }
    }
    
    /**
     * 监听JSON格式消息
     */
    @KafkaListener(topics = "${kafka-demo.topics.json-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenJsonMessage(ConsumerRecord<String, KafkaMessage> record, Acknowledgment acknowledgment) {
        try {
            KafkaMessage message = record.value();
            System.out.println("接收到JSON消息: " + message + ", 分区: " + record.partition() + ", 偏移量: " + record.offset());
            
            // 更新消息计数
            messageCountMap.computeIfAbsent("jsonMessage", k -> new AtomicInteger(0)).incrementAndGet();
            
            // 手动确认消息
            acknowledgment.acknowledge();
        } catch (Exception e) {
            System.err.println("处理JSON消息异常: " + e.getMessage());
        }
    }
    
    /**
     * 批量监听消息
     */
    @KafkaListener(topics = "${kafka-demo.topics.batch-topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "batchKafkaListenerContainerFactory")
    public void listenBatchMessages(List<ConsumerRecord<String, KafkaMessage>> records, Acknowledgment acknowledgment) {
        try {
            System.out.println("接收到批量消息，数量: " + records.size());
            
            for (ConsumerRecord<String, KafkaMessage> record : records) {
                KafkaMessage message = record.value();
                System.out.println("批量消息内容: " + message + ", 分区: " + record.partition() + ", 偏移量: " + record.offset());
            }
            
            // 更新消息计数
            messageCountMap.computeIfAbsent("batchMessage", k -> new AtomicInteger(0)).addAndGet(records.size());
            
            // 手动确认所有消息
            acknowledgment.acknowledge();
        } catch (Exception e) {
            System.err.println("处理批量消息异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取消息接收统计信息
     */
    public ConcurrentHashMap<String, AtomicInteger> getMessageCountMap() {
        return messageCountMap;
    }
}