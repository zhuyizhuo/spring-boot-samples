package com.github.zhuyizhuo.springboot.redispubsub.listener;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redis消息监听器，用于接收和处理发布的消息
 */
@Component
public class MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    /**
     * 处理普通消息
     * @param message 接收到的消息
     */
    public void onMessage(Object message) {
        logger.info("接收到普通消息: {}", message);
        // 这里可以添加消息处理逻辑
    }

    /**
     * 处理用户相关消息
     * @param message 接收到的用户消息
     */
    public void onUserMessage(Object message) {
        logger.info("接收到用户消息: {}", message);
        // 这里可以添加用户消息处理逻辑
    }
}