package com.github.zhuyizhuo.springai.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * AI请求模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIRequest {
    
    /**
     * 模型类型：openai 或 ollama
     */
    private String modelType;
    
    /**
     * 提示词
     */
    private String prompt;
    
    /**
     * 可选的参数配置
     */
    private Map<String, Object> options;
    
    /**
     * 系统提示词
     */
    private String systemPrompt;
    
}