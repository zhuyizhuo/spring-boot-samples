package com.github.zhuyizhuo.wechat.demo.controller;

import com.github.zhuyizhuo.wechat.demo.service.WechatMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信消息控制器
 */
@RestController
@RequestMapping("/api/wechat/message")
@AllArgsConstructor
public class WechatMessageController {

    private final WechatMessageService wechatMessageService;

    /**
     * 发送微信文本消息
     */
    @PostMapping("/text")
    public ResponseEntity<Map<String, Object>> sendTextMessage(
            @RequestParam String openId,
            @RequestParam String content) {

        boolean success = wechatMessageService.sendTextMessage(openId, content);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "发送成功" : "发送失败");
        return ResponseEntity.ok(result);
    }

    /**
     * 发送微信模板消息
     */
    @PostMapping("/template")
    public ResponseEntity<Map<String, Object>> sendTemplateMessage(
            @RequestParam String openId,
            @RequestParam String templateId,
            @RequestParam String data,
            @RequestParam(required = false) String url) {

        boolean success = wechatMessageService.sendTemplateMessage(openId, templateId, data, url);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "发送成功" : "发送失败");
        return ResponseEntity.ok(result);
    }
}