package com.github.zhuyizhuo.ai.demo.service;

import com.github.zhuyizhuo.ai.demo.model.ChatRequest;
import com.github.zhuyizhuo.ai.demo.model.CodeResponse;
import com.github.zhuyizhuo.ai.demo.model.ContentResponse;
import com.github.zhuyizhuo.ai.demo.model.UtilityResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring AI 服务实现类
 * 提供智能聊天、代码工具、内容处理和实用工具功能
 */
@Service
public class SpringAiService {

    @Autowired
    private ChatClient chatClient;

    // 存储会话历史的Map，使用ConcurrentHashMap保证线程安全
    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    /**
     * 处理智能聊天请求
     * @param chatRequest 聊天请求
     * @return 聊天响应
     */
    public String processChat(ChatRequest chatRequest) {
        String conversationId = chatRequest.getConversationId();
        String content = chatRequest.getContent();
        String style = chatRequest.getStyle();

        System.out.println("=== [DEBUG] 开始处理聊天请求 ===");
        System.out.println("=== [DEBUG] 会话ID: " + conversationId);
        System.out.println("=== [DEBUG] 用户输入: " + content);
        System.out.println("=== [DEBUG] 回复风格: " + style);

        // 获取或创建会话历史
        List<Message> messages = conversationHistory.computeIfAbsent(conversationId, k -> new ArrayList<>());
        System.out.println("=== [DEBUG] 当前会话历史条数: " + messages.size());

        // 构建提示，根据不同的风格添加额外指令
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("用户问题: " + content + "\n");

        if (!"normal".equals(style)) {
            switch (style) {
                case "professional":
                    promptBuilder.append("请以专业、正式的语气回答这个问题。");
                    break;
                case "friendly":
                    promptBuilder.append("请以友好、平易近人的语气回答这个问题。");
                    break;
                case "humorous":
                    promptBuilder.append("请以幽默、轻松的语气回答这个问题。");
                    break;
                case "technical":
                    promptBuilder.append("请提供技术细节丰富的专业回答，包括具体实现方法和示例。");
                    break;
            }
        }

        String finalPrompt = promptBuilder.toString();
        System.out.println("=== [DEBUG] 构建的完整提示: " + finalPrompt);

        // 添加用户消息到历史记录
        UserMessage userMessage = new UserMessage(finalPrompt);
        messages.add(userMessage);
        System.out.println("=== [DEBUG] 已添加用户消息到历史记录");

        // 限制历史消息数量，防止上下文过长
        if (messages.size() > 10) {
            messages = messages.subList(messages.size() - 10, messages.size());
            conversationHistory.put(conversationId, messages);
            System.out.println("=== [DEBUG] 历史消息已截断到最新10条");
        }

        // 创建提示并发送请求
        Prompt prompt = new Prompt(messages);
        System.out.println("=== [DEBUG] 准备发送AI请求...");
        try {
            String response = chatClient.prompt(prompt).call().content();
            System.out.println("=== [DEBUG] AI响应成功: " + response);
            System.out.println("=== [DEBUG] 请求处理完成 ===");
            
            // 保留历史记录，以便下次交互
            // 注意：实际应用中可能需要对历史记录进行更复杂的管理
            
            return response;
        } catch (Exception e) {
            System.out.println("=== [DEBUG] AI请求失败: " + e.getMessage());
            System.out.println("=== [DEBUG] 异常类型: " + e.getClass().getName());
            e.printStackTrace();
            System.out.println("=== [DEBUG] 请求处理失败 ===");
            throw new RuntimeException("AI服务调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理代码相关请求
     * @param requestData 请求数据
     * @return 代码响应
     */
    public CodeResponse processCode(Map<String, Object> requestData) {
        CodeResponse response = new CodeResponse();
        String action = (String) requestData.get("action");
        
        try {
            String result = "";
            switch (action) {
                case "generate":
                    result = generateCode(requestData);
                    break;
                case "explain":
                    result = explainCode(requestData);
                    break;
                case "review":
                    result = reviewCode(requestData);
                    break;
                default:
                    response.setSuccess(false);
                    response.setError("不支持的代码操作类型: " + action);
                    return response;
            }
            
            response.setSuccess(true);
            response.setAction(action);
            response.setResult(result);
            
            // 尝试提取语言信息
            if (requestData.containsKey("language")) {
                response.setLanguage((String) requestData.get("language"));
            }
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setError("处理代码时发生错误: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * 生成代码
     */
    private String generateCode(Map<String, Object> requestData) {
        String language = (String) requestData.get("language");
        // 支持 prompt 或 description 字段
        String description = (String) requestData.getOrDefault("prompt", requestData.get("description"));
        String framework = requestData.containsKey("framework") ? (String) requestData.get("framework") : "";
        String complexity = requestData.containsKey("complexity") ? (String) requestData.get("complexity") : "medium";
        
        // 处理额外要求
        boolean requireComments = requestData.containsKey("requireComments") && 
                                  (Boolean) requestData.getOrDefault("requireComments", false);
        boolean requireErrorHandling = requestData.containsKey("requireErrorHandling") && 
                                       (Boolean) requestData.getOrDefault("requireErrorHandling", false);
        boolean requireTestCases = requestData.containsKey("requireTestCases") && 
                                   (Boolean) requestData.getOrDefault("requireTestCases", false);

        StringBuilder prompt = new StringBuilder();
        prompt.append("请生成").append(language).append("代码，");
        
        if (!framework.isEmpty()) {
            prompt.append("使用").append(framework).append("框架，");
        }
        
        prompt.append("复杂度为").append(complexity).append("，");
        prompt.append("实现以下功能：\n\n");
        prompt.append(description).append("\n\n");
        
        // 添加额外要求
        if (requireComments) {
            prompt.append("请包含详细的注释说明代码功能和关键逻辑。\n");
        }
        if (requireErrorHandling) {
            prompt.append("请包含适当的错误处理机制。\n");
        }
        if (requireTestCases) {
            prompt.append("请包含测试用例或使用示例。\n");
        }
        
        prompt.append("请提供完整的、可运行的代码。");

        // 使用ChatClient直接传递消息，而不是PromptTemplate
        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
        messages.add(new org.springframework.ai.chat.messages.SystemMessage(
            "你是一个专业的" + language + "程序员。请根据用户的要求生成高质量的" + language + "代码。"));
        messages.add(new org.springframework.ai.chat.messages.UserMessage(prompt.toString()));
        
        return chatClient.prompt()
                .messages(messages)
                .call()
                .content();
    }

    /**
     * 解释代码
     */
    private String explainCode(Map<String, Object> requestData) {
        String code = (String) requestData.get("code");
        String language = (String) requestData.get("language");

        StringBuilder prompt = new StringBuilder();
        prompt.append("请详细解释以下").append(language).append("代码的功能、工作原理和关键部分：\n\n");
        prompt.append(code).append("\n\n");
        prompt.append("请按照以下结构进行解释：\n");
        prompt.append("1. 代码总体功能\n");
        prompt.append("2. 关键算法或逻辑\n");
        prompt.append("3. 重要函数或方法的作用\n");
        prompt.append("4. 可能的优化建议（如果有）");

        // 使用ChatClient直接传递消息
        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
        messages.add(new org.springframework.ai.chat.messages.SystemMessage(
            "你是一个专业的" + language + "代码解释专家，擅长详细解释代码的功能和实现原理。"));
        messages.add(new org.springframework.ai.chat.messages.UserMessage(prompt.toString()));
        
        return chatClient.prompt()
                .messages(messages)
                .call()
                .content();
    }

    /**
     * 审查代码
     */
    private String reviewCode(Map<String, Object> requestData) {
        String code = (String) requestData.get("code");
        String language = (String) requestData.get("language");
        String focus = requestData.containsKey("focus") ? (String) requestData.get("focus") : "all";

        StringBuilder prompt = new StringBuilder();
        prompt.append("请对以下").append(language).append("代码进行审查：\n\n");
        prompt.append(code).append("\n\n");
        prompt.append("请重点关注：\n");
        
        switch (focus) {
            case "performance":
                prompt.append("- 性能优化问题\n");
                prompt.append("- 时间和空间复杂度分析\n");
                break;
            case "security":
                prompt.append("- 潜在的安全漏洞\n");
                prompt.append("- 数据验证和输入处理\n");
                break;
            case "readability":
                prompt.append("- 代码可读性和风格\n");
                prompt.append("- 命名规范和注释质量\n");
                break;
            default:
                prompt.append("- 代码质量和最佳实践\n");
                prompt.append("- 潜在的性能问题\n");
                prompt.append("- 安全漏洞\n");
                prompt.append("- 代码可读性和可维护性\n");
                prompt.append("- 可能的优化建议");
        }

        // 使用ChatClient直接传递消息
        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
        messages.add(new org.springframework.ai.chat.messages.SystemMessage(
            "你是一个专业的" + language + "代码审查专家，擅长发现代码中的问题并提供改进建议。"));
        messages.add(new org.springframework.ai.chat.messages.UserMessage(prompt.toString()));
        
        return chatClient.prompt()
                .messages(messages)
                .call()
                .content();
    }

    /**
     * 处理内容相关请求
     * @param requestData 请求数据
     * @return 内容响应
     */
    public ContentResponse processContent(Map<String, Object> requestData) {
        ContentResponse response = new ContentResponse();
        String action = (String) requestData.get("action");
        
        try {
            String result = "";
            List<String> keywords = null;
            
            switch (action) {
                case "summarize":
                    result = summarizeText(requestData);
                    break;
                case "keywords":
                    result = extractKeywords(requestData);
                    // 解析关键词结果
                    keywords = parseKeywords(result);
                    break;
                case "translate":
                    result = translateText(requestData);
                    break;
                default:
                    response.setSuccess(false);
                    response.setError("不支持的内容操作类型: " + action);
                    return response;
            }
            
            response.setSuccess(true);
            response.setAction(action);
            response.setResult(result);
            response.setKeywords(keywords);
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setError("处理内容时发生错误: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * 文本摘要
     */
    private String summarizeText(Map<String, Object> requestData) {
        String text = (String) requestData.get("text");
        String length = (String) requestData.get("length");
        String format = (String) requestData.get("format");
        boolean focusMain = (boolean) requestData.get("focusMain");
        boolean focusDetails = (boolean) requestData.get("focusDetails");
        boolean focusConclusion = (boolean) requestData.get("focusConclusion");

        StringBuilder prompt = new StringBuilder();
        prompt.append("请为以下文本生成摘要：\n\n");
        prompt.append(text).append("\n\n");
        prompt.append("摘要要求：\n");
        
        switch (length) {
            case "short":
                prompt.append("- 简短摘要（1-2段）\n");
                break;
            case "long":
                prompt.append("- 详细摘要（5段以上）\n");
                break;
            default:
                prompt.append("- 中等长度摘要（3-5段）\n");
        }
        
        switch (format) {
            case "bullet":
                prompt.append("- 以要点形式呈现\n");
                break;
            case "both":
                prompt.append("- 同时提供段落形式和要点形式\n");
                break;
            default:
                prompt.append("- 以段落形式呈现\n");
        }
        
        prompt.append("- 重点关注：");
        List<String> focuses = new ArrayList<>();
        if (focusMain) focuses.add("主要观点");
        if (focusDetails) focuses.add("关键细节");
        if (focusConclusion) focuses.add("结论");
        prompt.append(String.join("、", focuses)).append("\n");
        
        prompt.append("- 保持原文的核心信息\n");
        prompt.append("- 使用简洁明了的语言");

        PromptTemplate promptTemplate = new PromptTemplate(prompt.toString());
        return chatClient.prompt(promptTemplate.create()).call().content();
    }

    /**
     * 提取关键词
     */
    private String extractKeywords(Map<String, Object> requestData) {
        String text = (String) requestData.get("text");
        int count = (int) requestData.get("count");
        String type = (String) requestData.get("type");
        boolean includeWeights = (boolean) requestData.get("includeWeights");
        boolean groupSynonyms = (boolean) requestData.get("groupSynonyms");

        StringBuilder prompt = new StringBuilder();
        prompt.append("请从以下文本中提取关键词：\n\n");
        prompt.append(text).append("\n\n");
        prompt.append("提取要求：\n");
        prompt.append("- 提取").append(count).append("个最相关的关键词\n");
        
        switch (type) {
            case "noun":
                prompt.append("- 仅提取名词关键词\n");
                break;
            case "phrase":
                prompt.append("- 优先提取短语关键词\n");
                break;
            default:
                prompt.append("- 提取全部类型的关键词\n");
        }
        
        if (includeWeights) {
            prompt.append("- 为每个关键词提供相关度权重（0-100）\n");
        }
        
        if (groupSynonyms) {
            prompt.append("- 合并同义词，选择最具代表性的词\n");
        }
        
        prompt.append("- 关键词应该能够准确反映文本的主题和核心内容\n");
        prompt.append("- 请以列表形式返回，每个关键词占一行");

        PromptTemplate promptTemplate = new PromptTemplate(prompt.toString());
        return chatClient.prompt(promptTemplate.create()).call().content();
    }

    /**
     * 解析关键词结果
     */
    private List<String> parseKeywords(String result) {
        List<String> keywords = new ArrayList<>();
        String[] lines = result.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                // 移除可能的编号和权重信息
                line = line.replaceAll("^\\d+[.)]\\s*", "");
                line = line.replaceAll("\\s*\\(\\d+\\)$", "");
                line = line.replaceAll("^[-•]\\s*", "");
                
                if (!line.isEmpty()) {
                    keywords.add(line);
                }
            }
        }
        
        return keywords;
    }

    /**
     * 文本翻译
     */
    private String translateText(Map<String, Object> requestData) {
        String text = (String) requestData.get("text");
        String sourceLanguage = (String) requestData.get("sourceLanguage");
        String targetLanguage = (String) requestData.get("targetLanguage");
        String type = (String) requestData.get("type");

        StringBuilder prompt = new StringBuilder();
        prompt.append("请将以下文本从");
        
        if ("auto".equals(sourceLanguage)) {
            prompt.append("自动检测的语言");
        } else {
            prompt.append(getLanguageName(sourceLanguage));
        }
        
        prompt.append("翻译成").append(getLanguageName(targetLanguage)).append("：\n\n");
        prompt.append(text).append("\n\n");
        
        prompt.append("翻译要求：\n");
        switch (type) {
            case "formal":
                prompt.append("- 使用正式、专业的语言风格\n");
                break;
            case "casual":
                prompt.append("- 使用口语化、非正式的语言风格\n");
                break;
            default:
                prompt.append("- 使用标准、自然的语言风格\n");
        }
        
        prompt.append("- 保持原文的意思和情感\n");
        prompt.append("- 确保翻译流畅自然\n");
        prompt.append("- 请只返回翻译结果，不要添加其他解释");

        PromptTemplate promptTemplate = new PromptTemplate(prompt.toString());
        return chatClient.prompt(promptTemplate.create()).call().content();
    }

    /**
     * 获取语言名称
     */
    private String getLanguageName(String code) {
        Map<String, String> languageMap = new HashMap<>();
        languageMap.put("zh", "中文");
        languageMap.put("en", "英语");
        languageMap.put("ja", "日语");
        languageMap.put("ko", "韩语");
        languageMap.put("fr", "法语");
        languageMap.put("de", "德语");
        languageMap.put("es", "西班牙语");
        languageMap.put("ru", "俄语");
        
        return languageMap.getOrDefault(code, code);
    }

    /**
     * 处理实用工具请求
     * @param requestData 请求数据
     * @return 工具响应
     */
    public UtilityResponse processUtility(Map<String, Object> requestData) {
        UtilityResponse response = new UtilityResponse();
        String action = (String) requestData.get("action");
        
        try {
            String result = "";
            
            switch (action) {
                case "email":
                    result = generateEmail(requestData);
                    break;
                case "outline":
                    result = generateOutline(requestData);
                    break;
                case "learning-plan":
                    result = generateLearningPlan(requestData);
                    break;
                default:
                    response.setSuccess(false);
                    response.setError("不支持的工具操作类型: " + action);
                    return response;
            }
            
            response.setSuccess(true);
            response.setAction(action);
            response.setResult(result);
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setError("处理工具请求时发生错误: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * 生成邮件
     */
    private String generateEmail(Map<String, Object> requestData) {
        String type = (String) requestData.get("type");
        String recipient = (String) requestData.get("recipient");
        String purpose = (String) requestData.get("purpose");
        String tone = (String) requestData.get("tone");
        String length = (String) requestData.get("length");
        boolean includeGreeting = (boolean) requestData.get("includeGreeting");
        boolean includeSignature = (boolean) requestData.get("includeSignature");
        boolean includeAction = (boolean) requestData.get("includeAction");

        StringBuilder prompt = new StringBuilder();
        prompt.append("请生成一封");
        
        switch (type) {
            case "business":
                prompt.append("商务");
                break;
            case "job-application":
                prompt.append("求职申请");
                break;
            case "follow-up":
                prompt.append("跟进");
                break;
            case "thank-you":
                prompt.append("感谢");
                break;
            case "complaint":
                prompt.append("投诉");
                break;
            default:
                prompt.append("普通");
        }
        
        prompt.append("邮件，收件人是").append(recipient).append("，");
        prompt.append("语气风格为").append(getToneDescription(tone)).append("，");
        prompt.append("长度为").append(getLengthDescription(length)).append("。\n\n");
        
        prompt.append("邮件目的：\n");
        prompt.append(purpose).append("\n\n");
        
        prompt.append("邮件要求：\n");
        
        if (includeGreeting) {
            prompt.append("- 包含适当的问候语\n");
        }
        
        if (includeSignature) {
            prompt.append("- 包含结束语和签名\n");
        }
        
        if (includeAction) {
            prompt.append("- 明确的行动呼吁\n");
        }
        
        prompt.append("- 语言清晰、简洁、专业\n");
        prompt.append("- 逻辑结构合理，重点突出\n");
        prompt.append("- 请同时提供合适的邮件主题行\n");
        prompt.append("- 以完整的邮件格式返回，包括主题和正文");

        PromptTemplate promptTemplate = new PromptTemplate(prompt.toString());
        return chatClient.prompt(promptTemplate.create()).call().content();
    }

    /**
     * 生成大纲
     */
    private String generateOutline(Map<String, Object> requestData) {
        String topic = (String) requestData.get("topic");
        String purpose = (String) requestData.get("purpose");
        String audience = (String) requestData.get("audience");
        String keyPoints = (String) requestData.get("keyPoints");
        String depth = (String) requestData.get("depth");
        String structure = (String) requestData.get("structure");

        StringBuilder prompt = new StringBuilder();
        prompt.append("请为以下主题创建内容大纲：\n\n");
        prompt.append("主题：").append(topic).append("\n\n");
        
        prompt.append("大纲要求：\n");
        prompt.append("- 目的：").append(getPurposeDescription(purpose)).append("\n");
        
        if (!audience.isEmpty()) {
            prompt.append("- 目标受众：").append(audience).append("\n");
        }
        
        if (!keyPoints.isEmpty()) {
            prompt.append("- 必须包含的关键点：").append(keyPoints).append("\n");
        }
        
        prompt.append("- 深度：").append(getDepthDescription(depth)).append("\n");
        prompt.append("- 结构类型：").append(getStructureDescription(structure)).append("\n");
        prompt.append("- 逻辑清晰，层次分明\n");
        prompt.append("- 每个部分提供简洁明了的标题\n");
        prompt.append("- 以层级列表形式返回大纲");

        PromptTemplate promptTemplate = new PromptTemplate(prompt.toString());
        return chatClient.prompt(promptTemplate.create()).call().content();
    }

    /**
     * 生成学习计划
     */
    private String generateLearningPlan(Map<String, Object> requestData) {
        String topic = (String) requestData.get("topic");
        String level = (String) requestData.get("level");
        String goal = (String) requestData.get("goal");
        int duration = (int) requestData.get("duration");
        String durationUnit = (String) requestData.get("durationUnit");
        int weeklyHours = (int) requestData.get("weeklyHours");
        Map<String, Boolean> resources = (Map<String, Boolean>) requestData.get("resources");

        StringBuilder prompt = new StringBuilder();
        prompt.append("请为以下学习主题创建详细的学习计划：\n\n");
        prompt.append("学习主题：").append(topic).append("\n");
        prompt.append("当前水平：").append(getLevelDescription(level)).append("\n");
        prompt.append("学习目标：").append(goal).append("\n");
        prompt.append("学习周期：").append(duration).append(" ").append(
                "week".equals(durationUnit) ? "周" : "月").append("\n");
        prompt.append("每周学习时间：").append(weeklyHours).append("小时\n\n");
        
        List<String> preferredResources = new ArrayList<>();
        if (resources.containsKey("video") && resources.get("video")) preferredResources.add("视频教程");
        if (resources.containsKey("article") && resources.get("article")) preferredResources.add("文章/文档");
        if (resources.containsKey("practice") && resources.get("practice")) preferredResources.add("实践项目");
        if (resources.containsKey("book") && resources.get("book")) preferredResources.add("书籍");
        
        if (!preferredResources.isEmpty()) {
            prompt.append("偏好的学习资源：").append(String.join("、", preferredResources)).append("\n\n");
        }
        
        prompt.append("学习计划要求：\n");
        prompt.append("1. 包含明确的学习目标和预期成果\n");
        prompt.append("2. 提供分阶段的学习路径，每周或每月的具体学习内容\n");
        prompt.append("3. 推荐适合的学习资源（考虑偏好的资源类型）\n");
        prompt.append("4. 包含实践项目或练习建议\n");
        prompt.append("5. 提供进度评估和反馈的方法\n");
        prompt.append("6. 计划应该循序渐进，从基础到进阶\n");
        prompt.append("7. 时间安排合理，考虑每周可用学习时间");

        PromptTemplate promptTemplate = new PromptTemplate(prompt.toString());
        return chatClient.prompt(promptTemplate.create()).call().content();
    }

    /**
     * 辅助方法：获取语气描述
     */
    private String getToneDescription(String tone) {
        switch (tone) {
            case "formal": return "正式"; 
            case "friendly": return "友好"; 
            case "casual": return "随意"; 
            case "persuasive": return "说服力强"; 
            default: return "专业";
        }
    }

    /**
     * 辅助方法：获取长度描述
     */
    private String getLengthDescription(String length) {
        switch (length) {
            case "short": return "简短"; 
            case "detailed": return "详细"; 
            default: return "中等";
        }
    }

    /**
     * 辅助方法：获取目的描述
     */
    private String getPurposeDescription(String purpose) {
        switch (purpose) {
            case "persuasive": return "说服他人"; 
            case "instructional": return "指导说明"; 
            case "research": return "研究报告"; 
            case "creative": return "创意写作"; 
            default: return "提供信息";
        }
    }

    /**
     * 辅助方法：获取深度描述
     */
    private String getDepthDescription(String depth) {
        switch (depth) {
            case "basic": return "基本（2级层次）"; 
            case "detailed": return "详细（4级或更多层次）"; 
            default: return "中级（3级层次）";
        }
    }

    /**
     * 辅助方法：获取结构描述
     */
    private String getStructureDescription(String structure) {
        switch (structure) {
            case "chronological": return "时间顺序"; 
            case "problem-solution": return "问题-解决方案"; 
            case "comparison": return "比较对照"; 
            case "hierarchical": return "层级结构"; 
            default: return "逻辑顺序";
        }
    }

    /**
     * 辅助方法：获取水平描述
     */
    private String getLevelDescription(String level) {
        switch (level) {
            case "intermediate": return "中级"; 
            case "advanced": return "高级"; 
            default: return "初学者";
        }
    }

    /**
     * 清理指定会话的历史记录
     * @param conversationId 会话ID
     */
    public void clearConversation(String conversationId) {
        conversationHistory.remove(conversationId);
    }

    /**
     * 获取活跃会话数量
     * @return 会话数量
     */
    public int getActiveConversationCount() {
        return conversationHistory.size();
    }
}