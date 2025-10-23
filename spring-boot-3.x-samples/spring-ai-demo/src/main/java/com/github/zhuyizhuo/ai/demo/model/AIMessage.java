package com.github.zhuyizhuo.ai.demo.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI 消息模型类
 */
@Data
public class AIMessage {
    private String id;
    private String role; // user, assistant, system
    private String content;
    private String style; // formal, creative, technical, etc.
    private LocalDateTime timestamp;
    private String conversationId;
    private String modelUsed;
    private boolean error = false;
    private String errorMessage;
    
    public AIMessage() {
        this.timestamp = LocalDateTime.now();
    }
    
    public AIMessage(String role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
}