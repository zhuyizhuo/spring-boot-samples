package com.github.zhuyizhuo.ai.demo.service;

import com.github.zhuyizhuo.ai.demo.model.AIMessage;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatPromptTemplate;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * OpenAI 服务实现类
 */
@Service
public class OpenAIServiceImpl implements AIService {

    @Autowired
    private ChatClient chatClient;
    
    @Value("${app.max-requests-per-minute:60}")
    private int maxRequestsPerMinute;
    
    // 简单的请求计数器（实际应用中可使用更复杂的限流机制）
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private long lastResetTime = System.currentTimeMillis();
    
    // 对话历史缓存
    private final Map<String, List<AIMessage>> conversationCache = new HashMap<>();
    
    @Override
    public AIMessage chat(AIMessage request, List<AIMessage> history, String style) {
        System.out.println("[OpenAIServiceImpl] ===== 开始处理聊天请求 =====");
        System.out.println("[OpenAIServiceImpl] 请求内容: " + request.getContent());
        System.out.println("[OpenAIServiceImpl] 回复风格: " + style);
        System.out.println("[OpenAIServiceImpl] 历史消息数量: " + (history != null ? history.size() : 0));
        
        checkRateLimit();
        
        AIMessage responseMessage = new AIMessage();
        responseMessage.setRole("assistant");
        
        try {
            // 构建系统提示
            String systemPrompt = getSystemPromptByStyle(style);
            System.out.println("[OpenAIServiceImpl] 系统提示: " + systemPrompt);
            
            // 构建消息列表
            List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
            messages.add(new org.springframework.ai.chat.messages.SystemMessage(systemPrompt));
            
            // 添加历史消息
            if (history != null) {
                System.out.println("[OpenAIServiceImpl] 处理历史消息:");
                for (int i = 0; i < history.size(); i++) {
                    AIMessage msg = history.get(i);
                    System.out.println("[OpenAIServiceImpl] 历史消息[" + i + "] - " + msg.getRole() + ": " + msg.getContent());
                    if ("user".equals(msg.getRole())) {
                        messages.add(new org.springframework.ai.chat.messages.UserMessage(msg.getContent()));
                    } else if ("assistant".equals(msg.getRole())) {
                        messages.add(new org.springframework.ai.chat.messages.AssistantMessage(msg.getContent()));
                    }
                }
            }
            
            // 添加当前用户消息
            System.out.println("[OpenAIServiceImpl] 添加用户消息: " + request.getContent());
            messages.add(new org.springframework.ai.chat.messages.UserMessage(request.getContent()));
            
            System.out.println("[OpenAIServiceImpl] 准备调用AI服务...");
            System.out.println("[OpenAIServiceImpl] 消息总数: " + messages.size());
            System.out.println("[OpenAIServiceImpl] 使用模型配置（从application.yml）...");
            
            // 使用ChatClient直接传递消息列表，它会自动使用配置的模型
            // ChatClient会从配置中读取模型名称
            String content = chatClient.prompt()
                    .messages(messages)
                    .call()
                    .content();
            
            System.out.println("[OpenAIServiceImpl] AI服务调用成功!");
            System.out.println("[OpenAIServiceImpl] 收到回复长度: " + (content != null ? content.length() : 0));
            
            responseMessage.setContent(content);
            responseMessage.setModelUsed("gpt-3.5-turbo");
            
        } catch (Exception e) {
            System.out.println("[OpenAIServiceImpl] ===== AI服务调用失败 =====");
            System.out.println("[OpenAIServiceImpl] 异常类型: " + e.getClass().getName());
            System.out.println("[OpenAIServiceImpl] 异常消息: " + e.getMessage());
            System.out.println("[OpenAIServiceImpl] 完整异常堆栈:");
            e.printStackTrace();
            
            responseMessage.setError(true);
            responseMessage.setErrorMessage("AI服务调用失败: " + e.getMessage());
            responseMessage.setContent("抱歉，处理您的请求时遇到了问题。请稍后重试。");
        }
        
        System.out.println("[OpenAIServiceImpl] ===== 聊天请求处理完成 =====");
        return responseMessage;
    }
    
    @Override
    public String generateCode(String prompt, String language, String requirements, boolean includeComments) {
        checkRateLimit();
        
        String systemPrompt = "你是一个专业的" + language + "程序员。请根据用户的要求生成高质量的" + language + "代码。";
        if (includeComments) {
            systemPrompt += " 请包含详细的注释说明代码功能和关键逻辑。";
        }
        
        String userPrompt = "请生成" + language + "代码，满足以下需求：\n" + prompt;
        if (requirements != null && !requirements.isEmpty()) {
            userPrompt += "\n详细要求：" + requirements;
        }
        
        userPrompt += "\n请只返回代码，不要包含其他解释文本。";
        
        try {
            ChatPromptTemplate promptTemplate = new ChatPromptTemplate(
                    List.of(
                            new SystemPromptTemplate(systemPrompt),
                            new SystemPromptTemplate(userPrompt)
                    )
            );
            
            Prompt promptObj = promptTemplate.create();
            return chatClient.prompt(promptObj).call().content();
        } catch (Exception e) {
            return "生成代码失败: " + e.getMessage();
        }
    }
    
    @Override
    public String explainCode(String code, String language) {
        checkRateLimit();
        
        String prompt = "请详细解释以下" + language + "代码的功能、结构和逻辑。" +
                "包括代码的主要目的、关键部分的作用、使用的数据结构和算法，" +
                "以及可能的优化建议。\n\n" + code;
        
        try {
            PromptTemplate promptTemplate = new PromptTemplate(prompt);
            return chatClient.prompt(promptTemplate.create()).call().content();
        } catch (Exception e) {
            return "解释代码失败: " + e.getMessage();
        }
    }
    
    @Override
    public String reviewCode(String code, String language, String requirements) {
        checkRateLimit();
        
        String prompt = "请审查以下" + language + "代码，检查其中的问题并提供改进建议。\n";
        if (requirements != null && !requirements.isEmpty()) {
            prompt += "代码应满足以下需求：" + requirements + "\n\n";
        }
        prompt += code;
        prompt += "\n\n请分析代码的：1) 潜在错误和Bug 2) 代码质量和可读性 3) 性能优化建议 4) 安全性考虑 5) 最佳实践遵循情况";
        
        try {
            PromptTemplate promptTemplate = new PromptTemplate(prompt);
            return chatClient.prompt(promptTemplate.create()).call().content();
        } catch (Exception e) {
            return "审查代码失败: " + e.getMessage();
        }
    }
    
    @Override
    public String summarizeText(String text, int length) {
        checkRateLimit();
        
        String lengthDesc = switch (length) {
            case 1 -> "简短";  // 短摘要
            case 3 -> "详细";  // 详细摘要
            default -> "中等";  // 默认中等长度
        };
        
        String prompt = "请生成以下文本的" + lengthDesc + "摘要，保留关键信息和核心观点。\n\n" + text;
        
        try {
            PromptTemplate promptTemplate = new PromptTemplate(prompt);
            return chatClient.prompt(promptTemplate.create()).call().content();
        } catch (Exception e) {
            return "生成摘要失败: " + e.getMessage();
        }
    }
    
    @Override
    public List<String> extractKeywords(String text, int count) {
        checkRateLimit();
        
        String prompt = "请从以下文本中提取" + count + "个最相关的关键词，用逗号分隔。\n\n" + text + "\n\n请只返回关键词列表，不要其他文本。";
        
        try {
            PromptTemplate promptTemplate = new PromptTemplate(prompt);
            String keywordsStr = chatClient.prompt(promptTemplate.create()).call().content();
            return Arrays.stream(keywordsStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of("提取关键词失败: " + e.getMessage());
        }
    }
    
    @Override
    public String translateText(String text, String targetLanguage) {
        checkRateLimit();
        
        String prompt = "请将以下文本翻译成" + targetLanguage + "。\n\n" + text + "\n\n请只返回翻译后的文本，不要其他说明。";
        
        try {
            PromptTemplate promptTemplate = new PromptTemplate(prompt);
            return chatClient.prompt(promptTemplate.create()).call().content();
        } catch (Exception e) {
            return "翻译失败: " + e.getMessage();
        }
    }
    
    @Override
    public String generateEmail(String topic, String audience, String tone, String details) {
        checkRateLimit();
        
        String prompt = "请生成一封" + tone + "语气的电子邮件，主题为：" + topic + "\n" +
                "收件人：" + audience + "\n" +
                "邮件内容需要包含以下信息：" + details + "\n" +
                "请生成完整的邮件，包括主题行、称呼、正文和结束语。";
        
        try {
            PromptTemplate promptTemplate = new PromptTemplate(prompt);
            return chatClient.prompt(promptTemplate.create()).call().content();
        } catch (Exception e) {
            return "生成邮件失败: " + e.getMessage();
        }
    }
    
    @Override
    public String createOutline(String topic, String audience, int sections, String tone) {
        checkRateLimit();
        
        String prompt = "请为主题\"" + topic + "\"创建一个详细的文档大纲，针对" + audience + "读者，使用" + tone + "的语气。\n" +
                "大纲应包含" + sections + "个主要部分，每个部分应包含适当的子标题。\n" +
                "请确保大纲结构清晰，逻辑连贯，涵盖主题的所有重要方面。";
        
        try {
            PromptTemplate promptTemplate = new PromptTemplate(prompt);
            return chatClient.prompt(promptTemplate.create()).call().content();
        } catch (Exception e) {
            return "创建大纲失败: " + e.getMessage();
        }
    }
    
    @Override
    public String createLearningPlan(String topic, String audience, int durationWeeks, String tone) {
        checkRateLimit();
        
        String prompt = "请为\"" + topic + "\"创建一个" + durationWeeks + "周的学习计划，针对" + audience + "学习者，使用" + tone + "的语气。\n" +
                "学习计划应包括每周的学习目标、推荐资源、实践活动和复习内容。\n" +
                "请确保计划循序渐进，由浅入深，并且包含足够的实践环节。";
        
        try {
            PromptTemplate promptTemplate = new PromptTemplate(prompt);
            return chatClient.prompt(promptTemplate.create()).call().content();
        } catch (Exception e) {
            return "创建学习计划失败: " + e.getMessage();
        }
    }
    
    @Override
    public boolean checkConnection() {
        System.out.println("=== [CHECK-CONNECTION] 开始检查AI连接 ===");
        System.out.println("=== [CHECK-CONNECTION] 当前时间: " + new Date() + " ===");
        
        try {
            // 发送一个简单的请求来测试连接，使用更合理的提示词
            System.out.println("=== [CHECK-CONNECTION] 发送测试请求到AI服务 ===");
            String testPrompt = "你好，请回复'连接正常'";
            PromptTemplate promptTemplate = new PromptTemplate(testPrompt);
            
            System.out.println("=== [CHECK-CONNECTION] 构建的提示内容: " + testPrompt + " ===");
            System.out.println("=== [CHECK-CONNECTION] 开始调用chatClient.prompt() ===");
            
            String response = chatClient.prompt(promptTemplate.create()).call().content();
            
            System.out.println("=== [CHECK-CONNECTION] 收到AI响应 ===");
            System.out.println("=== [CHECK-CONNECTION] 响应内容长度: " + (response != null ? response.length() : "null") + " ===");
            System.out.println("=== [CHECK-CONNECTION] 响应内容预览: " + (response != null && response.length() > 100 ? response.substring(0, 100) + "..." : response) + " ===");
            
            boolean result = response != null && !response.isEmpty();
            System.out.println("=== [CHECK-CONNECTION] 连接检查结果: " + result + " ===");
            System.out.println("=== [CHECK-CONNECTION] 检查完成 ===");
            
            return result;
        } catch (Exception e) {
            System.out.println("=== [CHECK-CONNECTION] 连接检查异常 ===");
            System.out.println("=== [CHECK-CONNECTION] 异常类型: " + e.getClass().getName() + " ===");
            System.out.println("=== [CHECK-CONNECTION] 异常消息: " + e.getMessage() + " ===");
            
            // 如果是404错误，可能是模型名称或API端点问题
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                System.out.println("=== [CHECK-CONNECTION] 检测到404错误，可能是模型名称或API端点配置问题 ===");
            }
            
            System.out.println("=== [CHECK-CONNECTION] 连接检查失败，返回false ===");
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getUsageStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequests", requestCount.get());
        stats.put("maxRequestsPerMinute", maxRequestsPerMinute);
        stats.put("conversationCount", conversationCache.size());
        stats.put("lastResetTime", new Date(lastResetTime));
        return stats;
    }
    
    /**
     * 根据风格获取系统提示
     */
    private String getSystemPromptByStyle(String style) {
        return switch (style.toLowerCase()) {
            case "formal" -> "你是一位专业、正式的助手，使用礼貌和规范的语言回答问题。";
            case "creative" -> "你是一位富有创造力的助手，喜欢用生动有趣的语言和独特的视角回答问题。";
            case "technical" -> "你是一位技术专家，擅长深入解释技术概念，提供准确的技术信息。";
            case "friendly" -> "你是一位友好的助手，使用轻松愉快的语气与用户交流。";
            default -> "你是一位全能的AI助手，能够回答各种问题并提供帮助。";
        };
    }
    
    /**
     * 检查并执行速率限制
     */
    private void checkRateLimit() {
        long currentTime = System.currentTimeMillis();
        // 每分钟重置计数器
        if (currentTime - lastResetTime > 60000) {
            requestCount.set(0);
            lastResetTime = currentTime;
        }
        
        int count = requestCount.incrementAndGet();
        if (count > maxRequestsPerMinute) {
            throw new RuntimeException("请求过于频繁，请稍后再试。");
        }
    }
}