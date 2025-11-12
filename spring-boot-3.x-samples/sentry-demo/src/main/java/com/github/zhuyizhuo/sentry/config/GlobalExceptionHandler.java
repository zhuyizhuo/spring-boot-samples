package com.github.zhuyizhuo.sentry.config;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器，统一处理应用中的异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        // 捕获异常并发送到 Sentry
        Sentry.captureException(ex);
        
        // 添加请求信息作为上下文
        Sentry.setExtra("request_path", request.getDescription(false));
        Sentry.setExtra("timestamp", formatter.format(LocalDateTime.now()));
        
        // 记录异常日志
        logger.error("全局异常捕获: {}", ex.getMessage());
        logger.error("异常详情", ex);
        
        // 构建响应
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", formatter.format(LocalDateTime.now()));
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "服务器内部错误，请稍后重试");
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        // 返回友好的错误响应给客户端
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理业务逻辑异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        // 设置错误级别为警告
        Sentry.captureException(ex, scope -> {
            scope.setLevel(SentryLevel.WARNING);
            scope.setExtra("exception_type", "业务参数错误");
        });
        
        // 记录错误日志
        logger.warn("业务参数错误: {}", ex.getMessage());
        
        // 构建响应
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", formatter.format(LocalDateTime.now()));
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        // 捕获运行时异常并发送到 Sentry
        Sentry.captureException(ex);
        
        // 添加错误上下文
        Sentry.setExtra("error_category", "运行时错误");
        
        // 记录错误日志
        logger.error("运行时异常: {}", ex.getMessage());
        logger.error("异常详情", ex);
        
        // 构建响应
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", formatter.format(LocalDateTime.now()));
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Runtime Error");
        body.put("message", "系统运行时错误，请联系管理员");
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}