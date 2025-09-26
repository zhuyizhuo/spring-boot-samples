# Spring Boot 集成 RabbitMQ

## 📋 模块介绍

本模块演示了在 Spring Boot 项目中集成 RabbitMQ 消息队列的基本实现，包括消息的发送和接收功能。

## ✨ 功能特点

- ✅ RabbitMQ 消息发送
- ✅ RabbitMQ 消息接收
- ✅ 支持单元测试验证
- ✅ 可通过管理控制台监控消息

## 🛠 技术栈

- Spring Boot 2.x
- Spring AMQP
- RabbitMQ

## 📦 依赖配置

核心依赖配置如下：

```xml
<dependencies>
    <!-- RabbitMQ 集成 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    
    <!-- 测试依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
    </dependency>
</dependencies>
```

## 🔧 配置说明

在 `application.yml` 文件中配置 RabbitMQ 连接信息：

```yaml
spring:
  rabbitmq:
    host: 127.0.0.1    # RabbitMQ 服务器地址
    port: 5672         # RabbitMQ 服务器端口（默认5672）
    username: guest    # 用户名
    password: guest    # 密码
    #virtual-host: /   # 虚拟主机（可选，默认为/）
    
    # 以下为可选高级配置
    #listener:
    #  simple:
    #    acknowledge-mode: manual # 手动应答
    #    concurrency: 5 # 消费端最小并发数
    #    max-concurrency: 10 # 消费端最大并发数
    #    prefetch: 5 # 一次请求中预处理的消息数量
    #cache:
    #  channel:
    #    size: 50 # 缓存的channel数量
    #publisher-confirms: true # 发送成功回掉
    #publisher-returns: true
```

## 🚀 快速开始

### 准备工作

1. 确保您已经安装并启动了 RabbitMQ 服务器
2. 修改 `application.yml` 中的 RabbitMQ 连接配置，确保与您的实际环境匹配

### 运行示例

1. 通过 IDE 运行 `RabbitmqDemoApplicationTests` 中的单元测试
2. 观察控制台输出，查看消息发送和接收的结果
3. 可以登录 RabbitMQ 管理后台（通常为 `http://localhost:15672`）查看消息队列状态

## 📝 消息模型

该示例演示了基本的点对点消息模型，包括：

1. **消息生产者**：负责发送消息到 RabbitMQ 服务器
2. **消息队列**：存储消息的缓冲区
3. **消息消费者**：从队列中接收并处理消息

## 🔍 常见问题

### 1. 连接失败
- 确认 RabbitMQ 服务是否已启动
- 检查连接地址、端口、用户名和密码是否正确
- 确保网络连接通畅，防火墙未阻止连接

### 2. 消息未接收
- 检查消息队列是否存在
- 确认消费者是否正确绑定到队列
- 查看日志是否有错误信息

## 📚 参考资料

- [Spring AMQP 官方文档](https://docs.spring.io/spring-amqp/docs/current/reference/html/)
- [RabbitMQ 官方文档](https://www.rabbitmq.com/documentation.html)