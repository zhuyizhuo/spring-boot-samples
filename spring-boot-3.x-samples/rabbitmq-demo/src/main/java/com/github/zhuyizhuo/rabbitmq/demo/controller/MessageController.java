package com.github.zhuyizhuo.rabbitmq.demo.controller;

import com.github.zhuyizhuo.rabbitmq.demo.model.MessageDto;
import com.github.zhuyizhuo.rabbitmq.demo.model.MessageType;
import com.github.zhuyizhuo.rabbitmq.demo.service.MessageProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息发送控制器
 * 提供各种类型的消息发送API接口
 * 
 * @author zhuyizhuo
 * @version 1.0
 * @date 2025/10/02
 */
@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "消息管理", description = "RabbitMQ消息发送相关接口")
public class MessageController {

    @Autowired
    private MessageProducerService messageProducerService;

    /**
     * 发送Direct类型消息
     */
    @PostMapping("/direct")
    @Operation(summary = "发送Direct消息", description = "发送到Direct交换机的消息")
    public ResponseEntity<Map<String, Object>> sendDirectMessage(
            @Valid @RequestBody MessageDto messageDto) {
        
        messageProducerService.sendDirectMessage(messageDto);
        
        Map<String, Object> response = createSuccessResponse("Direct消息发送成功", messageDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 发送Topic类型消息
     */
    @PostMapping("/topic")
    @Operation(summary = "发送Topic消息", description = "发送到Topic交换机的消息，支持自定义routing key")
    public ResponseEntity<Map<String, Object>> sendTopicMessage(
            @Valid @RequestBody MessageDto messageDto,
            @Parameter(description = "路由键") @RequestParam("routingKey") String routingKey) {
        
        messageProducerService.sendTopicMessage(messageDto, routingKey);
        
        Map<String, Object> response = createSuccessResponse("Topic消息发送成功", messageDto);
        response.put("routingKey", routingKey);
        return ResponseEntity.ok(response);
    }

    /**
     * 发送用户相关消息
     */
    @PostMapping("/topic/user")
    @Operation(summary = "发送用户消息", description = "发送用户相关的Topic消息")
    public ResponseEntity<Map<String, Object>> sendUserMessage(
            @Valid @RequestBody MessageDto messageDto) {
        
        messageDto.setMessageType(MessageType.USER_MESSAGE);
        messageProducerService.sendUserMessage(messageDto);
        
        Map<String, Object> response = createSuccessResponse("用户消息发送成功", messageDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 发送订单相关消息
     */
    @PostMapping("/topic/order")
    @Operation(summary = "发送订单消息", description = "发送订单相关的Topic消息")
    public ResponseEntity<Map<String, Object>> sendOrderMessage(
            @Valid @RequestBody MessageDto messageDto) {
        
        messageDto.setMessageType(MessageType.ORDER);
        messageProducerService.sendOrderMessage(messageDto);
        
        Map<String, Object> response = createSuccessResponse("订单消息发送成功", messageDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 发送Fanout类型消息（广播消息）
     */
    @PostMapping("/fanout")
    @Operation(summary = "发送Fanout消息", description = "发送广播消息到所有绑定的队列")
    public ResponseEntity<Map<String, Object>> sendFanoutMessage(
            @Valid @RequestBody MessageDto messageDto) {
        
        messageProducerService.sendFanoutMessage(messageDto);
        
        Map<String, Object> response = createSuccessResponse("Fanout广播消息发送成功", messageDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 发送Headers类型消息
     */
    @PostMapping("/headers")
    @Operation(summary = "发送Headers消息", description = "发送基于Headers匹配的消息")
    public ResponseEntity<Map<String, Object>> sendHeadersMessage(
            @Valid @RequestBody MessageDto messageDto,
            @Parameter(description = "消息类型") @RequestParam("type") String type,
            @Parameter(description = "优先级") @RequestParam("priority") String priority) {
        
        messageProducerService.sendHeadersMessage(messageDto, type, priority);
        
        Map<String, Object> response = createSuccessResponse("Headers消息发送成功", messageDto);
        response.put("headers", Map.of("type", type, "priority", priority));
        return ResponseEntity.ok(response);
    }

    /**
     * 发送延迟消息
     */
    @PostMapping("/delayed")
    @Operation(summary = "发送延迟消息", description = "发送带有TTL的延迟消息")
    public ResponseEntity<Map<String, Object>> sendDelayedMessage(
            @Valid @RequestBody MessageDto messageDto,
            @Parameter(description = "延迟秒数") @RequestParam("delaySeconds") int delaySeconds) {
        
        messageProducerService.sendDelayedMessage(messageDto, delaySeconds);
        
        Map<String, Object> response = createSuccessResponse("延迟消息发送成功", messageDto);
        response.put("delaySeconds", delaySeconds);
        return ResponseEntity.ok(response);
    }

    /**
     * 批量发送消息
     */
    @PostMapping("/batch")
    @Operation(summary = "批量发送消息", description = "批量发送多个消息")
    public ResponseEntity<Map<String, Object>> sendBatchMessages(
            @Valid @RequestBody MessageDto[] messages) {
        
        messageProducerService.sendBatchMessages(messages);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "批量消息发送成功");
        response.put("count", messages.length);
        return ResponseEntity.ok(response);
    }

    /**
     * 发送通知消息
     */
    @PostMapping("/notification")
    @Operation(summary = "发送通知消息", description = "发送系统通知消息")
    public ResponseEntity<Map<String, Object>> sendNotificationMessage(
            @Parameter(description = "通知内容") @RequestParam("content") String content,
            @Parameter(description = "接收者") @RequestParam(value = "receiver", required = false) String receiver) {
        
        MessageDto messageDto = new MessageDto(content, MessageType.NOTIFICATION);
        messageDto.setReceiver(receiver);
        messageDto.setSender("SYSTEM");
        
        messageProducerService.sendFanoutMessage(messageDto);
        
        Map<String, Object> response = createSuccessResponse("通知消息发送成功", messageDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 发送邮件消息
     */
    @PostMapping("/email")
    @Operation(summary = "发送邮件消息", description = "发送邮件类型的消息")
    public ResponseEntity<Map<String, Object>> sendEmailMessage(
            @Parameter(description = "邮件内容") @RequestParam("content") String content,
            @Parameter(description = "收件人") @RequestParam("receiver") String receiver) {
        
        MessageDto messageDto = new MessageDto(content, MessageType.EMAIL);
        messageDto.setReceiver(receiver);
        messageDto.setSender("EMAIL_SERVICE");
        
        messageProducerService.sendFanoutMessage(messageDto);
        
        Map<String, Object> response = createSuccessResponse("邮件消息发送成功", messageDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 发送短信消息
     */
    @PostMapping("/sms")
    @Operation(summary = "发送短信消息", description = "发送短信类型的消息")
    public ResponseEntity<Map<String, Object>> sendSmsMessage(
            @Parameter(description = "短信内容") @RequestParam("content") String content,
            @Parameter(description = "手机号") @RequestParam("receiver") String receiver) {
        
        MessageDto messageDto = new MessageDto(content, MessageType.SMS);
        messageDto.setReceiver(receiver);
        messageDto.setSender("SMS_SERVICE");
        
        messageProducerService.sendFanoutMessage(messageDto);
        
        Map<String, Object> response = createSuccessResponse("短信消息发送成功", messageDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取消息类型列表
     */
    @GetMapping("/types")
    @Operation(summary = "获取消息类型", description = "获取支持的消息类型列表")
    public ResponseEntity<Map<String, Object>> getMessageTypes() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", MessageType.values());
        return ResponseEntity.ok(response);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查消息服务是否正常运行")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "RabbitMQ消息服务运行正常");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(String message, MessageDto messageDto) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", messageDto);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}

