package com.github.zhuyizhuo.ai.demo.service;

import com.github.zhuyizhuo.ai.demo.model.AIMessage;
import com.github.zhuyizhuo.ai.demo.model.Conversation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 会话管理服务
 */
@Service
public class ConversationService {

    @Value("${app.conversation-history-size:20}")
    private int maxHistorySize;
    
    // 使用ConcurrentHashMap保证线程安全
    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();
    
    /**
     * 创建新会话
     */
    public Conversation createConversation(String name) {
        String conversationName = name != null && !name.isEmpty() ? name : "对话 " + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        Conversation conversation = new Conversation(conversationName);
        conversations.put(conversation.getId(), conversation);
        return conversation;
    }
    
    /**
     * 获取会话
     */
    public Conversation getConversation(String id) {
        return conversations.get(id);
    }
    
    /**
     * 获取所有会话列表
     */
    public List<Conversation> getAllConversations() {
        return conversations.values().stream()
                .sorted(Comparator.comparing(Conversation::getUpdatedAt).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * 更新会话
     */
    public void updateConversation(Conversation conversation) {
        conversations.put(conversation.getId(), conversation);
    }
    
    /**
     * 删除会话
     */
    public void deleteConversation(String id) {
        conversations.remove(id);
    }
    
    /**
     * 添加消息到会话
     */
    public void addMessage(String conversationId, AIMessage message) {
        Conversation conversation = getConversation(conversationId);
        if (conversation != null) {
            conversation.addMessage(message);
            
            // 限制历史消息数量
            if (conversation.getMessages().size() > maxHistorySize) {
                // 保留系统消息，只移除最旧的用户和助手消息对
                List<AIMessage> messages = conversation.getMessages();
                List<AIMessage> systemMessages = messages.stream()
                        .filter(m -> "system".equals(m.getRole()))
                        .collect(Collectors.toList());
                
                List<AIMessage> userAndAssistantMessages = messages.stream()
                        .filter(m -> !"system".equals(m.getRole()))
                        .collect(Collectors.toList());
                
                // 计算需要保留的非系统消息数量（总历史记录大小减去系统消息数量）
                int keepNonSystemCount = Math.max(0, maxHistorySize - systemMessages.size());
                
                if (keepNonSystemCount > 0 && keepNonSystemCount < userAndAssistantMessages.size()) {
                    // 保留最新的消息
                    userAndAssistantMessages = userAndAssistantMessages.subList(
                            userAndAssistantMessages.size() - keepNonSystemCount,
                            userAndAssistantMessages.size());
                }
                
                // 重建消息列表
                List<AIMessage> newMessages = new ArrayList<>(systemMessages);
                newMessages.addAll(userAndAssistantMessages);
                conversation.setMessages(newMessages);
                conversation.setMessageCount(newMessages.size());
            }
        }
    }
    
    /**
     * 清空会话消息
     */
    public void clearConversation(String conversationId) {
        Conversation conversation = getConversation(conversationId);
        if (conversation != null) {
            conversation.clearMessages();
        }
    }
    
    /**
     * 重命名会话
     */
    public void renameConversation(String conversationId, String newName) {
        Conversation conversation = getConversation(conversationId);
        if (conversation != null) {
            conversation.rename(newName);
        }
    }
    
    /**
     * 获取会话历史（最近的N条消息）
     */
    public List<AIMessage> getRecentMessages(String conversationId, int limit) {
        Conversation conversation = getConversation(conversationId);
        if (conversation != null && !conversation.getMessages().isEmpty()) {
            List<AIMessage> messages = conversation.getMessages();
            if (messages.size() <= limit) {
                return new ArrayList<>(messages);
            } else {
                return new ArrayList<>(messages.subList(messages.size() - limit, messages.size()));
            }
        }
        return Collections.emptyList();
    }
    
    /**
     * 保存会话到缓存
     */
    public void saveConversation(Conversation conversation) {
        conversations.put(conversation.getId(), conversation);
    }
    
    /**
     * 统计会话数量
     */
    public int getConversationCount() {
        return conversations.size();
    }
    
    /**
     * 查找包含特定内容的会话
     */
    public List<Conversation> searchConversations(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllConversations();
        }
        
        String lowerKeyword = keyword.toLowerCase();
        return conversations.values().stream()
                .filter(conv -> conv.getName().toLowerCase().contains(lowerKeyword) ||
                        conv.getMessages().stream().anyMatch(msg -> 
                                msg.getContent() != null && 
                                msg.getContent().toLowerCase().contains(lowerKeyword)))
                .sorted(Comparator.comparing(Conversation::getUpdatedAt).reversed())
                .collect(Collectors.toList());
    }
}