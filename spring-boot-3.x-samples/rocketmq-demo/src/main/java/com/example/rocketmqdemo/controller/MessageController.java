package com.example.rocketmqdemo.controller;

import com.example.rocketmqdemo.entity.MessageDTO;
import com.example.rocketmqdemo.producer.RocketMQProducerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息发送API控制器
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    private final RocketMQProducerService rocketMQProducerService;

    /**
     * 发送同步消息
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, String>> sendSyncMessage(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String messageType = request.getOrDefault("messageType", "TEXT");
        String topic = request.getOrDefault("topic", "demo-topic");

        rocketMQProducerService.sendSyncMessage(topic, content, messageType);
        return ResponseEntity.ok(Map.of("status", "success", "message", "同步消息发送成功"));
    }

    /**
     * 发送异步消息
     */
    @PostMapping("/async")
    public ResponseEntity<Map<String, String>> sendAsyncMessage(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String messageType = request.getOrDefault("messageType", "TEXT");
        String topic = request.getOrDefault("topic", "demo-topic");

        rocketMQProducerService.sendAsyncMessage(topic, content, messageType);
        return ResponseEntity.ok(Map.of("status", "success", "message", "异步消息发送请求已提交"));
    }

    /**
     * 发送单向消息
     */
    @PostMapping("/oneway")
    public ResponseEntity<Map<String, String>> sendOnewayMessage(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String messageType = request.getOrDefault("messageType", "TEXT");
        String topic = request.getOrDefault("topic", "demo-topic");

        rocketMQProducerService.sendOnewayMessage(topic, content, messageType);
        return ResponseEntity.ok(Map.of("status", "success", "message", "单向消息发送成功"));
    }

    /**
     * 发送延时消息
     */
    @PostMapping("/delay")
    public ResponseEntity<Map<String, String>> sendDelayMessage(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String messageType = request.getOrDefault("messageType", "TEXT");
        String topic = request.getOrDefault("topic", "demo-delay-topic");
        int delayLevel = Integer.parseInt(request.getOrDefault("delayLevel", "3"));

        rocketMQProducerService.sendDelayMessage(topic, content, messageType, delayLevel);
        return ResponseEntity.ok(Map.of("status", "success", "message", "延时消息发送成功"));
    }

    /**
     * 发送批量消息
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, String>> sendBatchMessage(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<java.util.LinkedHashMap<String, Object>> rawMessages = (List<java.util.LinkedHashMap<String, Object>>) request.get("messages");
        String topic = request.getOrDefault("topic", "demo-topic").toString();
        
        // 转换为MessageDTO对象列表
        List<MessageDTO> messages = new java.util.ArrayList<>();
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        
        for (java.util.LinkedHashMap<String, Object> rawMessage : rawMessages) {
            try {
                MessageDTO messageDTO = mapper.convertValue(rawMessage, MessageDTO.class);
                messages.add(messageDTO);
            } catch (Exception e) {
                log.error("转换消息对象失败", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "error", "message", "消息格式错误"));
            }
        }

        rocketMQProducerService.sendBatchMessage(topic, messages);
        return ResponseEntity.ok(Map.of("status", "success", "message", "批量消息发送成功"));
    }

    /**
     * 获取延时级别说明
     */
    @GetMapping("/delay-levels")
    public ResponseEntity<Map<String, String>> getDelayLevels() {
        return ResponseEntity.ok(Map.ofEntries(
                Map.entry("1", "1s"),
                Map.entry("2", "5s"),
                Map.entry("3", "10s"),
                Map.entry("4", "30s"),
                Map.entry("5", "1m"),
                Map.entry("6", "2m"),
                Map.entry("7", "3m"),
                Map.entry("8", "4m"),
                Map.entry("9", "5m"),
                Map.entry("10", "6m"),
                Map.entry("11", "7m"),
                Map.entry("12", "8m"),
                Map.entry("13", "9m"),
                Map.entry("14", "10m"),
                Map.entry("15", "20m"),
                Map.entry("16", "30m"),
                Map.entry("17", "1h"),
                Map.entry("18", "2h")
        ));
    }
}