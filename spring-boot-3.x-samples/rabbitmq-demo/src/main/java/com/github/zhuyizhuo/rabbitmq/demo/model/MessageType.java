package com.github.zhuyizhuo.rabbitmq.demo.model;

/**
 * 消息类型枚举
 * 
 * @author zhuyizhuo
 * @version 1.0
 * @date 2025/10/02
 */
public enum MessageType {
    
    /**
     * 普通消息
     */
    NORMAL("normal", "普通消息"),
    
    /**
     * 系统通知
     */
    NOTIFICATION("notification", "系统通知"),
    
    /**
     * 用户消息
     */
    USER_MESSAGE("user_message", "用户消息"),
    
    /**
     * 订单消息
     */
    ORDER("order", "订单消息"),
    
    /**
     * 支付消息
     */
    PAYMENT("payment", "支付消息"),
    
    /**
     * 邮件消息
     */
    EMAIL("email", "邮件消息"),
    
    /**
     * 短信消息
     */
    SMS("sms", "短信消息");

    private final String code;
    private final String description;

    MessageType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取MessageType
     * 
     * @param code 消息类型代码
     * @return MessageType
     */
    public static MessageType fromCode(String code) {
        for (MessageType type : MessageType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message type code: " + code);
    }

    @Override
    public String toString() {
        return "MessageType{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

