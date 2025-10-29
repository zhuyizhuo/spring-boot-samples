# Spring Boot 3.x 示例集合

这是一个Spring Boot 3.x的示例项目集合，包含各种常见场景的Spring Boot应用示例。

## 项目结构
本项目采用多模块Maven结构，每个模块都是一个独立的Spring Boot示例应用。

```
spring-boot-3.x-samples/
├── .gitignore             # Git忽略文件配置
├── README.md              # 项目说明文档
├── TODO.md                # 待办事项清单
├── pom.xml                # 父POM文件，定义公共依赖和版本管理
├── data-jpa/              # Spring Boot 3.x集成JPA和MySQL示例
├── dingtalk-message-demo/ # 钉钉消息推送示例
├── elasticsearch-demo/    # Spring Boot 3.x集成Elasticsearch示例
├── h2-demo/               # Spring Boot 3.x集成H2内存数据库示例
├── hello-world/           # Hello World基础示例
├── kafka-demo/            # Spring Boot 3.x集成Kafka消息队列示例
├── minio-demo/            # Spring Boot 3.x集成MinIO对象存储示例
├── mybatis-plus-demo/     # Spring Boot 3.x集成MyBatis-Plus示例
├── nacos-demo/            # Nacos服务发现与配置管理示例
├── rabbitmq-demo/         # RabbitMQ消息队列集成示例
├── redis-pubsub-demo/     # Redis发布订阅模式示例
├── spring-ai-demo/        # Spring AI人工智能集成示例
├── spring-cloud-gateway-demo/ # Spring Cloud Gateway API网关示例
└── wechat-message-demo/   # 微信消息推送示例
```

## 技术栈
- Java 17
- Spring Boot 3.2.0
- Maven

## 示例模块列表
| 序号 | 模块名称 | 说明 | 主要功能 |
|-----|---------|------|---------|
| 1 | [hello-world](hello-world) | Spring Boot 3.x基础入门示例 | 基础REST API，展示参数传递和对象处理 |
| 2 | [data-jpa](data-jpa) | Spring Boot 3.x集成JPA和MySQL示例 | 完整的用户管理CRUD功能，带前端界面 |
| 3 | [elasticsearch-demo](elasticsearch-demo) | Spring Boot 3.x集成Elasticsearch示例 | 全文搜索、数据索引和查询功能 |
| 4 | [h2-demo](h2-demo) | Spring Boot 3.x集成H2内存数据库示例 | 用户管理CRUD功能，无需额外数据库配置 |
| 5 | [kafka-demo](kafka-demo) | Spring Boot 3.x集成Kafka消息队列示例 | 高性能消息发布订阅、流处理功能 |
| 6 | [dingtalk-message-demo](dingtalk-message-demo) | 钉钉消息推送示例 | 实现钉钉机器人消息推送功能 |
| 7 | [minio-demo](minio-demo) | Spring Boot 3.x集成MinIO对象存储示例 | 文件上传、下载、删除等对象存储功能 |
| 8 | [mybatis-plus-demo](mybatis-plus-demo) | Spring Boot 3.x集成MyBatis-Plus示例 | 增强型ORM框架，简化数据库操作 |
| 9 | [nacos-demo](nacos-demo) | Nacos服务发现与配置管理示例 | 微服务注册发现和配置管理 |
| 10 | [rabbitmq-demo](rabbitmq-demo) | RabbitMQ消息队列集成示例 | 消息发布、订阅、路由等功能 |
| 11 | [redis-pubsub-demo](redis-pubsub-demo) | Redis发布订阅模式示例 | 基于Redis的消息发布订阅实现 |
| 12 | [spring-ai-demo](spring-ai-demo) | Spring AI人工智能集成示例 | 集成AI能力到Spring Boot应用 |
| 13 | [spring-cloud-gateway-demo](spring-cloud-gateway-demo) | Spring Cloud Gateway API网关示例 | API路由、过滤、断言、负载均衡等功能 |
| 14 | [wechat-message-demo](wechat-message-demo) | 微信消息推送示例 | 微信公众号消息推送功能 |

## 如何使用

### 前提条件
- JDK 17或更高版本
- Maven 3.8或更高版本
- IDE（推荐IntelliJ IDEA、Eclipse或Spring Tool Suite）

### 构建整个项目
```bash
# 进入项目根目录
cd spring-boot-3.x-samples

# 构建所有模块
mvn clean install
```

### 运行单个示例模块
每个示例模块都是一个独立的Spring Boot应用，可以单独运行：

```bash
# 进入特定模块目录
cd spring-boot-3.x-samples/hello-world

# 运行应用
mvn spring-boot:run
```

## Spring Boot 3.x 主要特性
- **JDK 17+ 支持**：要求Java 17或更高版本
- **Jakarta EE 9/10**：基于Jakarta EE标准，包名从javax.*变更为jakarta.*
- **性能改进**：启动时间和内存使用都有优化
- **GraalVM原生镜像支持**：可以编译为原生镜像以获得更好的性能
- **SpringDoc OpenAPI**：替代Spring Boot 2.x中的SpringFox Swagger
- **Java模块系统支持**：更好地支持Java 9+的模块系统
- **新的deprecation策略**：更清晰的API生命周期管理

## 与Spring Boot 2.x的主要区别
1. **Java版本要求**：Spring Boot 3.x要求JDK 17+，而Spring Boot 2.x支持JDK 8+（部分版本支持到JDK 11+）
2. **Jakarta EE迁移**：从javax.*包迁移到jakarta.*包
3. **依赖升级**：升级了各种依赖库的版本，包括Spring Framework 6.0+，Hibernate 6.0+等
4. **移除了一些旧API**：删除了Spring Boot 2.x中已弃用的API
5. **文档系统变更**：从SpringFox Swagger变更为SpringDoc OpenAPI
6. **GraalVM原生镜像支持**：更完善的GraalVM原生镜像支持
7. **改进的条件装配**：更精确的条件装配机制
8. **支持Jakarta Persistence 3.0**：使用最新的JPA规范
9. **移除了对废弃的配置属性的支持**：清理了过时的配置选项

## 贡献指南
如果你对本项目有任何建议或想要贡献新的示例模块，欢迎提交Pull Request或Issue。

## License
本项目采用MIT许可证 - 详见LICENSE文件