# Spring Boot 集成 Sentinel 示例

本示例演示了如何在 Spring Boot 项目中集成并使用 Alibaba Sentinel 进行流量控制、熔断降级等功能。

## Sentinel 简介

[Sentinel](https://github.com/alibaba/Sentinel) 是阿里巴巴开源的流量控制组件，主要功能包括：

- **流量控制**：根据不同维度（QPS、并发线程数）对资源调用进行限流
- **熔断降级**：当资源出现不稳定时自动降级，避免级联故障
- **系统负载保护**：根据系统负载自动调整流量
- **热点参数限流**：对热点参数进行精确限流
- **实时监控与控制台**：提供实时监控和配置界面

## 项目结构

```
sentinel/
├── src/main/java/com/github/zhuyizhuo/
│   ├── SentinelApplication.java      # 应用入口
│   ├── config/
│   │   ├── SentinelConfig.java       # Sentinel 配置类
│   │   └── SentinelRulesConfig.java  # 限流规则配置类
│   ├── controller/
│   │   └── UserController.java       # 控制器，演示 Sentinel 使用
│   └── service/
│       └── UserService.java          # 服务类，演示注解使用
├── src/main/resources/
│   └── application.yml              # 应用配置
├── pom.xml                          # Maven 依赖
└── README.md                        # 文档
```

## 核心功能演示

### 1. 注解式使用（推荐）

通过 `@SentinelResource` 注解可以方便地对方法进行保护：

- **限流处理**：通过 `blockHandler` 指定限流时的处理函数
- **异常处理**：通过 `fallback` 指定异常时的处理函数
- **资源命名**：通过 `value` 指定资源名称

### 2. 编程式使用

通过 `SphU.entry()` 和 `entry.exit()` 手动定义资源的保护范围：

- 可以更灵活地控制保护范围
- 支持上下文环境设置

### 3. 规则配置

演示了如何通过代码配置限流规则：

- QPS 限流：限制每秒请求数
- 资源级别限流：针对不同接口设置不同的限流阈值

## 快速开始

### 1. 运行 Sentinel Dashboard（可选）

下载 Sentinel Dashboard：
```bash
# 下载最新版本的 jar 包
wget https://github.com/alibaba/Sentinel/releases/download/v1.8.6/sentinel-dashboard-1.8.6.jar

# 启动控制台
java -jar sentinel-dashboard-1.8.6.jar --server.port=8080
```

访问控制台：http://localhost:8080（默认用户名密码：sentinel/sentinel）

### 2. 运行示例项目

**方式一：通过 IDE 运行**

直接运行 `SentinelApplication` 类的 `main` 方法。

**方式二：通过 Maven 运行**

```bash
# 编译打包
mvn clean package

# 运行，连接到控制台
java -jar -Dcsp.sentinel.dashboard.server=localhost:8080 target/sentinel-1.0-SNAPSHOT.jar
```

## API 接口列表

### 编程式使用接口

1. **编程式限流演示**
   - URL: `GET /api/users/programmatic/{userId}`
   - 描述: 演示编程式使用 Sentinel 进行限流
   - 限流规则: 每秒最多 10 个请求

2. **带上下文的编程式使用**
   - URL: `GET /api/users/context/{userId}`
   - 描述: 演示带上下文环境的 Sentinel 使用

### 注解式使用接口

3. **获取用户信息**
   - URL: `GET /api/users/{userId}`
   - 描述: 通过注解获取用户信息
   - 限流规则: 每秒最多 5 个请求
   - 特殊参数: userId="001" 或 "002" 返回正常，其他会抛出异常

4. **获取用户订单**
   - URL: `GET /api/users/{userId}/orders`
   - 描述: 获取用户订单列表
   - 限流规则: 每秒最多 3 个请求

5. **创建用户**
   - URL: `POST /api/users?username=xxx`
   - 描述: 创建新用户
   - 限流规则: 每秒最多 2 个请求
   - 校验: 用户名长度不能小于 2，否则抛出异常

6. **热点参数演示**
   - URL: `GET /api/users/hotspot?param1=xxx&param2=yyy`
   - 描述: 用于热点参数限流演示

## 测试限流效果

### 1. 使用 curl 测试

```bash
# 快速连续请求触发限流
for i in {1..10}; do curl http://localhost:8080/api/users/001; echo ""; done
```

### 2. 使用 JMeter 或 Postman 进行压力测试

- 设置线程数大于限流阈值
- 观察返回结果，部分请求会被限流

## Sentinel Dashboard 使用

### 1. 配置限流规则

1. 在 Dashboard 中选择您的应用
2. 点击「流控规则」→「新增流控规则」
3. 设置资源名（如：getUserInfo）、阈值类型（QPS）、单机阈值等
4. 点击「新增」按钮保存规则

### 2. 配置熔断规则

1. 点击「熔断规则」→「新增熔断规则」
2. 设置资源名、熔断策略（慢调用比例/异常比例/异常数）
3. 设置阈值和熔断时长
4. 点击「新增」按钮保存规则

### 3. 查看监控数据

- 实时监控：查看资源的实时 QPS、响应时间等
- 簇点链路：查看所有资源的调用链路
- 热点参数：查看热点参数的监控数据

## 注意事项

1. **规则持久化**：本示例中的规则是内存型的，应用重启后会丢失。生产环境建议使用动态数据源（如 Nacos、ZooKeeper、Redis 等）进行规则持久化。

2. **性能影响**：Sentinel 的核心资源占用非常小，但仍建议根据实际业务需求配置合理的规则。

3. **熔断降级策略**：根据业务特点选择合适的熔断策略和阈值。

4. **JVM 参数**：连接 Dashboard 时需要配置 JVM 参数 `-Dcsp.sentinel.dashboard.server=localhost:8080`

## 进阶配置

### 规则持久化配置

#### 使用 Nacos 作为数据源

1. 添加依赖：
```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
    <version>1.8.6</version>
</dependency>
```

2. 配置 Nacos 数据源：
```yaml
spring:
  cloud:
    sentinel:
      datasource:
        ds:
          nacos:
            server-addr: localhost:8848
            dataId: sentinel-rules
            groupId: DEFAULT_GROUP
            rule-type: flow
```

### 自定义异常处理

可以实现 `UrlBlockHandler` 接口自定义限流异常处理：

```java
@Component
public class CustomUrlBlockHandler implements UrlBlockHandler {
    @Override
    public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
        // 自定义响应内容
        response.setStatus(200);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\":429,\"message\":\"请求太频繁，请稍后重试\"}");
    }
}
```

## 参考文档

- [Sentinel 官方文档](https://sentinelguard.io/zh-cn/docs/quick-start.html)
- [Sentinel GitHub 仓库](https://github.com/alibaba/Sentinel)
- [Spring Cloud Alibaba Sentinel](https://spring-cloud-alibaba-group.github.io/github-pages/hoxton/en-us/index.html#_spring_cloud_alibaba_sentinel)

## 总结

本示例演示了 Sentinel 的基本用法，包括编程式和注解式使用方式，以及如何配置限流规则。在实际项目中，建议结合 Sentinel Dashboard 进行动态规则配置，并使用持久化数据源保存规则，以提高系统的可用性和可维护性。