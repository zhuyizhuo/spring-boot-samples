# Spring Boot 3.x Kafka Demo

这是一个基于 Spring Boot 3.x 的 Kafka 集成示例项目，展示了如何在 Spring Boot 应用中使用 Kafka 进行消息的发送和接收。

## 项目特性

- ✅ 基于 Spring Boot 3.2.5 和 Spring Kafka
- ✅ 支持多种消息发送模式：字符串消息、JSON 消息和批量消息
- ✅ 提供完整的消费者示例，支持单条消息和批量消息消费
- ✅ 集成 Thymeleaf 提供 Web 测试界面
- ✅ 集成 Swagger 提供 API 文档
- ✅ 实时消息统计和展示
- ✅ 自动创建 Kafka 主题
- ✅ 消息发送确认和错误处理

## 技术栈

- **Spring Boot**: 3.2.5
- **Spring Kafka**: 集成 Kafka 的 Spring 框架
- **Thymeleaf**: 模板引擎，用于构建 Web 界面
- **SpringDoc OpenAPI**: 提供 API 文档
- **Bootstrap 5**: 前端 UI 框架
- **jQuery**: JavaScript 库

## 项目结构

```
src/main/
├── java/com/github/zhuyizhuo/kafka/demo/
│   ├── KafkaDemoApplication.java    # 应用程序入口类
│   ├── config/                      # 配置类
│   │   └── KafkaConfig.java         # Kafka 配置类
│   ├── controller/                  # 控制器
│   │   └── HomeController.java      # 首页控制器
│   ├── listener/                    # 监听器
│   │   └── KafkaConsumerListener.java # Kafka 消费者监听器
│   ├── model/                       # 数据模型
│   │   └── KafkaMessage.java        # Kafka 消息模型
│   └── service/                     # 服务层
│       └── KafkaProducerService.java # Kafka 消息生产者服务
└── resources/
    ├── application.yml              # 应用配置文件
    └── templates/                   # Thymeleaf 模板
        └── index.html               # 首页模板
```

## 快速开始

### 环境要求

- JDK 17 或更高版本
- Maven 3.6+ 或 Gradle 7.0+
- Kafka 2.8.0+（本地或远程服务器）
- 浏览器（用于访问 Web 界面）

### 安装步骤

1. 确保 Kafka 服务已启动并运行
   ```bash
   # 启动 ZooKeeper
   zookeeper-server-start.sh config/zookeeper.properties
   
   # 启动 Kafka 服务器
   kafka-server-start.sh config/server.properties
   ```

2. 克隆项目
   ```bash
   git clone https://github.com/zhuyizhuo/spring-boot-samples.git
   cd spring-boot-samples/spring-boot-3.x-samples/kafka-demo
   ```

3. 修改配置文件（可选）
   编辑 `src/main/resources/application.yml` 文件，修改 Kafka 连接信息
   ```yaml
   spring:
     kafka:
       producer:
         bootstrap-servers: localhost:9092  # 修改为你的 Kafka 服务器地址
       consumer:
         bootstrap-servers: localhost:9092  # 修改为你的 Kafka 服务器地址
   ```

4. 构建项目
   ```bash
   mvn clean package
   ```

5. 运行项目
   ```bash
   java -jar target/kafka-demo-1.0-SNAPSHOT.jar
   ```
   或者直接在 IDE 中运行 `KafkaDemoApplication.java` 类

### 访问地址

项目启动成功后，可以通过以下地址访问：

- 首页（测试界面）: `http://localhost:8080/kafka-demo/`
- API 文档: `http://localhost:8080/kafka-demo/swagger-ui.html`

## 功能说明

### 1. Web 测试界面

Web 界面提供了以下功能：

- **消息发送**：支持发送字符串消息、JSON 消息和批量消息
- **实时统计**：显示各类消息的接收数量
- **操作反馈**：实时显示操作结果

### 2. Kafka 配置

项目会自动创建三个主题：

- `kafka-demo-topic`: 用于发送普通字符串消息，保留 1 天
- `kafka-demo-json-topic`: 用于发送 JSON 格式消息，保留 7 天
- `kafka-demo-batch-topic`: 用于批量发送消息，保留 1 小时

### 3. API 接口

项目提供了以下 API 接口：

- `POST /send/string`: 发送字符串消息
- `POST /send/json`: 发送 JSON 消息
- `POST /send/batch`: 批量发送消息
- `GET /api/stats`: 获取消息接收统计

## 使用示例

### 1. 发送字符串消息

**Web 界面操作**：
1. 在字符串消息区域输入消息内容
2. 点击"发送"按钮

**API 调用**：
```bash
curl -X POST "http://localhost:8080/kafka-demo/send/string" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "message=Hello%20Kafka"
```

### 2. 发送 JSON 消息

**Web 界面操作**：
1. 在 JSON 消息区域输入消息内容和发送者名称
2. 点击"发送"按钮

**API 调用**：
```bash
curl -X POST "http://localhost:8080/kafka-demo/send/json" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "content=Hello%20JSON%20Message&sender=user1"
```

### 3. 批量发送消息

**Web 界面操作**：
1. 在批量消息区域输入消息内容、发送者名称和发送数量
2. 点击"批量发送"按钮

**API 调用**：
```bash
curl -X POST "http://localhost:8080/kafka-demo/send/batch" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "content=Batch%20Message&sender=user1&count=10"
```

## 故障排除

### 常见问题

1. **连接 Kafka 失败**
   - 确保 Kafka 服务正在运行
   - 检查 application.yml 中的 bootstrap-servers 配置是否正确
   - 检查防火墙设置，确保 9092 端口可访问

2. **消息发送失败**
   - 检查 Kafka 服务器状态
   - 查看应用日志中的错误信息
   - 确认主题是否已创建（应用启动时会自动创建）

3. **消息接收失败**
   - 检查消费者组配置是否正确
   - 确认主题存在且有消息发送到该主题
   - 查看消费者日志中的错误信息

## 监控与日志

应用启动后会输出以下信息：

- 应用访问地址
- 消息发送成功/失败日志
- 消息接收日志（包含分区和偏移量信息）
- 主题创建日志

## 扩展功能

本项目可以扩展以支持以下功能：

- 消息重试机制
- 死信队列
- 事务支持
- 消息幂等性处理
- 消费者偏移量管理
- 消息压缩

## 参考文档

- [Spring Kafka 官方文档](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [Apache Kafka 官方文档](https://kafka.apache.org/documentation/)
- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建你的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交你的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启一个 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 作者

**zhuyizhuo** - [GitHub](https://github.com/zhuyizhuo)