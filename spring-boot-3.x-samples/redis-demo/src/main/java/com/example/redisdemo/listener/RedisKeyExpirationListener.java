package com.example.redisdemo.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 处理Redis键过期事件
     * 注意：在键过期事件中，只能获取到过期的键名，无法获取键的过期值
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 获取过期的键名
        String expiredKey = message.toString();
        log.info("监听到Redis键过期事件：key = {}", expiredKey);
        
        // 根据不同的键前缀处理不同的业务逻辑
        if (expiredKey.startsWith("user:")) {
            // 处理用户相关的键过期
            handleUserKeyExpiration(expiredKey);
        } else if (expiredKey.startsWith("order:")) {
            // 处理订单相关的键过期
            handleOrderKeyExpiration(expiredKey);
        } else if (expiredKey.startsWith("session:")) {
            // 处理会话相关的键过期
            handleSessionKeyExpiration(expiredKey);
        } else {
            // 处理其他键过期
            handleOtherKeyExpiration(expiredKey);
        }
    }

    /**
     * 处理用户相关的键过期
     */
    private void handleUserKeyExpiration(String expiredKey) {
        // 提取用户ID或其他信息
        String userId = extractIdFromKey(expiredKey, "user:");
        log.info("处理用户键过期，用户ID: {}", userId);
        // 这里可以实现用户相关的业务逻辑，如清理用户缓存、更新用户状态等
    }

    /**
     * 处理订单相关的键过期
     */
    private void handleOrderKeyExpiration(String expiredKey) {
        // 提取订单ID或其他信息
        String orderId = extractIdFromKey(expiredKey, "order:");
        log.info("处理订单键过期，订单ID: {}", orderId);
        // 这里可以实现订单相关的业务逻辑，如自动取消超时未支付订单等
    }

    /**
     * 处理会话相关的键过期
     */
    private void handleSessionKeyExpiration(String expiredKey) {
        // 提取会话ID或其他信息
        String sessionId = extractIdFromKey(expiredKey, "session:");
        log.info("处理会话键过期，会话ID: {}", sessionId);
        // 这里可以实现会话相关的业务逻辑，如清理会话数据、记录用户下线日志等
    }

    /**
     * 处理其他类型的键过期
     */
    private void handleOtherKeyExpiration(String expiredKey) {
        log.info("处理其他类型键过期: {}", expiredKey);
        // 处理其他类型的键过期逻辑
    }

    /**
     * 从键名中提取ID或其他标识信息
     */
    private String extractIdFromKey(String key, String prefix) {
        if (key != null && key.startsWith(prefix)) {
            return key.substring(prefix.length());
        }
        return key;
    }
}