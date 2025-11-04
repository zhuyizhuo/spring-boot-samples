package com.example.rocketmqdemo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息传输对象
 */
@Data
public class MessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息ID
     */
    private String id;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 消息类型
     */
    private String messageType;
}