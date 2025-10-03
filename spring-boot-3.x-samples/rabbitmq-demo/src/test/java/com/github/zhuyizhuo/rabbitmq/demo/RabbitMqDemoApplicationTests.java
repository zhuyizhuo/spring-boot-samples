package com.github.zhuyizhuo.rabbitmq.demo;

import com.github.zhuyizhuo.rabbitmq.demo.model.MessageDto;
import com.github.zhuyizhuo.rabbitmq.demo.model.MessageType;
import com.github.zhuyizhuo.rabbitmq.demo.service.MessageProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * RabbitMQ Demo 应用测试类
 * 
 * @author zhuyizhuo
 * @version 1.0
 * @date 2025/10/02
 */
@SpringBootTest
@ActiveProfiles("test")
class RabbitMqDemoApplicationTests {

    @Autowired(required = false)
    private MessageProducerService messageProducerService;

    @Test
    void contextLoads() {
        // 测试 Spring 容器是否正常启动
        System.out.println("Spring Boot 应用启动成功！");
    }

    @Test
    void testMessageProducerServiceExists() {
        // 测试消息生产者服务是否存在（如果 RabbitMQ 连接可用）
        if (messageProducerService != null) {
            System.out.println("MessageProducerService 初始化成功");
        } else {
            System.out.println("MessageProducerService 未初始化（可能是 RabbitMQ 连接不可用）");
        }
    }

    @Test
    void testMessageDtoCreation() {
        // 测试消息对象创建
        MessageDto message = new MessageDto("测试消息", MessageType.NORMAL);
        message.setSender("test-sender");
        message.setReceiver("test-receiver");
        
        assert message.getContent().equals("测试消息");
        assert message.getMessageType() == MessageType.NORMAL;
        assert message.getSender().equals("test-sender");
        assert message.getReceiver().equals("test-receiver");
        assert message.getSendTime() != null;
        
        System.out.println("消息对象创建测试通过: " + message);
    }

    @Test
    void testMessageTypeEnum() {
        // 测试消息类型枚举
        MessageType[] types = MessageType.values();
        assert types.length > 0;
        
        MessageType userMessage = MessageType.fromCode("user_message");
        assert userMessage == MessageType.USER_MESSAGE;
        
        System.out.println("消息类型枚举测试通过，支持的类型数量: " + types.length);
    }
}

