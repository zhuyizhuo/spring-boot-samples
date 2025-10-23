package com.github.zhuyizhuo.ai.demo.model;

import lombok.Data;

/**
 * 聊天请求模型
 */
@Data
public class ChatRequest {
    private String conversationId;
    private String content;
    private String style; // formal, creative, technical, friendly, normal
    
    public ChatRequest() {
        this.style = "normal";
    }
}