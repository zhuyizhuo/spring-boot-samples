package com.github.zhuyizhuo.springboot.redispubsub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.github.zhuyizhuo.springboot.redispubsub.listener.MessageListener;

@Configuration
public class RedisConfig {

    // Redis消息的主题名称
    public static final String TOPIC_NAME = "demo-topic";
    public static final String USER_TOPIC_NAME = "user-topic";

    /**
     * 配置RedisTemplate，用于操作Redis
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        
        // 设置键的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        // 设置值的序列化器 - 使用配置的Jackson序列化器以支持Java 8日期时间类型
        template.setValueSerializer(jackson2JsonRedisSerializer);
        
        // 设置哈希键的序列化器
        template.setHashKeySerializer(new StringRedisSerializer());
        // 设置哈希值的序列化器
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 创建消息监听器容器
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter messageListenerAdapter,
            MessageListenerAdapter userMessageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        
        // 注册消息监听器，监听指定的主题
        container.addMessageListener(messageListenerAdapter, new ChannelTopic(TOPIC_NAME));
        container.addMessageListener(userMessageListenerAdapter, new ChannelTopic(USER_TOPIC_NAME));
        
        return container;
    }

    /**
     * 消息监听器适配器，用于接收普通消息
     */
    @Bean
    public MessageListenerAdapter messageListenerAdapter(MessageListener messageListener) {
        return new MessageListenerAdapter(messageListener, "onMessage");
    }

    /**
     * 消息监听器适配器，用于接收用户消息
     */
    @Bean
    public MessageListenerAdapter userMessageListenerAdapter(MessageListener messageListener) {
        return new MessageListenerAdapter(messageListener, "onUserMessage");
    }

    /**
     * 创建主题对象
     */
    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(TOPIC_NAME);
    }

    @Bean
    public ChannelTopic userTopic() {
        return new ChannelTopic(USER_TOPIC_NAME);
    }
}