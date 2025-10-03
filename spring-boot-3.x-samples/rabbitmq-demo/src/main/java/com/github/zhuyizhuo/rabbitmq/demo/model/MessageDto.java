package com.github.zhuyizhuo.rabbitmq.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息传输对象
 * 
 * @author zhuyizhuo
 * @version 1.0
 * @date 2025/10/02
 */
public class MessageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 消息类型
     */
    @NotNull(message = "消息类型不能为空")
    private MessageType messageType;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    /**
     * 发送者
     */
    private String sender;

    /**
     * 接收者
     */
    private String receiver;

    public MessageDto() {
        this.sendTime = LocalDateTime.now();
    }

    public MessageDto(String content, MessageType messageType) {
        this();
        this.content = content;
        this.messageType = messageType;
    }

    public MessageDto(String messageId, String content, MessageType messageType, String sender) {
        this(content, messageType);
        this.messageId = messageId;
        this.sender = sender;
    }

    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "messageId='" + messageId + '\'' +
                ", content='" + content + '\'' +
                ", messageType=" + messageType +
                ", sendTime=" + sendTime +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }
}

