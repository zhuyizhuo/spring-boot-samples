package com.github.zhuyizhuo.ai.demo.controller;

import com.github.zhuyizhuo.ai.demo.model.ChatRequest;
import com.github.zhuyizhuo.ai.demo.model.CodeResponse;
import com.github.zhuyizhuo.ai.demo.model.ContentResponse;
import com.github.zhuyizhuo.ai.demo.model.HealthStatus;
import com.github.zhuyizhuo.ai.demo.model.UtilityResponse;
import com.github.zhuyizhuo.ai.demo.service.SpringAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spring AI 控制器类
 * 处理所有AI相关的HTTP请求
 */
@Controller
@RequestMapping("/api")
public class SpringAiController {

    @Autowired
    private SpringAiService springAiService;

    // 计数器，用于追踪请求数量
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicInteger requestsPerMinute = new AtomicInteger(0);
    private long lastMinuteCheck = System.currentTimeMillis();

    /**
     * 获取仪表板页面
     */
    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        updateRequestCount();
        
        model.addAttribute("requestCount", requestCount.get());
        model.addAttribute("conversationCount", springAiService.getActiveConversationCount());
        model.addAttribute("requestsPerMinute", requestsPerMinute.get());
        
        return "dashboard";
    }

    /**
     * 获取聊天页面
     */
    @GetMapping("/chat")
    public String getChatPage() {
        updateRequestCount();
        return "chat";
    }

    /**
     * 获取代码工具页面
     */
    @GetMapping("/code")
    public String getCodePage() {
        updateRequestCount();
        return "code";
    }

    /**
     * 获取内容处理页面
     */
    @GetMapping("/content")
    public String getContentPage() {
        updateRequestCount();
        return "content";
    }

    /**
     * 获取实用工具页面
     */
    @GetMapping("/utility")
    public String getUtilityPage() {
        updateRequestCount();
        return "utility";
    }

    /**
     * 处理聊天请求
     */
    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest chatRequest) {
        updateRequestCount();
        
        try {
            // 如果没有提供会话ID，生成一个新的
            if (chatRequest.getConversationId() == null || chatRequest.getConversationId().isEmpty()) {
                chatRequest.setConversationId(UUID.randomUUID().toString());
            }
            
            String response = springAiService.processChat(chatRequest);
            
            Map<String, String> result = new HashMap<>();
            result.put("response", response);
            result.put("conversationId", chatRequest.getConversationId());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "处理聊天请求时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 处理代码相关请求
     */
    @PostMapping("/code")
    @ResponseBody
    public ResponseEntity<CodeResponse> code(@RequestBody Map<String, Object> requestData) {
        updateRequestCount();
        
        try {
            CodeResponse response = springAiService.processCode(requestData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CodeResponse errorResponse = new CodeResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("处理代码请求时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 处理内容相关请求
     */
    @PostMapping("/content")
    @ResponseBody
    public ResponseEntity<ContentResponse> content(@RequestBody Map<String, Object> requestData) {
        updateRequestCount();
        
        try {
            ContentResponse response = springAiService.processContent(requestData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ContentResponse errorResponse = new ContentResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("处理内容请求时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 处理实用工具请求
     */
    @PostMapping("/utility")
    @ResponseBody
    public ResponseEntity<UtilityResponse> utility(@RequestBody Map<String, Object> requestData) {
        updateRequestCount();
        
        try {
            UtilityResponse response = springAiService.processUtility(requestData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            UtilityResponse errorResponse = new UtilityResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("处理工具请求时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 清除会话历史
     */
    @DeleteMapping("/conversation")
    @ResponseBody
    public ResponseEntity<Map<String, String>> clearConversation(@RequestParam String conversationId) {
        updateRequestCount();
        
        try {
            springAiService.clearConversation(conversationId);
            Map<String, String> result = new HashMap<>();
            result.put("message", "会话已成功清除");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "清除会话时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 获取系统健康状态
     */
    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<HealthStatus> getHealthStatus() {
        updateRequestCount();
        
        try {
            HealthStatus status = new HealthStatus();
            status.setStatus("UP");
            status.setAiConnected(true); // 这里可以添加实际的连接检查逻辑
            status.setConversationCount(springAiService.getActiveConversationCount());
            status.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            HealthStatus errorStatus = new HealthStatus();
            errorStatus.setStatus("DOWN");
            errorStatus.setAiConnected(false);
            errorStatus.setConversationCount(0);
            errorStatus.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorStatus);
        }
    }

    /**
     * 更新请求计数
     */
    private synchronized void updateRequestCount() {
        requestCount.incrementAndGet();
        
        // 更新每分钟请求数
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMinuteCheck >= 60000) { // 60秒
            requestsPerMinute.set(1);
            lastMinuteCheck = currentTime;
        } else {
            requestsPerMinute.incrementAndGet();
        }
    }
}