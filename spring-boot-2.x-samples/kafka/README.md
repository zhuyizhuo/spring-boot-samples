# Spring Boot 集成 Kafka

## 📋 模块介绍

本模块演示了如何在 Spring Boot 项目中集成 Apache Kafka，实现消息的发送和接收功能。该模块已完成 Spring Boot 与 Kafka 的集成，提供了完整的消息生产和消费示例。

## ✨ 功能特点

- ✅ Kafka 消息发送
- ✅ Kafka 消息接收
- ✅ 手动提交消息确认（manual_immediate 模式）
- ✅ 完整的生产者/消费者配置
- ✅ 基于 Spring Kafka 框架实现

## 🛠 技术栈

- Spring Boot 2.x
- Spring Kafka
- Apache Kafka
- Fastjson
- Lombok

## 📦 依赖配置

核心依赖配置如下：

```xml
<dependencies>
    <!-- Spring Kafka 集成 -->
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
    
    <!-- Web 支持 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- 工具类库 -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

## 🔧 配置说明

在 `application.yml` 文件中配置 Kafka 连接和参数：

```yaml
spring:
  kafka:
    bootstrap-servers: 127.0.0.1:9092  # Kafka 服务器地址和端口
    
    # 生产者配置
    producer:
      retries: 0                       # 发送失败重试次数（0表示不重试）
      batch-size: 16384                # 批处理大小（字节）
      buffer-memory: 33554432          # 缓冲区大小（字节）
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    
    # 消费者配置
    consumer:
      enable-auto-commit: false        # 关闭自动提交
      auto-commit-interval: 100        # 自动提交间隔（毫秒）
      auto-offset-reset: earliest      # 偏移量重置策略（earliest/latest/none）
      max-poll-records: 500            # 单次最大拉取记录数
      key-serializer: org.apache.kafka.common.serialization.StringDeserializer
      value-serializer: org.apache.kafka.common.serialization.StringDeserializer
    
    # 监听器配置
    listener:
      # 手动立即提交模式
      ack-mode: manual_immediate
  
  # 应用名称
  application:
    name: kafka-springboot
```

## 🚀 快速开始

### 环境要求

- JDK 8+
- Apache Kafka 服务器（默认地址：127.0.0.1:9092）

### 运行步骤

1. 确保 Kafka 服务器已启动
2. 修改 `application.yml` 中的 `bootstrap-servers` 配置，指向您的 Kafka 服务器地址
3. 启动应用程序
4. 使用 Sender 类发送消息
5. 通过 Listener 接收并处理消息

## 📝 代码示例

### 1. 消息发送示例

```java
@Service
public class KafkaSender {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        System.out.println("发送消息成功：" + message);
    }
    
    // 带回调的消息发送
    public void sendMessageWithCallback(String topic, String message) {
        kafkaTemplate.send(topic, message).addCallback(
            success -> System.out.println("发送成功，主题：" + success.getRecordMetadata().topic()),
            failure -> System.err.println("发送失败：" + failure.getMessage())
        );
    }
}
```

### 2. 消息接收示例

```java
@Component
public class KafkaListener {
    
    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void receiveMessage(String message, Acknowledgment acknowledgment) {
        try {
            System.out.println("接收到消息：" + message);
            // 处理消息...
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 手动提交确认
            acknowledgment.acknowledge();
        }
    }
}
```

### 3. Kafka 常量配置

```java
public class KafkaConstant {
    // Kafka 服务器地址
    public static final String KAFKA_HOST = "127.0.0.1:9092";
    
    // 测试主题
    public static final String TEST_TOPIC = "test-topic";
    
    // 测试消费者组
    public static final String TEST_GROUP = "test-group";
}
```

## 🔍 常见问题

### 1. 连接 Kafka 失败
- 确认 Kafka 服务是否已启动
- 检查 `bootstrap-servers` 配置是否正确
- 确保网络连接通畅，防火墙未阻止连接

### 2. 消息发送失败
- 检查主题是否存在
- 查看生产者配置是否正确
- 检查 Kafka 服务器日志是否有错误信息

### 3. 消息接收不到
- 确认消费者组配置是否正确
- 检查 `auto-offset-reset` 配置
- 确认主题中是否有消息

## 📚 参考资料

- [Spring Kafka 官方文档](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [Apache Kafka 官方文档](https://kafka.apache.org/documentation/)
- [Spring Boot 与 Kafka 集成教程](https://spring.io/projects/spring-kafka)