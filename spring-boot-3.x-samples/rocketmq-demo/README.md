# Spring Boot RocketMQ 集成示例

本项目展示了 Spring Boot 3.x 与 Apache RocketMQ 的集成示例，实现了各种消息发送模式和消费者功能。

## 项目结构

```
src/main/java/com/example/rocketmqdemo/
├── RocketmqDemoApplication.java       # 应用主启动类
├── config/                            # 配置类
├── producer/                          # 生产者相关
│   └── RocketMQProducerService.java   # RocketMQ 生产者服务
├── consumer/                          # 消费者相关
│   ├── RocketMQConsumerService.java   # 普通消息消费者
│   └── RocketMQDelayConsumerService.java # 延时消息消费者
├── controller/                        # 控制器层
│   └── MessageController.java         # REST API 控制器
└── entity/                            # 数据实体
    └── MessageDTO.java                # 消息数据传输对象
src/main/resources/
├── application.yml                    # 主配置文件
├── application-rocketmq.yml           # RocketMQ 配置文件
└── application-rocketmq.yml.demo      # RocketMQ 配置示例文件
```

## 功能特性

### 1. 消息发送模式

实现了多种消息发送模式：

- **同步消息**：发送消息后等待服务器确认
- **异步消息**：发送消息后通过回调处理结果
- **单向消息**：发送消息不关心结果（高吞吐场景）
- **延时消息**：支持18个级别的定时延迟消息
- **批量消息**：批量发送多条消息

### 2. 消费者功能

- **普通消息消费者**：监听并处理普通主题消息
- **延时消息消费者**：专门处理延时主题消息
- **并发消费模式**：支持多条消息并发处理
- **集群消费模式**：多实例部署时自动负载均衡

### 3. REST API 接口

提供了完整的 REST API 接口用于测试各种消息发送：

- **同步消息**：`/api/message/sync`
- **异步消息**：`/api/message/async`
- **单向消息**：`/api/message/oneway`
- **延时消息**：`/api/message/delay`
- **批量消息**：`/api/message/batch`

## 环境要求

- JDK 17+
- Spring Boot 3.2.x
- Apache RocketMQ 5.x

## 快速开始

### 1. 准备 RocketMQ 服务器

确保 RocketMQ 服务器已启动并运行。

### 2. 配置 RocketMQ 连接

1. 复制配置示例文件（如果还没有实际配置文件）：
   ```bash
   cp src/main/resources/application-rocketmq.yml.demo src/main/resources/application-rocketmq.yml
   ```

2. 编辑 `application-rocketmq.yml` 文件，填入实际的 RocketMQ 连接信息：
   - `name-server`: RocketMQ NameServer 地址，格式为 ip:port
   - `producer.group`: 生产者组名
   - 其他生产者配置参数可根据需要调整

### 3. 构建和运行项目

```bash
# 构建项目
mvn clean package -DskipTests

# 运行应用
java -jar target/rocketmq-demo-1.0-SNAPSHOT.jar
```

## 配置说明

### 主配置文件（application.yml）

主配置文件主要包含：
- 激活的配置文件（通过 profiles.active 指定）
- 应用基本信息
- 服务器端口配置
- 日志级别配置

### RocketMQ 配置文件（application-rocketmq.yml）

RocketMQ 配置文件包含所有与 RocketMQ 相关的配置：

#### 1. NameServer 配置
- `name-server`: 指定 RocketMQ NameServer 地址，用于服务发现

#### 2. 生产者配置
- `group`: 生产者组名，必须唯一
- `send-message-timeout`: 发送消息超时时间（毫秒）
- `retry-times-when-send-failed`: 同步发送失败重试次数
- `retry-times-when-send-async-failed`: 异步发送失败重试次数
- `retry-next-server`: 发送失败时是否重试其他 Broker
- `max-message-size`: 消息最大大小（字节），默认4MB
- `compress-message-body-over-howmuch`: 消息体压缩阈值（字节）
- `topic-queue-nums`: 主题队列数量

## 使用示例

### 发送同步消息

通过 REST API 发送同步消息：

```bash
curl -X POST http://localhost:8080/api/message/sync \
  -H "Content-Type: application/json" \
  -d '{"content": "这是一条同步消息", "messageType": "TEXT", "topic": "demo-topic"}'
```

### 发送延时消息

通过 REST API 发送延时消息（延时级别3表示10秒）：

```bash
curl -X POST http://localhost:8080/api/message/delay \
  -H "Content-Type: application/json" \
  -d '{"content": "这是一条延时消息", "messageType": "NOTIFICATION", "topic": "demo-delay-topic", "delayLevel": 3}'
```

### 发送批量消息

通过 REST API 发送批量消息：

```bash
curl -X POST http://localhost:8080/api/message/batch \
  -H "Content-Type: application/json" \
  -d '{"topic": "demo-topic", "messages": [{"content": "批量消息1", "messageType": "TEXT"}, {"content": "批量消息2", "messageType": "TEXT"}]}'
```

## 延时级别说明

RocketMQ 支持18个级别的延时消息：

- 级别1：1秒
- 级别2：5秒
- 级别3：10秒
- 级别4：30秒
- 级别5：1分钟
- 级别6：2分钟
- 级别7：3分钟
- 级别8：4分钟
- 级别9：5分钟
- 级别10：6分钟
- 级别11：7分钟
- 级别12：8分钟
- 级别13：9分钟
- 级别14：10分钟
- 级别15：20分钟
- 级别16：30分钟
- 级别17：1小时
- 级别18：2小时

## 注意事项

1. 确保 RocketMQ NameServer 地址正确配置，否则无法连接到 RocketMQ 服务
2. 生产者组名必须唯一，避免与其他应用冲突
3. 消息大小不要超过配置的 max-message-size 限制
4. 生产环境中建议关闭 debug 日志级别，以提高性能
5. 消费者处理消息时应做好异常处理，避免消息堆积

## 扩展与自定义

如果需要自定义 RocketMQ 配置，可以创建配置类实现：

```java
@Configuration
public class RocketMQCustomConfig {
    // 自定义配置代码
}
```

## License

This project is licensed under the MIT License.