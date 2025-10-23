package com.github.zhuyizhuo.ai.demo.model;

import lombok.Data;

/**
 * 健康状态模型
 */
@Data
public class HealthStatus {
    private String status;
    private boolean aiConnected;
    private int conversationCount;
    private String timestamp = java.time.LocalDateTime.now().toString();
}