# Spring Boot 3.x 集成 Sentry 示例项目

本项目演示了如何在 Spring Boot 3.x 应用中集成 Sentry 进行错误监控、性能跟踪和异常管理。

## 项目结构

```
sentry-demo/
├── src/
│   ├── main/
│   │   ├── java/com/github/zhuyizhuo/sentry/
│   │   │   ├── SentryDemoApplication.java      # 应用入口
│   │   │   ├── controller/                     # 控制器层
│   │   │   │   └── SentryController.java       # Sentry 演示控制器
│   │   │   ├── service/                        # 服务层
│   │   │   │   └── SentryService.java          # Sentry 服务层演示
│   │   │   └── config/                         # 配置类
│   │   │       ├── SentryConfig.java           # Sentry 自定义配置
│   │   │       └── GlobalExceptionHandler.java # 全局异常处理器
│   │   └── resources/
│   │       └── application.yml                 # 应用配置文件
│   └── test/                                  # 测试目录
├── pom.xml                                    # Maven 配置文件
└── README.md                                  # 项目说明文档
```

## 功能特性

- ✅ 自动捕获异常并发送到 Sentry
- ✅ 手动记录异常和消息
- ✅ 性能监控和事务跟踪
- ✅ 用户信息关联
- ✅ 自定义标签和上下文信息
- ✅ 全局异常处理
- ✅ 敏感信息过滤
- ✅ API 文档（Swagger UI）

## 快速开始

### 前提条件

- JDK 17 或更高版本
- Maven 3.6+ 或 Gradle 7.0+
- Sentry 账户（免费版即可）

### 配置 Sentry

1. 在 [Sentry.io](https://sentry.io) 创建账户或登录
2. 创建一个新项目，选择 Spring Boot 作为平台
3. 复制生成的 DSN（Data Source Name）

### 项目配置

1. 修改 `src/main/resources/application.yml` 文件：

```yaml
sentry:
  # 替换为您的 Sentry DSN
  dsn: https://your-project-key@sentry.io/your-project-id
  # 根据环境设置（development, staging, production 等）
  environment: development
  # 采样率配置
  traces-sample-rate: 1.0
  sample-rate: 1.0
  # 是否发送默认的个人身份信息
  send-default-pii: false
```

### 构建和运行

```bash
# 构建项目
mvn clean package

# 运行应用
java -jar target/sentry-demo-1.0.0.jar
```

或者使用 Spring Boot Maven 插件直接运行：

```bash
mvn spring-boot:run
```

## API 接口说明

应用启动后，可以访问以下接口进行测试：

### 1. 正常接口
- **完整URL**: `http://localhost:8082/sentry-demo/api/sentry/normal`
- **方法**: `GET`
- **描述**: 正常请求，无异常抛出

### 2. 自动捕获异常
- **完整URL**: `http://localhost:8082/sentry-demo/api/sentry/exception`
- **方法**: `GET`
- **描述**: 抛出异常，将被 Sentry 自动捕获

### 3. 手动发送异常
- **完整URL**: `http://localhost:8082/sentry-demo/api/sentry/manual-exception`
- **方法**: `GET`
- **描述**: 手动捕获并发送异常到 Sentry

### 4. 性能监控示例
- **完整URL**: `http://localhost:8082/sentry-demo/api/sentry/performance`
- **方法**: `GET`
- **描述**: 演示性能监控功能

### 5. 用户信息跟踪
- **完整URL**: `http://localhost:8082/sentry-demo/api/sentry/user-tracking`
- **方法**: `POST`
- **请求体**: 
  ```json
  {
    "id": "user123",
    "username": "testuser",
    "email": "test@example.com"
  }
  ```
- **描述**: 设置用户信息并记录到 Sentry

### 6. 服务层异常测试
- **完整URL**: `http://localhost:8082/sentry-demo/api/sentry/service-exception`
- **方法**: `GET`
- **描述**: 测试服务层异常的传播和捕获

### 7. 慢操作测试
- **完整URL**: `http://localhost:8082/sentry-demo/api/sentry/slow-operation`
- **方法**: `GET`
- **描述**: 模拟慢操作，用于性能监控测试

### 8. 自定义标签测试
- **完整URL**: `http://localhost:8082/sentry-demo/api/sentry/custom-tags`
- **方法**: `GET`
- **描述**: 设置自定义标签并发送到 Sentry

## API 文档

项目集成了 Swagger UI，可以访问以下地址查看完整的 API 文档：

```
http://localhost:8082/sentry-demo/swagger-ui.html
```

## Sentry 控制台

在 Sentry 控制台中，您可以查看：

1. **Issues**: 所有捕获的异常和错误
2. **Performance**: 性能监控数据和事务跟踪
3. **Replays**: 用户会话回放（如果启用）
4. **Dashboards**: 自定义仪表盘
5. **Settings**: 项目配置和集成设置

## 验证测试

应用启动后，可以通过以下方式验证Sentry集成是否正常工作：

1. **测试自动异常上报**：
   ```bash
   curl http://localhost:8082/sentry-demo/api/sentry/exception
   ```
   这将触发一个运行时异常并自动上报到Sentry。

2. **测试手动异常上报**：
   ```bash
   curl http://localhost:8082/sentry-demo/api/sentry/manual-exception
   ```
   这将手动捕获并发送异常到Sentry。

3. **检查Sentry控制台**：
   访问您的Sentry项目控制台，查看是否有新的异常事件生成。

## 自定义配置

### 1. 环境变量配置

您可以通过环境变量覆盖配置：

```bash
# Linux/Mac
SENTRY_DSN=https://your-project-key@sentry.io/your-project-id \
SENTRY_ENVIRONMENT=production \
SENTRY_TRACES_SAMPLE_RATE=1.0 \
java -jar target/sentry-demo-1.0.0.jar

# Windows
set SENTRY_DSN=https://your-project-key@sentry.io/your-project-id
set SENTRY_ENVIRONMENT=production
set SENTRY_TRACES_SAMPLE_RATE=1.0
java -jar target/sentry-demo-1.0.0.jar
```

### 2. 采样率调整

在高流量应用中，您可能需要调整采样率以减少数据量：

```yaml
sentry:
  traces-sample-rate: 0.1  # 只采样 10% 的请求
  profiles-sample-rate: 0.2  # 只采样 20% 的性能分析
  sample-rate: 0.5  # 只采样 50% 的异常
```

### 3. 敏感信息过滤

项目已在 `SentryConfig.java` 中实现了基本的敏感信息过滤和正确的初始化配置：

```java
@Configuration
public class SentryConfig {

    @Value("${sentry.dsn}")
    private String dsn;
    
    @Value("${sentry.environment:development}")
    private String environment;
    
    @Value("${sentry.traces-sample-rate:1.0}")
    private double tracesSampleRate;
    
    @Value("${sentry.sample-rate:1.0}")
    private double sampleRate;
    
    @PostConstruct
    public void initSentry() {
        // 直接初始化Sentry
        Sentry.init(options -> {
            options.setDsn(dsn);
            options.setEnvironment(environment);
            options.setTracesSampleRate(tracesSampleRate);
            options.setSampleRate(sampleRate);
            options.setSendDefaultPii(false);
            
            // 敏感信息过滤
            options.setBeforeSend((event, hint) -> {
                // 实现敏感信息过滤逻辑
                return event;
            });
        });
    }
}

## 最佳实践

1. **区分环境**: 为开发、测试、生产环境设置不同的 environment
2. **版本标记**: 使用 release 标记应用版本，便于问题定位
3. **用户信息**: 仅收集必要的用户信息，遵循隐私保护法规
4. **敏感信息**: 确保过滤掉密码、令牌等敏感信息
5. **自定义标签**: 使用标签对问题进行分类，便于筛选
6. **错误上下文**: 添加足够的上下文信息，便于问题诊断
7. **定期清理**: 定期清理旧的事件和数据，保持控制台整洁

## 常见问题

### 1. 异常未发送到 Sentry

- 检查 DSN 是否正确配置
- 确认网络连接是否正常
- 检查采样率设置
- 查看应用日志中的 Sentry 相关信息

### 2. 性能监控无数据

- 确保 `traces-sample-rate` 设置正确
- 验证事务是否正确创建和结束
- 检查是否有防火墙阻止了连接

### 3. 敏感信息泄露

- 配置 `beforeSend` 钩子过滤敏感信息
- 禁用 `send-default-pii` 选项
- 使用自定义的事件处理器

## 参考资源

- [Sentry 官方文档](https://docs.sentry.io/)
- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Sentry Java SDK](https://docs.sentry.io/platforms/java/)
- [Spring Boot 与 Sentry 集成指南](https://docs.sentry.io/platforms/java/guides/spring-boot/)

## 许可证

本项目采用 MIT 许可证 - 详情请查看 LICENSE 文件