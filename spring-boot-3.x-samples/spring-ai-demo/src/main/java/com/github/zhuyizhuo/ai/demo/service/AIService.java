package com.github.zhuyizhuo.ai.demo.service;

import com.github.zhuyizhuo.ai.demo.model.*;

import java.util.List;
import java.util.Map;

/**
 * AI 服务接口
 */
public interface AIService {
    
    /**
     * 处理对话请求
     */
    AIMessage chat(AIMessage request, List<AIMessage> history, String style);
    
    /**
     * 生成代码
     */
    String generateCode(String prompt, String language, String requirements, boolean includeComments);
    
    /**
     * 解释代码
     */
    String explainCode(String code, String language);
    
    /**
     * 审查代码
     */
    String reviewCode(String code, String language, String requirements);
    
    /**
     * 生成文本摘要
     */
    String summarizeText(String text, int length);
    
    /**
     * 提取关键词
     */
    List<String> extractKeywords(String text, int count);
    
    /**
     * 翻译文本
     */
    String translateText(String text, String targetLanguage);
    
    /**
     * 生成邮件
     */
    String generateEmail(String topic, String audience, String tone, String details);
    
    /**
     * 创建文档大纲
     */
    String createOutline(String topic, String audience, int sections, String tone);
    
    /**
     * 制定学习计划
     */
    String createLearningPlan(String topic, String audience, int durationWeeks, String tone);
    
    /**
     * 检查AI服务连接状态
     */
    boolean checkConnection();
    
    /**
     * 获取使用统计
     */
    Map<String, Object> getUsageStats();
}