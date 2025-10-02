package com.github.zhuyizhuo.springboot.redispubsub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Jackson 配置类，用于配置 Jackson 序列化器
 * 主要用于解决 Java 8 日期时间类型的序列化问题
 */
@Configuration
public class JacksonConfig {
    
    /**
     * 配置 ObjectMapper 以支持 Java 8 日期时间类型
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册 JavaTimeModule 以支持 Java 8 日期时间类型
        objectMapper.registerModule(new JavaTimeModule());
        
        // 其他可能的配置
        // objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        return objectMapper;
    }
    
    /**
     * 配置 Redis 使用的 Jackson2JsonRedisSerializer
     */
    @Bean
    public Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer(ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }
}