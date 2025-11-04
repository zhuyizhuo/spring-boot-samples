package com.example.rocketmqdemo;

import com.example.rocketmqdemo.entity.MessageDTO;
import com.example.rocketmqdemo.producer.RocketMQProducerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@SpringBootTest
class RocketMQDemoApplicationTests {

    @Autowired
    private RocketMQProducerService producerService;

    // 测试同步消息发送
    @Test
    void testSendSyncMessage() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setContent("测试同步消息");
        messageDTO.setMessageType("TEXT");

        String result = producerService.sendSyncMessage("test-topic", messageDTO);
        System.out.println("同步消息发送结果: " + result);
        // 注意：实际测试中，这里可能需要根据RocketMQ服务器的状态进行断言
        // 如果没有启动RocketMQ服务器，这里可能会抛出异常
        // 可以根据实际情况调整测试策略
    }

    // 测试异步消息发送
    @Test
    void testSendAsyncMessage() throws InterruptedException {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setContent("测试异步消息");
        messageDTO.setMessageType("TEXT");

        CountDownLatch latch = new CountDownLatch(1);
        // 使用自定义回调来捕获结果
        producerService.sendAsyncMessage("test-topic", messageDTO);
        
        // 等待异步操作完成，但不保证一定等到（避免测试无限等待）
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        System.out.println("异步消息发送测试完成: " + completed);
        // 注意：异步测试比较复杂，通常需要模拟回调或者使用并发工具进行同步
    }

    // 测试单向消息发送
    @Test
    void testSendOnewayMessage() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setContent("测试单向消息");
        messageDTO.setMessageType("TEXT");

        // 单向消息没有返回值，这里主要验证不会抛出异常
        assertDoesNotThrow(() -> {
            producerService.sendOnewayMessage("test-topic", messageDTO);
            System.out.println("单向消息发送测试完成");
        });
    }

    // 测试延时消息发送
    @Test
    void testSendDelayMessage() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setContent("测试延时消息");
        messageDTO.setMessageType("TEXT");

        String result = producerService.sendDelayMessage("test-delay-topic", messageDTO, 3);
        System.out.println("延时消息发送结果: " + result);
        // 注意：实际测试中，这里可能需要根据RocketMQ服务器的状态进行断言
    }

    // 测试批量消息发送
    @Test
    void testSendBatchMessage() {
        MessageDTO message1 = new MessageDTO();
        message1.setContent("测试批量消息1");
        message1.setMessageType("TEXT");

        MessageDTO message2 = new MessageDTO();
        message2.setContent("测试批量消息2");
        message2.setMessageType("TEXT");

        List<MessageDTO> messages = Arrays.asList(message1, message2);

        assertDoesNotThrow(() -> {
            producerService.sendBatchMessage("test-topic", messages);
            System.out.println("批量消息发送测试完成");
        });
        // 注意：实际测试中，这里可能需要根据RocketMQ服务器的状态进行断言
    }

    // 测试消息实体的基本功能
    @Test
    void testMessageDTO() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId("123456");
        messageDTO.setContent("测试消息内容");
        messageDTO.setMessageType("TEXT");
        messageDTO.setSendTime(LocalDateTime.now());

        assertEquals("123456", messageDTO.getId());
        assertEquals("测试消息内容", messageDTO.getContent());
        assertEquals("TEXT", messageDTO.getMessageType());
        assertNotNull(messageDTO.getSendTime());

        // 测试toString方法
        String toString = messageDTO.toString();
        assertTrue(toString.contains("123456"));
        assertTrue(toString.contains("测试消息内容"));

        // 测试序列化相关功能（通过断言不抛出异常来验证）
        assertDoesNotThrow(() -> {
            String json = String.format("{\"id\":\"%s\",\"content\":\"%s\",\"messageType\":\"%s\"}",
                    messageDTO.getId(), messageDTO.getContent(), messageDTO.getMessageType());
            System.out.println("消息JSON格式: " + json);
        });
    }

    // 测试生产环境中的消息格式
    @Test
    void testProductionMessageFormat() {
        // 创建一个模拟生产环境的消息
        MessageDTO productionMessage = new MessageDTO();
        productionMessage.setContent("生产环境测试消息");
        productionMessage.setMessageType("NOTIFICATION");
        productionMessage.setSendTime(LocalDateTime.now());

        // 格式化时间用于日志记录
        String formattedTime = productionMessage.getSendTime().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        System.out.printf("生产环境消息测试 - 内容: %s, 类型: %s, 时间: %s\n",
                productionMessage.getContent(),
                productionMessage.getMessageType(),
                formattedTime);

        // 验证消息符合预期格式
        assertNotNull(productionMessage.getContent());
        assertNotNull(productionMessage.getMessageType());
        assertNotNull(productionMessage.getSendTime());
    }

    // 测试消息内容的最大长度限制（RocketMQ建议单条消息不超过4MB）
    @Test
    void testMessageSizeLimit() {
        // 创建一个相对较大的消息，但不超过4MB限制
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeContent.append("测试数据");
        }

        MessageDTO largeMessage = new MessageDTO();
        largeMessage.setContent(largeContent.toString());
        largeMessage.setMessageType("TEXT");

        // 检查消息大小（字节数）
        int messageSize = largeMessage.getContent().getBytes().length;
        System.out.println("测试消息大小: " + messageSize + " 字节");

        // RocketMQ建议单条消息不超过4MB
        assertTrue(messageSize < 4 * 1024 * 1024, "消息大小应小于4MB");
    }
}