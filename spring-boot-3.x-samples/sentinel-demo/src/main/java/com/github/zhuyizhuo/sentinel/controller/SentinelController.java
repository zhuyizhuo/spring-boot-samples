package com.github.zhuyizhuo.sentinel.controller;

import com.github.zhuyizhuo.sentinel.service.DemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Sentinel演示控制器
 * 提供各种API端点用于测试Sentinel的限流、熔断等功能
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Sentinel演示", description = "Sentinel限流熔断功能演示接口")
public class SentinelController {

    private static final Logger logger = LoggerFactory.getLogger(SentinelController.class);

    @Autowired
    private DemoService demoService;

    /**
     * 基础限流演示接口
     * @return 响应结果
     */
    @GetMapping("/hello")
    @Operation(summary = "基础限流演示", description = "展示基于QPS的限流控制")
    public ResponseEntity<Map<String, Object>> hello() {
        logger.info("[正常] 收到基础限流API请求");
        String result = demoService.hello();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        logger.info("[正常] 基础限流API响应成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 熔断降级演示接口
     * @param param 输入参数
     * @return 响应结果
     */
    @GetMapping("/demo")
    @Operation(summary = "熔断降级演示", description = "展示服务熔断和降级处理")
    public ResponseEntity<Map<String, Object>> demo(
            @Parameter(description = "测试参数，包含'error'时会触发异常")
            @RequestParam(required = false, defaultValue = "test") String param) {
        logger.info("[正常] 收到熔断API请求，参数: {}", param);
        String result = demoService.demo(param);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        logger.info("[正常] 熔断API响应成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 编码方式限流演示接口
     * @param param 输入参数
     * @return 响应结果
     */
    @GetMapping("/coding-style")
    @Operation(summary = "编码方式限流演示", description = "展示使用API手动进行资源保护")
    public ResponseEntity<Map<String, Object>> codingStyle(
            @Parameter(description = "测试参数")
            @RequestParam(required = false, defaultValue = "test") String param) {
        logger.info("[正常] 收到编码方式限流请求，参数: {}", param);
        String result = demoService.codingStyleLimit(param);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        logger.info("[正常] 编码方式限流响应成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 热点参数限流演示接口
     * @param param 热点参数
     * @return 响应结果
     */
    @GetMapping("/hot")
    @Operation(summary = "热点参数限流演示", description = "展示针对特定参数的限流")
    public ResponseEntity<Map<String, Object>> hotParam(
            @Parameter(description = "热点参数")
            @RequestParam(required = true) String param) {
        logger.info("[正常] 收到热点参数限流请求，参数: {}", param);
        String result = demoService.hotParamMethod(param, "normal");
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        logger.info("[正常] 热点参数限流响应成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 系统保护演示接口
     * @return 响应结果
     */
    @GetMapping("/system")
    @Operation(summary = "系统保护演示", description = "展示Sentinel的系统自适应保护")
    public ResponseEntity<Map<String, Object>> systemProtection() {
        logger.info("[正常] 收到系统保护API请求");
        String result = demoService.systemProtection();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        logger.info("[正常] 系统保护API响应成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 快速测试限流的接口
     * 用于测试频繁调用触发限流的情况
     * @return 响应结果
     */
    @GetMapping("/test-limit")
    @Operation(summary = "快速测试限流", description = "用于快速测试触发限流的情况")
    public ResponseEntity<Map<String, Object>> testLimit() {
        logger.info("[正常] 收到快速测试限流请求");
        String result = demoService.hello();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        response.put("timestamp", System.currentTimeMillis());
        logger.info("[正常] 快速测试限流响应成功");
        return ResponseEntity.ok(response);
    }
}