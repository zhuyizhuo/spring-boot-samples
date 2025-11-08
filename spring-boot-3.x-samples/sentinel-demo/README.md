# Spring Boot 3.x Sentinel 入门指南

本项目是一个基于Spring Boot 3.x和Alibaba Sentinel的限流熔断示例，专为Java开发者提供Sentinel的入门学习指南。本指南将详细介绍Sentinel的基本概念、集成方法、使用技巧和最佳实践。

## 什么是 Sentinel？

Sentinel 是阿里巴巴开源的分布式系统流量控制组件，它以流量为切入点，从流量控制、熔断降级、系统负载保护等多个维度来保障服务的稳定性。

### 核心功能特性

- **流量控制**：支持基于QPS或并发线程数的限流，可精确到调用关系
- **熔断降级**：当调用的资源出现异常时，可以自动进行熔断降级
- **系统负载保护**：根据系统负载自动调整流量，防止系统被压垮
- **热点参数限流**：对特定参数进行限流保护
- **实时监控**：提供实时的流量监控和统计信息
- **动态规则配置**：支持多种规则配置方式，可动态调整

## 技术栈
- Java 17
- Spring Boot 3.2.5
- Alibaba Sentinel 1.8.6
- Spring Cloud Alibaba Sentinel 2022.0.0.0
- Maven
- SpringDoc OpenAPI

## 项目结构

```
sentinel-demo/
├── src/
│   ├── main/
│   │   ├── java/com/github/zhuyizhuo/sentinel/
│   │   │   ├── SentinelDemoApplication.java   # 应用入口
│   │   │   ├── config/                        # 配置类（包含Sentinel配置）
│   │   │   ├── controller/                    # 控制器（API接口）
│   │   │   ├── handler/                       # 自定义异常处理器
│   │   │   └── service/                       # 业务服务层（包含Sentinel资源定义）
│   │   └── resources/
│   │       ├── application.properties         # 应用配置
│   │       └── static/
│   │           └── index.html                 # 测试页面
│   └── test/                                  # 测试代码
├── pom.xml                                    # Maven配置
└── README.md                                  # 项目说明（本文档）
```

## 快速开始

### 1. 前置条件
- JDK 17 或更高版本
- Maven 3.6+ 构建工具
- Sentinel 控制台（可选，但推荐）

### 2. 启动 Sentinel 控制台（推荐）

```bash
# 下载 Sentinel 控制台
# Windows 用户可以直接从GitHub下载：
# https://github.com/alibaba/Sentinel/releases/download/v1.8.6/sentinel-dashboard-1.8.6.jar

# 启动控制台
java -jar sentinel-dashboard-1.8.6.jar
```

控制台默认运行在 http://localhost:8080，默认账号密码：sentinel/sentinel

### 3. 运行本项目

```bash
# 构建项目
mvn clean package

# 运行应用
java -jar target/sentinel-demo-1.0-SNAPSHOT.jar
```

或者直接在 IDE 中运行 `SentinelDemoApplication.java` 类。

## Sentinel 入门使用指南

### 1. Maven 依赖配置

在 `pom.xml` 中添加 Sentinel 相关依赖：

```xml
<!-- Spring Boot 依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Sentinel 核心依赖 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>

<!-- Sentinel 注解支持 -->
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-annotation-aspectj</artifactId>
</dependency>
```

### 2. 配置 Sentinel

在 `application.properties` 中配置 Sentinel 相关参数：

```properties
# 应用名称，用于在 Sentinel 控制台中标识
spring.application.name=sentinel-demo

# Sentinel 控制台地址
spring.cloud.sentinel.transport.dashboard=localhost:8080

# 应用与 Sentinel 控制台交互的端口
spring.cloud.sentinel.transport.port=8719

# 关闭 Sentinel 控制台懒加载，启动时立即连接
spring.cloud.sentinel.eager=true
```

### 3. 启用 Sentinel 注解支持

创建 Sentinel 配置类，启用注解支持：

```java
package com.github.zhuyizhuo.sentinel.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentinelConfig {
    // 注入 SentinelResource 注解支持的切面
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}
```

### 4. 使用 Sentinel 进行资源保护

#### 4.1 注解方式（推荐）

使用 `@SentinelResource` 注解标记需要保护的方法：

```java
package com.github.zhuyizhuo.sentinel.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.github.zhuyizhuo.sentinel.handler.CustomBlockHandler;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    // 基础限流方法
    @SentinelResource(
            value = "helloService",  // 资源名称
            blockHandler = "handleFlowException",  // 限流处理方法
            blockHandlerClass = CustomBlockHandler.class,  // 限流处理类
            fallback = "helloFallback"  // 降级方法
    )
    public String hello() {
        return "Hello Sentinel!";
    }
    
    // 降级方法
    public String helloFallback() {
        return "服务降级中，请稍后再试";
    }
}
```

#### 4.2 编码方式（灵活控制）

使用 Sentinel API 手动进行资源保护：

```java
public String codingStyleLimit(String param) {
    Entry entry = null;
    try {
        // 资源名称
        entry = SphU.entry("codingStyleResource");
        
        // 业务逻辑
        return "业务处理成功";
    } catch (BlockException e) {
        // 限流处理
        return "请求过于频繁，请稍后再试";
    } finally {
        if (entry != null) {
            entry.exit();
        }
    }
}
```

### 5. 自定义异常处理

创建自定义限流异常处理器：

```java
package com.github.zhuyizhuo.sentinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CustomBlockHandler {
    private static final Logger logger = Logger.getLogger(CustomBlockHandler.class.getName());
    
    /**
     * 处理限流异常
     * @param e 限流异常
     * @return 统一的错误响应
     */
    public static ResponseEntity<Map<String, Object>> handleFlowException(BlockException e) {
        logger.warning("[限流] 请求被限流，异常信息: " + e.getMessage());
        Map<String, Object> result = new HashMap<>();
        result.put("code", 429);
        result.put("message", "请求过于频繁，请稍后再试");
        result.put("type", "flow");
        result.put("timestamp", System.currentTimeMillis());
        
        return new ResponseEntity<>(result, HttpStatus.TOO_MANY_REQUESTS);
    }
}
```

### 6. 配置限流规则

#### 6.1 通过控制台配置规则（推荐）

1. 访问 Sentinel 控制台：http://localhost:8080
2. 确保应用已经启动并访问过至少一次 API（Sentinel 懒加载特性）
3. 在控制台左侧选择您的应用
4. 点击「流控规则」-> 「新增流控规则」
5. 配置规则：
   - 资源名：`helloService`（对应代码中的 value）
   - 阈值类型：QPS
   - 单机阈值：1（每秒最多1个请求）
   - 流控模式：直接
   - 流控效果：快速失败

#### 6.2 通过代码配置规则

```java
// 定义流控规则
private static void initFlowRules() {
    List<FlowRule> rules = new ArrayList<>();
    FlowRule rule = new FlowRule();
    rule.setResource("helloService"); // 资源名
    rule.setGrade(RuleConstant.FLOW_GRADE_QPS); // 限流阈值类型：QPS
    rule.setCount(1); // QPS阈值：1
    rules.add(rule);
    // 加载规则
    FlowRuleManager.loadRules(rules);
}
```

#### 6.3 通过文件配置规则

创建 `sentinel-rules.json` 文件：

```json
[
  {
    "resource": "helloService",
    "limitApp": "default",
    "grade": 1,
    "count": 1,
    "strategy": 0,
    "controlBehavior": 0
  }
]
```

在 `application.properties` 中配置：

```properties
spring.cloud.sentinel.datasource.ds1.file.file=classpath:sentinel-rules.json
spring.cloud.sentinel.datasource.ds1.file.data-type=json
spring.cloud.sentinel.datasource.ds1.file.rule-type=flow
```

## 触发限流测试

### 方法一：使用测试页面

1. 访问测试页面：http://localhost:8080/sentinel-demo/index.html
2. 在「快速测试」部分选择要测试的 API
3. 设置调用次数（如：20次）和间隔时间（如：50毫秒）
4. 点击「开始测试」
5. 观察结果区域，超过限流阈值的请求会显示 429 状态码

### 方法二：使用浏览器手动刷新

1. 访问：http://localhost:8080/sentinel-demo/api/hello
2. 快速刷新页面多次
3. 超过限流阈值后，页面会显示限流提示

### 方法三：使用命令行工具（如 curl）

```bash
for i in {1..10}; do curl http://localhost:8080/sentinel-demo/api/hello; echo "\n"; sleep 0.1; done
```

## 观察限流效果

### 1. 查看控制台监控

1. 访问 Sentinel 控制台
2. 点击「实时监控」查看流量监控
3. 点击「簇点链路」查看资源调用链

### 2. 查看日志输出

在应用日志中，可以看到类似以下的限流日志：

```
[限流] 请求被限流，异常信息: null
```

### 3. 查看返回结果

被限流的请求会返回 429 状态码和自定义的限流提示信息。

## 熔断降级配置

### 1. 熔断规则配置

在 Sentinel 控制台中配置熔断规则：

1. 点击「熔断规则」-> 「新增熔断规则」
2. 配置规则：
   - 资源名：`demoService`
   - 熔断策略：异常比例
   - 比例阈值：0.5（50%的请求异常就熔断）
   - 熔断时长：10（熔断10秒）
   - 最小请求数：5（最小请求数）

### 2. 触发熔断测试

访问会触发异常的接口：

```bash
http://localhost:8080/sentinel-demo/api/demo?param=error
```

快速访问多次后，服务会被熔断。

## 热点参数限流

### 1. 热点规则配置

在 Sentinel 控制台中配置热点规则：

1. 点击「热点规则」-> 「新增热点规则」
2. 配置规则：
   - 资源名：`hotParamService`
   - 参数索引：0（第一个参数）
   - 单机阈值：1

### 2. 测试热点限流

```bash
http://localhost:8080/sentinel-demo/api/hot?hotParam=test&normalParam=normal
```

## 生产环境最佳实践

### 1. 规则持久化

生产环境中必须配置规则持久化，避免应用重启后规则丢失。推荐以下几种方式：

- 配置中心存储：使用 Nacos、Apollo 等配置中心
- 文件存储：定期将规则保存到文件
- 数据库存储：将规则存储在数据库中

### 2. 配置示例（Nacos）

```properties
# Nacos 数据源配置
spring.cloud.sentinel.datasource.ds.nacos.server-addr=localhost:8848
spring.cloud.sentinel.datasource.ds.nacos.dataId=sentinel-rules
spring.cloud.sentinel.datasource.ds.nacos.groupId=DEFAULT_GROUP
spring.cloud.sentinel.datasource.ds.nacos.data-type=json
spring.cloud.sentinel.datasource.ds.nacos.rule-type=flow
```

### 3. 异常处理优化

- 使用统一异常处理类捕获限流异常
- 提供友好的错误提示
- 记录详细的限流日志

### 4. 监控告警

- 配置 Sentinel 控制台的告警功能
- 接入企业告警系统（如钉钉、邮件等）
- 设置合理的告警阈值

## 常见问题与解决方案

### 1. 控制台看不到数据

**问题**：应用已启动，但 Sentinel 控制台看不到任何数据

**解决方案**：
- 确保配置了 `spring.cloud.sentinel.transport.dashboard`
- 确保 Sentinel 控制台已启动并能访问
- 访问一下应用的 API，Sentinel 默认是懒加载模式
- 检查网络连接，确保应用能连接到控制台

### 2. 限流规则不生效

**问题**：配置了限流规则，但没有生效

**解决方案**：
- 检查资源名称是否正确匹配
- 确认规则类型（QPS/线程数）是否适合你的场景
- 检查阈值设置是否合理
- 查看应用日志，确认是否有异常信息

### 3. 规则丢失

**问题**：应用重启后，限流规则丢失

**解决方案**：
- 配置规则持久化
- 使用配置中心或数据库存储规则

## 深入学习资源

### 官方文档
- [Sentinel 官方文档](https://sentinelguard.io/zh-cn/docs/introduction.html)
- [Spring Cloud Alibaba Sentinel 文档](https://sca.aliyun.com/zh-cn/docs/2022.0.0.0/user-guide/sentinel/)

### 进阶学习
- Sentinel 源码分析
- Sentinel 集群流控
- 大规模微服务架构中的 Sentinel 最佳实践

### 其他资源
- [Sentinel GitHub 仓库](https://github.com/alibaba/Sentinel)
- [Spring Boot 官方文档](https://spring.io/guides/gs/spring-boot/)

---

## 如何贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目！

## 许可证

本项目采用 MIT 许可证 - 详情请查看 LICENSE 文件