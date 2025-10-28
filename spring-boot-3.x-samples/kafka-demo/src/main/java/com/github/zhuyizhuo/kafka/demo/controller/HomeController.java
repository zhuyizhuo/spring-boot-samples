package com.github.zhuyizhuo.kafka.demo.controller;

import com.github.zhuyizhuo.kafka.demo.model.KafkaMessage;
import com.github.zhuyizhuo.kafka.demo.service.KafkaProducerService;
import com.github.zhuyizhuo.kafka.demo.service.KafkaTopicService;
import com.github.zhuyizhuo.kafka.demo.listener.KafkaConsumerListener;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 首页控制器
 */
@Controller
@Tag(name = "Kafka消息管理", description = "提供Kafka消息发送和统计功能")
public class HomeController {

    @Autowired
    private KafkaProducerService producerService;
    
    @Autowired
    private KafkaConsumerListener consumerListener;
    
    @Autowired
    private KafkaTopicService topicService;
    
    /**
     * 首页
     */
    @GetMapping("/")
    public String home(Model model) {
        // 获取消息接收统计信息
        ConcurrentHashMap<String, AtomicInteger> countMap = consumerListener.getMessageCountMap();
        
        Map<String, Integer> stats = new HashMap<>();
        stats.put("stringMessage", countMap.getOrDefault("stringMessage", new AtomicInteger(0)).get());
        stats.put("jsonMessage", countMap.getOrDefault("jsonMessage", new AtomicInteger(0)).get());
        stats.put("batchMessage", countMap.getOrDefault("batchMessage", new AtomicInteger(0)).get());
        
        // 获取Kafka主题列表
        try {
            List<String> topics = topicService.listTopics();
            model.addAttribute("topics", topics);
        } catch (Exception e) {
            System.err.println("获取主题列表失败: " + e.getMessage());
            model.addAttribute("topics", new ArrayList<>());
        }
        
        model.addAttribute("stats", stats);
        return "index";
    }
    
    /**
     * 发送字符串消息
     */
    @Operation(summary = "发送字符串消息", description = "向Kafka发送简单的字符串消息")
    @PostMapping("/send/string")
    @ResponseBody
    public Map<String, Object> sendStringMessage(@RequestParam String message) {
        Map<String, Object> result = new HashMap<>();
        try {
            producerService.sendStringMessage(message);
            result.put("success", true);
            result.put("message", "字符串消息发送成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发送失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 发送JSON消息
     */
    @Operation(summary = "发送JSON消息", description = "向Kafka发送结构化的JSON格式消息")
    @PostMapping("/send/json")
    @ResponseBody
    public Map<String, Object> sendJsonMessage(@RequestParam String content, @RequestParam String sender) {
        Map<String, Object> result = new HashMap<>();
        try {
            KafkaMessage message = new KafkaMessage(content, sender);
            producerService.sendJsonMessage(message);
            result.put("success", true);
            result.put("message", "JSON消息发送成功");
            result.put("data", message);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发送失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 批量发送消息
     */
    @Operation(summary = "批量发送消息", description = "批量向Kafka发送多条消息")
    @PostMapping("/send/batch")
    @ResponseBody
    public Map<String, Object> sendBatchMessages(
            @RequestParam String content,
            @RequestParam String sender,
            @RequestParam(defaultValue = "10") int count) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            List<KafkaMessage> messages = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                messages.add(new KafkaMessage(content + " (" + i + ")", sender));
            }
            
            boolean success = producerService.sendBatchMessages(messages, 10);
            
            if (success) {
                result.put("success", true);
                result.put("message", "批量消息发送成功，共发送 " + count + " 条消息");
            } else {
                result.put("success", false);
                result.put("message", "部分消息发送失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发送失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取消息接收统计
     */
    @Operation(summary = "获取消息统计", description = "获取各类消息的接收统计数据")
    @GetMapping("/api/stats")
    @ResponseBody
    public Map<String, Integer> getStats() {
        ConcurrentHashMap<String, AtomicInteger> countMap = consumerListener.getMessageCountMap();
        
        Map<String, Integer> stats = new HashMap<>();
        stats.put("stringMessage", countMap.getOrDefault("stringMessage", new AtomicInteger(0)).get());
        stats.put("jsonMessage", countMap.getOrDefault("jsonMessage", new AtomicInteger(0)).get());
        stats.put("batchMessage", countMap.getOrDefault("batchMessage", new AtomicInteger(0)).get());
        
        return stats;
    }
    
    /**
     * 获取Kafka主题列表
     */
    @Operation(summary = "获取主题列表", description = "获取Kafka中的所有主题")
    @GetMapping("/api/topics")
    @ResponseBody
    public Map<String, Object> getTopics() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<String> topics = topicService.listTopics();
            result.put("success", true);
            result.put("topics", topics);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取主题列表失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 创建Kafka主题
     */
    @Operation(summary = "创建主题", description = "在Kafka中创建新主题")
    @PostMapping("/api/topic/create")
    @ResponseBody
    public Map<String, Object> createTopic(@RequestParam String topicName) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (topicName == null || topicName.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "主题名称不能为空");
                return result;
            }
            
            boolean created = topicService.createTopic(topicName.trim());
            if (created) {
                result.put("success", true);
                result.put("message", "主题创建成功");
            } else {
                result.put("success", false);
                result.put("message", "主题已存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建主题失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 删除Kafka主题
     */
    @Operation(summary = "删除主题", description = "从Kafka中删除指定主题")
    @PostMapping("/api/topic/delete")
    @ResponseBody
    public Map<String, Object> deleteTopic(@RequestParam String topicName) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean deleted = topicService.deleteTopic(topicName);
            if (deleted) {
                result.put("success", true);
                result.put("message", "主题删除成功");
            } else {
                result.put("success", false);
                result.put("message", "主题不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除主题失败: " + e.getMessage());
        }
        return result;
    }
}