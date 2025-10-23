package com.github.zhuyizhuo.ai.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理应用程序中的异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理通用异常，返回JSON响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "发生内部错误");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理业务逻辑异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "业务逻辑错误");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理API相关异常，返回错误页面
     */
    @ExceptionHandler(ApiException.class)
    public ModelAndView handleApiException(ApiException ex, WebRequest request) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorCode", ex.getErrorCode());
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("timestamp", System.currentTimeMillis());
        
        return modelAndView;
    }
}