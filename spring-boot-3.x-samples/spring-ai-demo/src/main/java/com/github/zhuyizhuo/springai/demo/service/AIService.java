package com.github.zhuyizhuo.springai.demo.service;

import com.github.zhuyizhuo.springai.demo.model.AIRequest;
import com.github.zhuyizhuo.springai.demo.model.AIResponse;

/**
 * AI服务接口
 */
public interface AIService {
    
    /**
     * 发送AI请求并获取响应
     * @param request AI请求对象
     * @return AI响应对象
     */
    AIResponse generate(AIRequest request);
    
    /**
     * 使用OpenAI模型生成响应
     * @param prompt 提示词
     * @return 生成的内容
     */
    String generateWithOpenAI(String prompt);
    
    /**
     * 使用Ollama本地模型生成响应
     * @param prompt 提示词
     * @return 生成的内容
     */
    String generateWithOllama(String prompt);
    
    /**
     * 进行嵌入向量检索
     * @param query 查询文本
     * @return 检索结果
     */
    String retrieve(String query);
    
}