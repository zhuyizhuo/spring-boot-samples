package com.github.zhuyizhuo.sentry.controller;

import com.github.zhuyizhuo.sentry.service.SentryService;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.protocol.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Sentry 示例控制器，演示各种异常监控场景
 */
@RestController
@RequestMapping("/api/sentry")
public class SentryController {

    private static final Logger logger = LoggerFactory.getLogger(SentryController.class);

    @Autowired
    private SentryService sentryService;

    /**
     * 正常接口，无异常
     */
    @GetMapping("/normal")
    public ResponseEntity<Map<String, String>> normal() {
        logger.info("[SentryController] 开始处理正常请求");
        
        try {
            Map<String, String> response = new HashMap<>();
            response.put("message", "正常请求处理成功");
            response.put("status", "success");
            
            logger.info("[SentryController] 正常请求处理完成，返回状态: success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[SentryController] 正常请求处理异常", e);
            throw e;
        }
    }

    /**
     * 自动捕获异常的接口
     */
    @GetMapping("/exception")
    public ResponseEntity<Map<String, String>> exception() {
        logger.info("[SentryController] 开始处理异常测试请求，准备抛出异常");
        
        // 记录Sentry初始化状态
        boolean isSentryInitialized = Sentry.isEnabled();
        logger.info("[SentryController] Sentry是否已初始化: {}", isSentryInitialized);
        
        // 这个异常会被 Sentry 自动捕获
        RuntimeException exception = new RuntimeException("这是一个测试异常，将被 Sentry 自动捕获");
        logger.warn("[SentryController] 即将抛出测试异常，应该被Sentry捕获", exception);
        throw exception;
    }

    /**
     * 手动发送异常到 Sentry
     */
    @GetMapping("/manual-exception")
    public ResponseEntity<Map<String, String>> manualException() {
        logger.info("[SentryController] 开始处理手动异常捕获请求");
        
        try {
            int result = 1 / 0;
        } catch (Exception e) {
            logger.warn("[SentryController] 捕获到除零异常，准备手动发送到Sentry", e);
            
            // 记录Sentry状态
            boolean isSentryEnabled = Sentry.isEnabled();
            logger.info("[SentryController] Sentry是否已初始化: {}", isSentryEnabled);
            
            // 手动捕获并发送异常到 Sentry
            io.sentry.protocol.SentryId eventId = Sentry.captureException(e);
            logger.info("[SentryController] 异常已发送到Sentry，事件ID: {}", eventId.toString());
            
            // 也可以添加额外的上下文信息
            Sentry.setExtra("operation", "division_by_zero");
            logger.info("[SentryController] 已设置额外上下文信息");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "异常已被手动捕获并发送到 Sentry");
        response.put("status", "handled");
        
        logger.info("[SentryController] 手动异常捕获请求处理完成");
        return ResponseEntity.ok(response);
    }

    /**
     * 性能监控示例
     */
    @GetMapping("/performance")
    public ResponseEntity<Map<String, String>> performance() {
        logger.info("[SentryController] 开始处理性能监控请求");
        
        try {
            // 记录性能监控
            logger.info("[SentryController] 开始记录性能监控事务");
            io.sentry.ITransaction transaction = Sentry.startTransaction("performance-test", "api-request");
            
            // 模拟一些操作
            Thread.sleep(50);
            
            transaction.finish();
            logger.info("[SentryController] 性能监控事务已完成");

            Map<String, String> response = new HashMap<>();
            response.put("message", "性能监控示例");
            response.put("status", "measured");
            
            logger.info("[SentryController] 性能监控请求处理完成");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[SentryController] 性能监控请求处理异常", e);
            throw new RuntimeException("性能监控请求失败", e);
        }
    }

    /**
     * 用户信息跟踪
     */
    @PostMapping("/user-tracking")
    public ResponseEntity<Map<String, String>> userTracking(@RequestBody User userData) {
        logger.info("[SentryController] 开始处理用户信息跟踪请求，用户ID: {}", userData.getId());
        
        try {
            // 设置用户信息，用于在 Sentry 中关联问题
            User sentryUser = new User();
            sentryUser.setId(userData.getId());
            sentryUser.setUsername(userData.getUsername());
            sentryUser.setEmail(userData.getEmail());
            
            logger.info("[SentryController] 设置用户信息到Sentry: username={}, email={}", 
                      userData.getUsername(), userData.getEmail());
            Sentry.setUser(sentryUser);

            // 记录用户操作日志
            logger.info("[SentryController] 记录用户操作日志到Sentry");
            Sentry.captureMessage("用户登录操作", SentryLevel.INFO);

            Map<String, String> response = new HashMap<>();
            response.put("message", "用户信息已记录到 Sentry");
            response.put("status", "tracked");
            
            logger.info("[SentryController] 用户信息跟踪请求处理完成");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[SentryController] 用户信息跟踪请求处理异常", e);
            throw e;
        }
    }

    /**
     * 服务层异常测试
     */
    @GetMapping("/service-exception")
    public ResponseEntity<Map<String, String>> serviceException() {
        logger.info("[SentryController] 开始处理服务层异常测试请求");
        
        // 调用服务层方法，该方法会抛出异常
        logger.info("[SentryController] 调用sentryService.throwException()，预期会抛出异常");
        sentryService.throwException();
        
        // 以下代码不会被执行
        return ResponseEntity.ok(Map.of("message", "这个返回不会被执行"));
    }

    /**
     * 慢操作测试（性能问题）
     */
    @GetMapping("/slow-operation")
    public ResponseEntity<Map<String, String>> slowOperation() {
        logger.info("[SentryController] 开始处理慢操作测试请求");
        
        long startTime = System.currentTimeMillis();
        try {
            // 模拟慢操作
            logger.info("[SentryController] 开始执行模拟慢操作，睡眠3秒");
            TimeUnit.SECONDS.sleep(3);
            
            long endTime = System.currentTimeMillis();
            logger.info("[SentryController] 慢操作执行完成，耗时: {}ms", (endTime - startTime));
        } catch (InterruptedException e) {
            logger.warn("[SentryController] 慢操作被中断", e);
            Thread.currentThread().interrupt();
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "慢操作完成");
        response.put("status", "completed");
        
        logger.info("[SentryController] 慢操作测试请求处理完成");
        return ResponseEntity.ok(response);
    }

    /**
     * 自定义标签测试
     */
    @GetMapping("/custom-tags")
    public ResponseEntity<Map<String, String>> customTags() {
        logger.info("[SentryController] 开始处理自定义标签测试请求");
        
        try {
            // 设置自定义标签
            logger.info("[SentryController] 开始设置自定义标签到Sentry");
            Sentry.setTag("feature", "custom-tags");
            Sentry.setTag("environment", "production");
            Sentry.setTag("user-type", "premium");
            logger.info("[SentryController] 已设置3个自定义标签");
            
            // 捕获带有标签的消息
            logger.info("[SentryController] 捕获带有自定义标签的测试消息");
            io.sentry.protocol.SentryId eventId = Sentry.captureMessage("带有自定义标签的测试消息", SentryLevel.DEBUG);
            logger.info("[SentryController] 带标签消息已发送到Sentry，事件ID: {}", eventId.toString());

            Map<String, String> response = new HashMap<>();
            response.put("message", "自定义标签已设置");
            response.put("status", "tagged");
            
            logger.info("[SentryController] 自定义标签测试请求处理完成");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[SentryController] 自定义标签测试请求处理异常", e);
            throw e;
        }
    }
}