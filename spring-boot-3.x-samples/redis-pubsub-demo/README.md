# Redis 发布订阅示例项目

本项目是一个基于Spring Boot 3.x的Redis发布订阅（Publish-Subscribe）功能示例，演示了如何使用Spring Data Redis实现消息的发布和订阅功能。

## 功能特性

- 支持Redis消息的发布与订阅
- 提供多种主题的消息订阅功能
- 提供REST API接口用于发布消息和用户对象
- 集成Swagger文档，方便API测试
- 完整的日志记录
- 支持本地配置文件分离（application-local.yml）
- 提供HTML测试页面，方便手动测试
- 支持Java 8日期时间类型的JSON序列化
- 应用启动时自动打印访问地址信息

## 技术栈

- Spring Boot 3.x
- Spring Data Redis
- Redis
- SpringDoc OpenAPI
- Lombok
- Jackson-datatype-jsr310 (Java 8日期时间支持)

## 快速开始

### 1. 环境要求

- JDK 17或更高版本
- Maven 3.6.0或更高版本
- Redis 服务器（本地或远程）

### 2. 配置Redis

确保Redis服务器已启动，并在`src/main/resources/application-local.yml`文件中配置正确的Redis连接参数（本地配置文件，不会被版本控制）：

```yaml
spring:
  data:
    redis:
      host: localhost  # Redis服务器地址
      port: 6379       # Redis服务器端口
      password:        # Redis密码（如果没有密码，保持空）
      database: 0      # Redis数据库索引
      timeout: 60000   # 连接超时时间（毫秒）
      lettuce:
        pool:
          max-active: 8  # 连接池最大连接数
          max-idle: 8    # 连接池最大空闲连接数
          min-idle: 0    # 连接池最小空闲连接数
          max-wait: -1ms # 连接池最大阻塞等待时间（负值表示无限制）
```

注意：`application.yml`文件已配置为自动导入本地配置文件：
```yaml
spring.config.import: optional:classpath:application-local.yml
```

### 3. 构建和启动应用

使用Maven构建并启动应用：

```bash
# 进入项目目录
cd e:\git\github\spring-boot-samples\spring-boot-3.x-samples\redis-pubsub-demo

# 构建项目
mvn clean install

# 启动应用
mvn spring-boot:run
```

### 4. 访问服务

应用启动后，会自动在控制台打印访问地址信息：

```
应用已成功启动！
HTML测试页面: http://localhost:8083/redis-pubsub/redis-pubsub-test.html
Swagger API文档: http://localhost:8083/redis-pubsub/swagger-ui.html
```

## API接口说明

### 发布普通消息

```
POST /api/redis/publish
```

请求体：
```json
"这是一条测试消息"
```

### 发布用户消息

```
POST /api/redis/publish/user
```

请求体：
```json
"这是一条用户消息"
```

### 发布用户对象消息

```
POST /api/redis/publish/user-object
```

请求体：
```json
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com"
}
```

### 发布消息到指定主题

```
POST /api/redis/publish/{topic}
```

请求体：
```json
"这是一条指定主题的消息"
```

### 发布简单文本消息（GET请求）

```
GET /api/redis/publish?message=这是一条简单消息
```

## 项目结构说明

```
src/main/java/com/github/zhuyizhuo/springboot/redispubsub/
├── Application.java                 # 应用入口类
├── config/                          # 配置类
│   ├── RedisConfig.java             # Redis相关配置
│   ├── JacksonConfig.java           # Jackson JSON序列化配置
│   └── ApplicationRunnerConfig.java # 应用启动配置
├── controller/                      # 控制器
│   └── MessageController.java       # REST API控制器
├── listener/                        # 消息监听器
│   ├── MessageListener.java         # Redis消息监听器
│   └── UserMessageListener.java     # 用户消息监听器
├── model/                           # 实体类
│   └── User.java                    # 用户实体类
└── service/                         # 服务层
    └── MessagePublisherService.java # 消息发布服务
```

```
src/main/resources/
├── application.yml          # 主配置文件
├── application-local.yml    # 本地配置文件（不会被版本控制）
└── static/
    └── redis-pubsub-test.html  # HTML测试页面
```

## 工作原理

1. **消息发布**：通过`MessagePublisherService`使用RedisTemplate将消息发布到指定的Redis主题
2. **消息订阅**：通过`MessageListener`和`UserMessageListener`监听Redis主题，并在收到消息时进行处理
3. **配置管理**：在`RedisConfig`中配置RedisTemplate和消息监听器容器
4. **序列化配置**：在`JacksonConfig`中配置JSON序列化，支持Java 8日期时间类型
5. **启动配置**：在`ApplicationRunnerConfig`中配置应用启动时的行为，如打印访问地址

## 注意事项

1. 确保Redis服务器正常运行，并且连接参数配置正确
2. 默认配置下，应用连接到本地Redis服务器（localhost:6379）
3. 如需修改Redis连接参数，请在`application-local.yml`文件中进行调整
4. 项目使用了两个默认主题：`demo-topic`和`user-topic`，分别用于普通消息和用户消息
5. 本地配置文件`application-local.yml`用于存放敏感配置，不会被版本控制
6. HTML测试页面提供了便捷的测试界面，可以直接在浏览器中操作

## 扩展建议

1. 可以根据业务需求扩展更多类型的消息和主题
2. 可以实现消息的持久化存储
3. 可以添加消息确认机制，确保消息被成功处理
4. 可以考虑使用Redis集群提高系统的可用性和性能
5. 可以扩展HTML测试页面，添加更多的测试功能
6. 可以添加消息过滤和路由功能，实现更复杂的消息处理逻辑