package com.github.zhuyizhuo.sentinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局Sentinel限流异常处理器
 * 使用Spring的异常处理机制捕获所有Sentinel限流异常
 */
@RestControllerAdvice
public class GlobalBlockExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalBlockExceptionHandler.class);

    /**
     * 处理所有Sentinel限流异常
     * @param e BlockException异常
     * @return 标准格式的JSON响应
     */
    @ExceptionHandler(BlockException.class)
    public ResponseEntity<Map<String, Object>> handleBlockException(BlockException e) {
        Map<String, Object> response = new HashMap<>();
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
        
        if (e instanceof FlowException) {
            logger.warn("[限流] 触发流控规则，异常信息: {}", e.getMessage());
            response.put("code", 429);
            response.put("message", "请求过于频繁，请稍后再试");
            response.put("error", "限流");
            response.put("data", null);
            status = HttpStatus.TOO_MANY_REQUESTS;
        } else if (e instanceof DegradeException) {
            logger.warn("[限流] 触发熔断规则，异常信息: {}", e.getMessage());
            response.put("code", 503);
            response.put("message", "服务暂时不可用，请稍后再试");
            response.put("error", "熔断");
            response.put("data", null);
            status = HttpStatus.SERVICE_UNAVAILABLE;
        } else if (e instanceof ParamFlowException) {
            logger.warn("[限流] 触发热点参数限流，异常信息: {}", e.getMessage());
            response.put("code", 429);
            response.put("message", "热点参数限流，请稍后再试");
            response.put("error", "热点限流");
            response.put("data", null);
            status = HttpStatus.TOO_MANY_REQUESTS;
        } else if (e instanceof AuthorityException) {
            logger.warn("[限流] 触发授权规则，异常信息: {}", e.getMessage());
            response.put("code", 403);
            response.put("message", "未授权访问");
            response.put("error", "授权错误");
            response.put("data", null);
            status = HttpStatus.FORBIDDEN;
        } else if (e instanceof SystemBlockException) {
            logger.warn("[限流] 触发系统保护，异常信息: {}", e.getMessage());
            response.put("code", 503);
            response.put("message", "系统保护中，请稍后再试");
            response.put("error", "系统保护");
            response.put("data", null);
            status = HttpStatus.SERVICE_UNAVAILABLE;
        } else {
            logger.warn("[限流] 触发未知限流规则，异常信息: {}", e.getMessage());
            response.put("code", 429);
            response.put("message", "服务限流中，请稍后再试");
            response.put("error", "限流");
            response.put("data", null);
            status = HttpStatus.TOO_MANY_REQUESTS;
        }
        
        return ResponseEntity.status(status)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(response);
    }
}