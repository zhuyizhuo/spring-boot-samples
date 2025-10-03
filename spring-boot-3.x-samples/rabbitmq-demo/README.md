# Spring Boot 3.x RabbitMQ 集成示例

这是一个基于 Spring Boot 3.x 的 RabbitMQ 消息队列集成示例项目，展示了如何在现代 Spring Boot 应用中使用 RabbitMQ 进行消息传递。

## 📋 项目特性

- **Spring Boot 3.x** 最新版本支持
- **Java 17+** 现代 Java 特性
- **多种交换机类型** Direct、Topic、Fanout、Headers
- **消息确认机制** 发送确认和消费确认
- **死信队列** 处理失败消息
- **延迟消息** TTL 支持
- **批量消息** 高效消息处理
- **Swagger UI** API 文档自动生成
- **健康检查** 系统监控支持
- **多环境配置** 开发、测试、生产环境

## 🛠️ 技术栈

- **Spring Boot**: 3.2.5
- **Spring AMQP**: 消息队列抽象层
- **RabbitMQ**: 消息代理
- **Jackson**: JSON 序列化
- **SpringDoc OpenAPI**: API 文档
- **Maven**: 构建工具

## 📁 项目结构

```
rabbitmq-demo/
├── src/main/java/com/github/zhuyizhuo/rabbitmq/demo/
│   ├── RabbitMqDemoApplication.java          # 启动类
│   ├── config/
│   │   └── RabbitConfig.java                 # RabbitMQ 配置
│   ├── controller/
│   │   └── MessageController.java            # REST API 控制器
│   ├── model/
│   │   ├── MessageDto.java                   # 消息数据传输对象
│   │   └── MessageType.java                  # 消息类型枚举
│   └── service/
│       ├── MessageProducerService.java       # 消息生产者服务
│       └── MessageConsumerService.java       # 消息消费者服务
├── src/main/resources/
│   └── application.yml                       # 应用配置文件
├── pom.xml                                   # Maven 配置
└── README.md                                 # 项目说明
```

## 🚀 快速开始

### 1. 环境要求

- **JDK 17+**
- **Maven 3.8+**
- **RabbitMQ 3.8+**

### 2. 安装 RabbitMQ

#### 使用 Docker (推荐)

```bash
# 启动 RabbitMQ 容器
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  rabbitmq:3-management

# 访问管理界面: http://localhost:15672
# 用户名/密码: guest/guest
```

#### 本地安装

参考 [RabbitMQ 官方文档](https://www.rabbitmq.com/download.html)

### 3. 启动应用

```bash
# 克隆项目
git clone <repository-url>
cd spring-boot-samples/spring-boot-3.x-samples/rabbitmq-demo

# 编译运行
mvn clean compile
mvn spring-boot:run

# 或者
mvn clean package
java -jar target/rabbitmq-demo-1.0-SNAPSHOT.jar
```

### 4. 访问应用

- **应用地址**: http://localhost:8080/rabbitmq-demo
- **API 文档**: http://localhost:8080/rabbitmq-demo/swagger-ui.html
- **健康检查**: http://localhost:8080/rabbitmq-demo/actuator/health

## 📖 API 接口说明

### 消息发送接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/messages/direct` | POST | 发送 Direct 消息 |
| `/api/v1/messages/topic` | POST | 发送 Topic 消息 |
| `/api/v1/messages/topic/user` | POST | 发送用户消息 |
| `/api/v1/messages/topic/order` | POST | 发送订单消息 |
| `/api/v1/messages/fanout` | POST | 发送广播消息 |
| `/api/v1/messages/headers` | POST | 发送 Headers 消息 |
| `/api/v1/messages/delayed` | POST | 发送延迟消息 |
| `/api/v1/messages/batch` | POST | 批量发送消息 |
| `/api/v1/messages/notification` | POST | 发送通知消息 |
| `/api/v1/messages/email` | POST | 发送邮件消息 |
| `/api/v1/messages/sms` | POST | 发送短信消息 |

### 系统接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/messages/types` | GET | 获取消息类型 |
| `/api/v1/messages/health` | GET | 健康检查 |

## 🔧 配置说明

### RabbitMQ 连接配置

```yaml
spring:
  rabbitmq:
    host: localhost        # RabbitMQ 服务器地址
    port: 5672            # 端口号
    username: guest       # 用户名
    password: guest       # 密码
    virtual-host: /       # 虚拟主机
```

### 消费者配置

```yaml
spring:
  rabbitmq:
    listener:
      simple:
        acknowledge-mode: auto    # 确认模式
        concurrency: 3           # 并发消费者数量
        max-concurrency: 10      # 最大并发数
        prefetch: 5              # 预取消息数量
```

### 生产者配置

```yaml
spring:
  rabbitmq:
    publisher-confirm-type: correlated  # 发送确认
    publisher-returns: true             # 发送失败回调
```

## 📝 使用示例

### 1. 发送简单消息

```bash
curl -X POST "http://localhost:8080/rabbitmq-demo/api/v1/messages/direct" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Hello RabbitMQ!",
    "messageType": "NORMAL",
    "sender": "user1"
  }'
```

### 2. 发送Topic消息

```bash
curl -X POST "http://localhost:8080/rabbitmq-demo/api/v1/messages/topic?routingKey=demo.topic.user.info" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "用户信息更新",
    "messageType": "USER_MESSAGE",
    "sender": "user-service"
  }'
```

### 3. 发送广播消息

```bash
curl -X POST "http://localhost:8080/rabbitmq-demo/api/v1/messages/fanout" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "系统维护通知",
    "messageType": "NOTIFICATION",
    "sender": "system"
  }'
```

### 4. 发送延迟消息

```bash
curl -X POST "http://localhost:8080/rabbitmq-demo/api/v1/messages/delayed?delaySeconds=30" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "30秒后执行的任务",
    "messageType": "NORMAL",
    "sender": "scheduler"
  }'
```

## 🏗️ 交换机和队列说明

### Direct Exchange
- **交换机**: `demo.direct.exchange`
- **队列**: `demo.direct.queue`
- **路由键**: `demo.direct.routing.key`
- **特点**: 精确匹配路由键

### Topic Exchange
- **交换机**: `demo.topic.exchange`
- **队列**: 
  - `demo.topic.queue.user` (路由键: `demo.topic.user.*`)
  - `demo.topic.queue.order` (路由键: `demo.topic.order.*`)
- **特点**: 支持通配符匹配

### Fanout Exchange
- **交换机**: `demo.fanout.exchange`
- **队列**: 
  - `demo.fanout.queue.email`
  - `demo.fanout.queue.sms`
  - `demo.fanout.queue.push`
- **特点**: 广播到所有绑定队列

### Headers Exchange
- **交换机**: `demo.headers.exchange`
- **队列**: `demo.headers.queue`
- **特点**: 基于消息头属性匹配

### Dead Letter Exchange
- **交换机**: `demo.dlx.exchange`
- **队列**: `demo.dlx.queue`
- **特点**: 处理失败或过期消息

## 🔍 消息类型

| 类型 | 代码 | 说明 |
|------|------|------|
| NORMAL | normal | 普通消息 |
| NOTIFICATION | notification | 系统通知 |
| USER_MESSAGE | user_message | 用户消息 |
| ORDER | order | 订单消息 |
| PAYMENT | payment | 支付消息 |
| EMAIL | email | 邮件消息 |
| SMS | sms | 短信消息 |

## 🐛 故障排除

### 1. 连接失败
- 检查 RabbitMQ 服务是否启动
- 确认连接配置是否正确
- 检查网络连接和防火墙设置

### 2. 消息未被消费
- 检查队列是否正确绑定
- 确认消费者是否正常启动
- 查看日志是否有异常信息

### 3. 内存不足
- 调整 RabbitMQ 内存限制
- 增加消费者数量
- 优化消息处理逻辑

## 📊 监控指标

### Spring Boot Actuator 端点
- `/actuator/health` - 健康检查
- `/actuator/metrics` - 应用指标
- `/actuator/prometheus` - Prometheus 指标

### RabbitMQ 管理界面
- 队列状态监控
- 消息速率统计
- 连接状态查看

## 🔄 扩展功能

### 1. 消息重试
项目已配置自动重试机制，失败消息会自动重试3次。

### 2. 死信队列
超过重试次数或TTL过期的消息会进入死信队列。

### 3. 消息持久化
所有队列和消息都配置为持久化存储。

### 4. 集群支持
配置支持 RabbitMQ 集群部署。

## 📚 参考资料

- [Spring AMQP 官方文档](https://spring.io/projects/spring-amqp)
- [RabbitMQ 官方文档](https://www.rabbitmq.com/documentation.html)
- [Spring Boot 3.x 文档](https://spring.io/projects/spring-boot)

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交变更
4. 推送到分支
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](../../../LICENSE) 文件了解详情。

## 👨‍💻 作者

- **zhuyizhuo** - [GitHub](https://github.com/zhuyizhuo)

---

如有问题或建议，欢迎提交 Issue 或 Pull Request！

