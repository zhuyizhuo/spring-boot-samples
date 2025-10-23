package com.github.zhuyizhuo.kafka.demo.controller;

import com.github.zhuyizhuo.kafka.demo.model.KafkaMessage;
import com.github.zhuyizhuo.kafka.demo.service.KafkaProducerService;
import com.github.zhuyizhuo.kafka.demo.listener.KafkaConsumerListener;
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
public class HomeController {

    @Autowired
    private KafkaProducerService producerService;
    
    @Autowired
    private KafkaConsumerListener consumerListener;
    
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
        
        model.addAttribute("stats", stats);
        return "index";
    }
    
    /**
     * 发送字符串消息
     */
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
}