package com.github.zhuyizhuo.springboot.redispubsub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zhuyizhuo.springboot.redispubsub.config.RedisConfig;

/**
 * Redis消息发布服务，用于发布消息到指定的主题
 */
@Service
public class MessagePublisherService {

    private static final Logger logger = LoggerFactory.getLogger(MessagePublisherService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @Autowired
    private ChannelTopic userTopic;

    /**
     * 发布普通消息到默认主题
     * @param message 要发布的消息
     */
    public void publishMessage(Object message) {
        logger.info("发布消息到主题[{}]: {}", RedisConfig.TOPIC_NAME, message);
        redisTemplate.convertAndSend(RedisConfig.TOPIC_NAME, message);
    }

    /**
     * 发布用户消息到用户主题
     * @param message 要发布的用户消息
     */
    public void publishUserMessage(Object message) {
        logger.info("发布用户消息到主题[{}]: {}", RedisConfig.USER_TOPIC_NAME, message);
        redisTemplate.convertAndSend(RedisConfig.USER_TOPIC_NAME, message);
    }

    /**
     * 发布消息到指定主题
     * @param topicName 主题名称
     * @param message 要发布的消息
     */
    public void publishMessageToTopic(String topicName, Object message) {
        logger.info("发布消息到主题[{}]: {}", topicName, message);
        redisTemplate.convertAndSend(topicName, message);
    }
}