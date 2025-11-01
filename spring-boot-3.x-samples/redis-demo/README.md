# Spring Boot Redis 集成示例

本项目展示了 Spring Boot 3.x 与 Redis 的集成示例，包括 Redis 的常见用法和高级特性如键过期监听等功能。

## 项目结构

```
src/main/java/com/example/redisdemo/
├── RedisDemoApplication.java    # 应用主启动类
├── config/                      # 配置类
│   └── RedisConfig.java         # Redis 配置类
├── service/                     # 服务层
│   └── RedisService.java        # Redis 操作封装服务
├── controller/                  # 控制器层
│   └── RedisController.java     # REST API 控制器
├── model/                       # 数据模型
│   └── User.java                # 用户模型类
└── listener/                    # 监听器
    └── RedisKeyExpirationListener.java # Redis 键过期监听器
src/main/resources/
└── application.properties       # 应用配置文件
```

## 功能特性

### 1. Redis 操作封装

在 `RedisService` 类中封装了 Redis 的常用操作，包括：

- **字符串(String)操作**：设置、获取、删除字符串值
- **对象存储**：序列化和反序列化 Java 对象
- **哈希(Hash)操作**：设置和获取哈希字段
- **列表(List)操作**：左右推、弹出、范围查询
- **集合(Set)操作**：添加、查询、成员检查
- **有序集合(ZSet)操作**：添加带分数的元素、范围查询
- **计数器操作**：递增、递减
- **键管理**：设置过期时间、查询过期时间、批量操作键

### 2. Redis 键过期监听

实现了 `RedisKeyExpirationListener` 类，监听 Redis 中的键过期事件，并根据键的前缀进行不同的业务处理：

- **用户前缀(user:)**：处理用户相关的键过期
- **订单前缀(order:)**：处理订单相关的键过期
- **会话前缀(session:)**：处理会话相关的键过期

### 3. REST API 接口

提供了完整的 REST API 接口，用于演示和测试 Redis 的各种功能，主要包括：

- **字符串操作**：`/api/redis/string`
- **对象存储**：`/api/redis/user`
- **哈希操作**：`/api/redis/hash`
- **列表操作**：`/api/redis/list`
- **集合操作**：`/api/redis/set`
- **有序集合操作**：`/api/redis/zset`
- **计数器操作**：`/api/redis/counter`
- **键过期演示**：`/api/redis/demo/expiration`
- **键管理**：`/api/redis/keys`

## 环境要求

- JDK 17+
- Spring Boot 3.2.0
- Redis 6.0+

## 快速开始

### 1. 准备 Redis 服务器

确保 Redis 服务器已启动并运行在默认端口 6379。

### 2. 配置 Redis 连接

1. 复制配置示例文件：
   ```bash
   cp src/main/resources/application-redis.yml.demo src/main/resources/application-redis.yml
   ```

2. 编辑 `application-redis.yml` 文件，填入实际的 Redis 连接信息：
   - `host`: Redis 服务器地址
   - `port`: Redis 服务器端口
   - `password`: Redis 密码（如果有）
   - `database`: Redis 数据库索引

### 3. 配置键空间通知

要启用 Redis 键过期监听，需要在 Redis 服务器上启用键空间通知。可以通过以下方式配置：

在 Redis 配置文件中添加：
```
notify-keyspace-events Ex
```

或在 Redis 客户端中执行命令：
```
CONFIG SET notify-keyspace-events Ex
```

### 4. 启动应用

使用 Maven 构建并启动应用：

```bash
mvn clean install
mvn spring-boot:run
```

## API 使用示例

### 字符串操作

**设置字符串值**
```bash
POST /api/redis/string?key=test&value=hello&expire=60
```

**获取字符串值**
```bash
GET /api/redis/string/test
```

### 对象存储

**保存用户对象**
```bash
POST /api/redis/user
Content-Type: application/json

{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "phone": "13800138000",
  "age": 25,
  "address": "北京市"
}
```

**保存带过期时间的用户对象**
```bash
POST /api/redis/user-with-expire?expireSeconds=180
Content-Type: application/json

{
  "id": 2,
  "username": "tempuser",
  "email": "temp@example.com"
}
```

### 键过期监听演示

**创建一个带有过期时间的测试键**
```bash
POST /api/redis/demo/expiration?prefix=user&expireSeconds=10
```

创建后，可以在应用日志中观察到键过期事件的处理过程。

## 注意事项

1. **Redis 键空间通知**：确保 Redis 服务器已启用 `notify-keyspace-events Ex` 配置，否则键过期监听不会生效。

2. **序列化设置**：项目中使用了 Jackson 进行 JSON 序列化，这确保了复杂对象能够正确地存储和读取。

3. **性能考虑**：在生产环境中，请根据实际需求调整连接池配置和超时设置。

4. **异常处理**：为了简单起见，某些异常处理可能不够完善，请在实际项目中根据需求进行增强。

## 扩展建议

1. 添加缓存注解支持，如 `@Cacheable`、`@CachePut`、`@CacheEvict`
2. 实现分布式锁功能
3. 添加 Redis 集群支持
4. 增加性能监控和统计功能

## 许可证

本项目采用 MIT 许可证。