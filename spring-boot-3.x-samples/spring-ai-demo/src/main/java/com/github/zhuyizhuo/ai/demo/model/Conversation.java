package com.github.zhuyizhuo.ai.demo.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 对话会话模型类
 */
@Data
public class Conversation {
    private String id;
    private String name;
    private List<AIMessage> messages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String initialPrompt;
    private String currentStyle;
    private int messageCount;
    
    public Conversation() {
        this.id = UUID.randomUUID().toString();
        this.messages = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.messageCount = 0;
        this.currentStyle = "normal";
    }
    
    public Conversation(String name) {
        this();
        this.name = name;
    }
    
    public void addMessage(AIMessage message) {
        messages.add(message);
        message.setConversationId(this.id);
        messageCount++;
        updatedAt = LocalDateTime.now();
    }
    
    public void clearMessages() {
        messages.clear();
        messageCount = 0;
        updatedAt = LocalDateTime.now();
    }
    
    public void rename(String newName) {
        this.name = newName;
        updatedAt = LocalDateTime.now();
    }
}