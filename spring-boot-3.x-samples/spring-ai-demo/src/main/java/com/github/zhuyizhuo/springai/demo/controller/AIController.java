package com.github.zhuyizhuo.springai.demo.controller;

import com.github.zhuyizhuo.springai.demo.model.AIRequest;
import com.github.zhuyizhuo.springai.demo.model.AIResponse;
import com.github.zhuyizhuo.springai.demo.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * AI控制器
 * 提供AI相关API接口
 * 注意：当前版本为简化版，使用模拟数据返回
 */
@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI服务", description = "AI文本生成和检索API")
public class AIController {
    
    private static final Logger logger = Logger.getLogger(AIController.class.getName());
    
    @Autowired
    private AIService aiService;
    
    /**
     * 通用AI生成接口
     * 支持不同的模型类型
     */
    @PostMapping("/generate")
    @Operation(summary = "通用AI文本生成", description = "通过不同的AI模型生成文本内容")
    @ApiResponse(responseCode = "200", description = "生成成功", content = @Content(schema = @Schema(implementation = AIResponse.class)))
    @ApiResponse(responseCode = "400", description = "请求参数错误")
    @ApiResponse(responseCode = "500", description = "服务器内部错误")
    public ResponseEntity<AIResponse> generate(@RequestBody AIRequest request) {
        try {
            logger.info("接收到AI生成请求: " + request);
            AIResponse response = aiService.generate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("AI生成失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AIResponse.error("AI生成失败: " + e.getMessage()));
        }
    }
    
    /**
     * 使用OpenAI模型生成文本
     */
    @GetMapping("/openai/generate")
    @Operation(summary = "使用OpenAI生成文本", description = "通过OpenAI模型生成文本内容")
    @ApiResponse(responseCode = "200", description = "生成成功", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "请求参数错误")
    @ApiResponse(responseCode = "500", description = "服务器内部错误")
    public ResponseEntity<String> generateWithOpenAI(@RequestParam String prompt) {
        try {
            logger.info("接收到OpenAI生成请求: " + prompt);
            String response = aiService.generateWithOpenAI(prompt);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("OpenAI生成失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("OpenAI生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 使用Ollama模型生成文本
     */
    @GetMapping("/ollama/generate")
    @Operation(summary = "使用Ollama生成文本", description = "通过本地Ollama模型生成文本内容")
    @ApiResponse(responseCode = "200", description = "生成成功", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "请求参数错误")
    @ApiResponse(responseCode = "500", description = "服务器内部错误")
    public ResponseEntity<String> generateWithOllama(@RequestParam String prompt) {
        try {
            logger.info("接收到Ollama生成请求: " + prompt);
            String response = aiService.generateWithOllama(prompt);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Ollama生成失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ollama生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 向量检索接口
     */
    @GetMapping("/retrieve")
    @Operation(summary = "向量检索", description = "通过向量数据库检索相关内容")
    @ApiResponse(responseCode = "200", description = "检索成功", content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "请求参数错误")
    @ApiResponse(responseCode = "500", description = "服务器内部错误")
    public ResponseEntity<String> retrieve(@RequestParam String query) {
        try {
            logger.info("接收到检索请求: " + query);
            String response = aiService.retrieve(query);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("检索失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("检索失败: " + e.getMessage());
        }
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查AI服务是否正常运行")
    @ApiResponse(responseCode = "200", description = "服务正常")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI服务运行正常");
    }
    
    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        logger.severe("处理请求时发生错误: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("处理请求时发生错误: " + e.getMessage());
    }
}