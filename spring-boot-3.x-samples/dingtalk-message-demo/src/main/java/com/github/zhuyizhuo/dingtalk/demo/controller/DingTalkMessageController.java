package com.github.zhuyizhuo.dingtalk.demo.controller;

import com.github.zhuyizhuo.dingtalk.demo.service.DingTalkMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉消息控制器
 */
@RestController
@RequestMapping("/api/dingtalk/message")
@AllArgsConstructor
@Tag(name = "钉钉消息接口", description = "提供钉钉消息推送相关的API")
public class DingTalkMessageController {

    private final DingTalkMessageService dingTalkMessageService;

    /**
     * 发送钉钉文本消息
     */
    @PostMapping("/text")
    @Operation(summary = "发送文本消息", description = "向钉钉群组发送文本消息")
    public ResponseEntity<Map<String, Object>> sendTextMessage(
            @Parameter(description = "消息内容") @RequestParam(value = "content") String content,
            @Parameter(description = "需要@的手机号列表") @RequestParam(value = "atMobiles", required = false) String[] atMobiles,
            @Parameter(description = "是否@所有人") @RequestParam(value = "isAtAll", defaultValue = "false") boolean isAtAll) {

        boolean success = dingTalkMessageService.sendTextMessage(content, atMobiles, isAtAll);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "发送成功" : "发送失败");
        return ResponseEntity.ok(result);
    }

    /**
     * 发送钉钉Markdown消息
     */
    @PostMapping("/markdown")
    @Operation(summary = "发送Markdown消息", description = "向钉钉群组发送Markdown格式消息")
    public ResponseEntity<Map<String, Object>> sendMarkdownMessage(
            @Parameter(description = "消息标题") @RequestParam(value = "title") String title,
            @Parameter(description = "消息内容（Markdown格式）") @RequestParam(value = "text") String text,
            @Parameter(description = "需要@的手机号列表") @RequestParam(value = "atMobiles", required = false) String[] atMobiles,
            @Parameter(description = "是否@所有人") @RequestParam(value = "isAtAll", defaultValue = "false") boolean isAtAll) {

        boolean success = dingTalkMessageService.sendMarkdownMessage(title, text, atMobiles, isAtAll);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "发送成功" : "发送失败");
        return ResponseEntity.ok(result);
    }

    /**
     * 发送钉钉工作通知消息
     */
    @PostMapping("/work-notice")
    @Operation(summary = "发送工作通知消息", description = "向指定用户发送钉钉工作通知消息")
    public ResponseEntity<Map<String, Object>> sendWorkNoticeMessage(
            @Parameter(description = "用户ID") @RequestParam(value = "userId") String userId,
            @Parameter(description = "消息标题") @RequestParam(value = "title") String title,
            @Parameter(description = "消息内容") @RequestParam(value = "content") String content) {

        boolean success = dingTalkMessageService.sendWorkNoticeMessage(userId, title, content);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "发送成功" : "发送失败");
        return ResponseEntity.ok(result);
    }
}