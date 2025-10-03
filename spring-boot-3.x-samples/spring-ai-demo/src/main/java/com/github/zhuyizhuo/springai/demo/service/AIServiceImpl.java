package com.github.zhuyizhuo.springai.demo.service;

import com.github.zhuyizhuo.springai.demo.model.AIRequest;
import com.github.zhuyizhuo.springai.demo.model.AIResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * AI服务实现类
 * 注意：当前版本为简化版，移除了对Spring AI依赖的引用
 * 在实际使用时，需要添加完整的Spring AI依赖并取消相应注释
 */
@Service
public class AIServiceImpl implements AIService {
    
    private static final Logger logger = Logger.getLogger(AIServiceImpl.class.getName());
    
    @Override
    public AIResponse generate(AIRequest request) {
        try {
            if (request == null || request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                return AIResponse.error("请求参数不能为空");
            }
            
            String modelType = request.getModelType() == null ? "openai" : request.getModelType().toLowerCase();
            String prompt = request.getPrompt();
            
            logger.info("处理AI请求: 模型类型=" + modelType + ", 提示=" + prompt);
            
            // 简化版返回模拟数据
            String responseContent = "模拟的AI响应: " + prompt;
            
            Map<String, Object> modelInfo = new HashMap<>();
            modelInfo.put("model", modelType.equals("ollama") ? "llama3" : "gpt-3.5-turbo");
            modelInfo.put("provider", modelType.equals("ollama") ? "Ollama (Local)" : "OpenAI");
            
            return AIResponse.success(responseContent, modelType, modelInfo);
        } catch (Exception e) {
            logger.severe("AI生成失败: " + e.getMessage());
            return AIResponse.error("AI生成失败: " + e.getMessage());
        }
    }
    
    @Override
    public String generateWithOpenAI(String prompt) {
        logger.info("Generating content with OpenAI: " + prompt);
        return "模拟的OpenAI响应: " + prompt;
    }
    
    @Override
    public String generateWithOllama(String prompt) {
        logger.info("Generating content with Ollama: " + prompt);
        return "模拟的Ollama响应: " + prompt;
    }
    
    @Override
    public String retrieve(String query) {
        logger.info("Performing vector store retrieval with query: " + query);
        return "模拟的检索结果: " + query;
    }
}