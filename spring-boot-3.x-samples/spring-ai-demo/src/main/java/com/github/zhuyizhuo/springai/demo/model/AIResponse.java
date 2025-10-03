package com.github.zhuyizhuo.springai.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI响应模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIResponse {
    
    /**
     * 响应内容
     */
    private String content;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 错误信息（如果有）
     */
    private String errorMessage;
    
    /**
     * 响应时间
     */
    private LocalDateTime timestamp;
    
    /**
     * 模型使用的配置信息
     */
    private Map<String, Object> modelInfo;
    
    /**
     * 响应类型
     */
    private String modelType;
    
    /**
     * 构建成功响应
     */
    public static AIResponse success(String content, String modelType, Map<String, Object> modelInfo) {
        AIResponse response = new AIResponse();
        response.setContent(content);
        response.setSuccess(true);
        response.setTimestamp(LocalDateTime.now());
        response.setModelType(modelType);
        response.setModelInfo(modelInfo);
        return response;
    }
    
    /**
     * 构建失败响应
     */
    public static AIResponse error(String errorMessage) {
        AIResponse response = new AIResponse();
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
}