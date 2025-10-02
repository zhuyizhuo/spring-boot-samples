package com.github.zhuyizhuo.springboot.redispubsub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.github.zhuyizhuo.springboot.redispubsub.model.User;
import com.github.zhuyizhuo.springboot.redispubsub.service.MessagePublisherService;

/**
 * 消息发布控制器，提供REST API接口用于发布Redis消息
 */
@RestController
@RequestMapping("/api/redis")
public class MessageController {

    @Autowired
    private MessagePublisherService messagePublisherService;

    /**
     * 发布普通消息
     * @param message 要发布的消息内容
     * @return 发布结果
     */
    @PostMapping("/publish")
    public String publishMessage(@RequestBody String message) {
        messagePublisherService.publishMessage(message);
        return "消息发布成功: " + message;
    }

    /**
     * 发布用户消息
     * @param message 要发布的用户消息内容
     * @return 发布结果
     */
    @PostMapping("/publish/user")
    public String publishUserMessage(@RequestBody String message) {
        messagePublisherService.publishUserMessage(message);
        return "用户消息发布成功: " + message;
    }

    /**
     * 发布用户对象消息
     * @param user 用户对象
     * @return 发布结果
     */
    @PostMapping("/publish/user-object")
    public String publishUserObjectMessage(@RequestBody User user) {
        messagePublisherService.publishUserMessage(user);
        return "用户对象消息发布成功: " + user;
    }

    /**
     * 发布消息到指定主题
     * @param topic 主题名称
     * @param message 要发布的消息内容
     * @return 发布结果
     */
    @PostMapping("/publish/{topic}")
    public String publishMessageToTopic(@PathVariable String topic, @RequestBody String message) {
        messagePublisherService.publishMessageToTopic(topic, message);
        return "消息发布到主题[" + topic + "]成功: " + message;
    }

    /**
     * 发布简单文本消息（GET请求）
     * @param message 要发布的消息内容
     * @return 发布结果
     */
    @GetMapping("/publish")
    public String publishSimpleMessage(@RequestParam String message) {
        messagePublisherService.publishMessage(message);
        return "简单消息发布成功: " + message;
    }
}