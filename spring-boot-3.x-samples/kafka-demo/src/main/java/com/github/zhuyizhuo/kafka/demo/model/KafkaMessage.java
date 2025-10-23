package com.github.zhuyizhuo.kafka.demo.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Kafka消息模型类
 */
public class KafkaMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String id;
    private String content;
    private String sender;
    private LocalDateTime timestamp;
    
    public KafkaMessage() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }
    
    public KafkaMessage(String content, String sender) {
        this();
        this.content = content;
        this.sender = sender;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "KafkaMessage{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}