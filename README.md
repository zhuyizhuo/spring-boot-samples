# Spring Boot Samples

<div align="center">
    <img src="https://spring.io/img/spring-2.svg" width="120px" alt="Spring Boot Logo" />
    <p>👨‍💻 Spring Boot 实战教程与示例代码集合</p>
    <p>支持 Spring Boot 2.x 和 Spring Boot 3.x 双版本</p>
    <div>
        <a href="https://github.com/zhuyizhuo/spring-boot-samples/stargazers"><img src="https://img.shields.io/github/stars/zhuyizhuo/spring-boot-samples" alt="GitHub stars" /></a>
        <a href="https://github.com/zhuyizhuo/spring-boot-samples/network"><img src="https://img.shields.io/github/forks/zhuyizhuo/spring-boot-samples" alt="GitHub forks" /></a>
        <a href="https://github.com/zhuyizhuo/spring-boot-samples/blob/master/LICENSE"><img src="https://img.shields.io/github/license/zhuyizhuo/spring-boot-samples" alt="GitHub license" /></a>
    </div>
</div>

## 📚 项目简介

本项目是一个全面的 Spring Boot 教程与示例代码集合，包含 Spring Boot 2.x 和 Spring Boot 3.x 两个主要版本，涵盖各种常用组件、框架的集成与实战案例，帮助开发者快速上手并掌握 Spring Boot 生态系统。特别说明：Spring Boot 3.x 示例项目基本都提供了直观的页面操作界面，便于开发者学习和测试。

### 🔥 项目特点

- **全面覆盖**：涵盖 Spring Boot 核心功能及主流中间件集成
- **实例驱动**：每个功能点都配有完整可运行的代码示例
- **详细教程**：配套博客文章提供深入解析
- **易于扩展**：模块化设计，便于学习和定制
- **持续更新**：定期添加新的技术栈和实用功能

## 🛠 环境要求

### 通用要求
- Maven 3.5 及以上
- IntelliJ IDEA 或其他 Java IDE
- 部分示例项目需安装 Lombok 插件

### Spring Boot 2.x 环境要求
- JDK 1.8 及以上
- MySQL 5.7 及以上（部分示例需要）

### Spring Boot 3.x 环境要求
- JDK 17 及以上
- MySQL 8.0 及以上（部分示例需要）

## 🚀 快速开始

1. **克隆项目**
   ```bash
   git clone https://github.com/zhuyizhuo/spring-boot-samples.git
   cd spring-boot-samples
   ```

2. **构建项目**
   ```bash
   mvn clean install
   ```

3. **运行示例**
   - 选择感兴趣的模块，进入对应目录
   - 通过 IDE 运行 Spring Boot 应用主类
   - 或使用 Maven 命令运行：`mvn spring-boot:run`

## 📁 项目结构

```
spring-boot-samples/
├── .gitignore                 # Git忽略文件配置
├── LICENSE                    # 许可证文件
├── README.md                  # 项目说明文档
├── TODO.md                    # 待办事项清单
├── custom-spring-boot-starter/ # 自定义 Starter 示例
│   ├── spring-boot-starter-demo/  # 自定义Starter实现
│   └── spring-boot-starter-demo-client/ # Starter客户端示例
├── nginx.conf                 # Nginx配置示例
├── renovate.json              # 依赖更新配置
├── spring-boot-2.x-samples/   # Spring Boot 2.x 示例集合
│   ├── activiti/              # Activiti 工作流引擎集成
│   ├── actuator/              # Spring Boot 监控机制
│   ├── annotation/            # Spring Boot 注解使用
│   ├── async/                 # 异步处理示例
│   ├── elasticsearch/         # Elasticsearch搜索引擎集成
│   ├── event/                 # 事件驱动模型
│   ├── flyway/                # 数据库版本管理工具
│   ├── jackson/               # JSON 处理框架
│   ├── japidocs/              # API 文档生成工具
│   ├── kafka/                 # Kafka 消息队列集成
│   ├── ldap/                  # LDAP 目录服务集成
│   ├── liquibase/             # 数据库版本管理工具
│   ├── log4j2/                # 日志框架集成
│   ├── logback/               # 日志框架集成
│   ├── minio/                 # MinIO对象存储集成
│   ├── multiple-data-sources/ # 多数据源配置示例
│   ├── mybatis-plus/          # MyBatis-Plus集成示例
│   ├── mybatis-plus-advanced/ # MyBatis-Plus高级特性
│   ├── oss/                   # 阿里云OSS存储集成
│   ├── oss-qiniu/             # 七牛云OSS存储集成
│   ├── quartz/                # 定时任务调度
│   ├── rabbitmq/              # RabbitMQ消息队列集成
│   ├── rbac/                  # 权限管理实现
│   ├── redis-geohash/         # Redis 地理位置功能
│   ├── resources/             # 静态资源访问
│   ├── rocketmq/              # RocketMQ 消息队列集成
│   ├── spring-security/       # Spring Security 安全框架
│   ├── spring-state-machine/  # 状态机框架集成
│   ├── swagger/               # API 文档生成
│   ├── xxl-job/               # 分布式任务调度
├── spring-boot-3.x-samples/   # Spring Boot 3.x 示例集合
│   ├── data-jpa/              # Spring Boot 3.x集成JPA和MySQL示例
│   ├── dingtalk-message-demo/ # 钉钉消息推送示例
│   ├── ehcache-demo/          # Ehcache缓存集成示例
│   ├── elasticsearch-demo/    # Elasticsearch搜索引擎集成示例
│   ├── h2-demo/               # Spring Boot 3.x集成H2内存数据库示例
│   ├── hello-world/           # Hello World基础示例
│   ├── kafka-demo/            # Kafka消息队列集成示例
│   ├── mapstruct-demo/        # MapStruct对象映射示例
│   ├── memcache-demo/         # Memcached缓存集成示例
│   ├── minio-demo/            # MinIO对象存储集成示例
│   ├── mybatis-plus-demo/     # MyBatis-Plus集成示例
│   ├── nacos-demo/            # Nacos服务发现与配置管理示例
│   ├── postgresql-demo/       # PostgreSQL数据库集成示例
│   ├── rabbitmq-demo/         # RabbitMQ消息队列集成示例
│   ├── redis-demo/            # Redis缓存与数据结构示例
│   ├── redis-pubsub-demo/     # Redis发布订阅模式示例
│   ├── rocketmq-demo/         # RocketMQ消息队列集成示例
│   ├── spring-ai-demo/        # Spring AI人工智能集成示例
│   └── wechat-message-demo/   # 微信消息推送示例
└── spring-boot-extension/     # Spring Boot 扩展功能
```

## 📖 教程与示例模块

## 🔍 版本选择指南

### Spring Boot 3.x 示例 (推荐用于新项目)
Spring Boot 3.x 是最新的稳定版本，基于 Java 17+，提供了更好的性能和更多新功能。

| 模块名称 | 主要功能 | 链接 |
|---------|---------|------|
| [hello-world](spring-boot-3.x-samples/hello-world) | Spring Boot 3.x基础入门示例，包含基础API、多参数测试、对象参数测试等功能，并提供Web测试页面和SpringDoc OpenAPI文档 | [查看详情](./spring-boot-3.x-samples/hello-world/README.md) |
| [data-jpa](spring-boot-3.x-samples/data-jpa) | Spring Boot 3.x集成JPA和MySQL示例，实现完整的用户管理CRUD功能，提供响应式前端管理界面、Swagger 3文档和测试功能 | [查看详情](./spring-boot-3.x-samples/data-jpa/README.md) |
| [h2-demo](spring-boot-3.x-samples/h2-demo) | Spring Boot 3.x集成H2内存数据库示例，实现用户管理CRUD操作，提供RESTful API、Swagger文档、H2控制台和前端测试页面 | [查看详情](./spring-boot-3.x-samples/h2-demo/README.md) |
| [dingtalk-message-demo](spring-boot-3.x-samples/dingtalk-message-demo) | 钉钉消息推送示例，支持发送文本消息（带@功能）、Markdown格式消息和工作通知消息，提供REST API、Swagger文档和Web前端测试页面 | [查看详情](./spring-boot-3.x-samples/dingtalk-message-demo/README.md) |
| [nacos-demo](spring-boot-3.x-samples/nacos-demo) | Nacos服务发现与配置管理示例，支持Nacos配置中心集成、配置动态刷新、服务注册与发现、交互式HTML测试页面和API文档自动生成 | [查看详情](./spring-boot-3.x-samples/nacos-demo/README.md) |
| [rabbitmq-demo](spring-boot-3.x-samples/rabbitmq-demo) | RabbitMQ消息队列集成示例，支持多种交换机类型（Direct、Topic、Fanout、Headers）、消息确认机制、死信队列、延迟消息和批量消息处理 | [查看详情](./spring-boot-3.x-samples/rabbitmq-demo/README.md) |
| [redis-pubsub-demo](spring-boot-3.x-samples/redis-pubsub-demo) | Redis发布订阅模式示例，支持多种主题的消息发布与订阅，提供REST API接口、Swagger文档和HTML测试页面 | [查看详情](./spring-boot-3.x-samples/redis-pubsub-demo/README.md) |
| [spring-ai-demo](spring-boot-3.x-samples/spring-ai-demo) | Spring AI人工智能集成示例，支持OpenAI GPT模型和本地Ollama模型，提供文本生成API和向量数据库检索功能 | [查看详情](./spring-boot-3.x-samples/spring-ai-demo/README.md) |
| [wechat-message-demo](spring-boot-3.x-samples/wechat-message-demo) | 微信消息推送示例，使用微信公众号SDK实现文本消息和模板消息的发送功能 | [查看详情](./spring-boot-3.x-samples/wechat-message-demo/README.md) |
| [ehcache-demo](spring-boot-3.x-samples/ehcache-demo) | Ehcache缓存集成示例，提供缓存配置、使用方式和缓存策略示例 | [查看详情](./spring-boot-3.x-samples/ehcache-demo/README.md) |
| [elasticsearch-demo](spring-boot-3.x-samples/elasticsearch-demo) | Elasticsearch搜索引擎集成示例，实现文档索引、查询和聚合功能 | [查看详情](./spring-boot-3.x-samples/elasticsearch-demo/README.md) |
| [kafka-demo](spring-boot-3.x-samples/kafka-demo) | Kafka消息队列集成示例，支持消息生产、消费、序列化和分区策略 | [查看详情](./spring-boot-3.x-samples/kafka-demo/README.md) |
| [mapstruct-demo](spring-boot-3.x-samples/mapstruct-demo) | MapStruct对象映射示例，展示高效的对象间转换实现 | [查看详情](./spring-boot-3.x-samples/mapstruct-demo/README.md) |
| [memcache-demo](spring-boot-3.x-samples/memcache-demo) | Memcached缓存集成示例，提供缓存操作和配置最佳实践 | [查看详情](./spring-boot-3.x-samples/memcache-demo/README.md) |
| [minio-demo](spring-boot-3.x-samples/minio-demo) | MinIO对象存储集成示例，实现文件上传、下载和管理功能 | [查看详情](./spring-boot-3.x-samples/minio-demo/README.md) |
| [mybatis-plus-demo](spring-boot-3.x-samples/mybatis-plus-demo) | MyBatis-Plus集成示例，展示CRUD、分页、条件查询等功能 | [查看详情](./spring-boot-3.x-samples/mybatis-plus-demo/README.md) |
| [postgresql-demo](spring-boot-3.x-samples/postgresql-demo) | PostgreSQL数据库集成示例，实现数据访问和操作 | [查看详情](./spring-boot-3.x-samples/postgresql-demo/README.md) |
| [redis-demo](spring-boot-3.x-samples/redis-demo) | Redis缓存与数据结构示例，展示Redis的各种使用场景 | [查看详情](./spring-boot-3.x-samples/redis-demo/README.md) |
| [rocketmq-demo](spring-boot-3.x-samples/rocketmq-demo) | RocketMQ消息队列集成示例，支持消息发送、消费和事务消息 | [查看详情](./spring-boot-3.x-samples/rocketmq-demo/README.md) |

### Spring Boot 2.x 示例 (适用于维护现有项目)
Spring Boot 2.x 是成熟稳定的版本，适用于需要保持兼容性的现有项目。

#### 核心功能示例

| 模块名称 | 主要功能 | 链接 |
|---------|---------|------|
| resources | 静态资源访问 | [查看详情](./spring-boot-2.x-samples/resources/README.md) |
| async | 异步方法处理 | [查看详情](./spring-boot-2.x-samples/async/README.md) |
| event | 自定义事件驱动 | [查看详情](./spring-boot-2.x-samples/event/README.md) |
| annotation | 自定义注解实现 | [查看详情](./spring-boot-2.x-samples/annotation/README.md) |
| actuator | 应用监控与管理 | [查看详情](./spring-boot-2.x-samples/actuator/README.md) |

#### 数据访问与存储

| 模块名称 | 主要功能 | 链接 |
|---------|---------|------|
| flyway | 数据库版本管理 | [查看详情](./spring-boot-2.x-samples/flyway/README.md) |
| liquibase | 数据库版本管理 | [查看详情](./spring-boot-2.x-samples/liquibase/README.md) |
| redis-geohash | 地理位置服务 | [查看详情](./spring-boot-2.x-samples/redis-geohash/README.md) |
| mybatis-plus | MyBatis增强框架 | [查看详情](./spring-boot-2.x-samples/mybatis-plus/README.md) |
| minio | 对象存储服务 | [查看详情](./spring-boot-2.x-samples/minio/README.md) |
| oss | 阿里云对象存储 | [查看详情](./spring-boot-2.x-samples/oss/README.md) |
| oss-qiniu | 七牛云对象存储 | [查看详情](./spring-boot-2.x-samples/oss-qiniu/README.md) |

#### 日志系统

| 模块名称 | 主要功能 | 链接 |
|---------|---------|------|
| log4j2 | 日志统一ID与彩色输出 | [查看详情](./spring-boot-2.x-samples/log4j2/README.md) |
| logback | 日志配置与使用 | [查看详情](./spring-boot-2.x-samples/logback/README.md) |

#### 消息队列

| 模块名称 | 主要功能 | 链接 |
|---------|---------|------|
| kafka | Kafka 消息队列集成 | [查看详情](./spring-boot-2.x-samples/kafka/README.md) |
| rocketmq | RocketMQ 消息队列集成 | [查看详情](./spring-boot-2.x-samples/rocketmq/README.md) |
| rabbitmq | RabbitMQ消息队列集成 | [查看详情](./spring-boot-2.x-samples/rabbitmq/README.md) |

#### 安全框架

| 模块名称 | 主要功能 | 链接 |
|---------|---------|------|
| spring-security | 安全认证与授权 | [查看详情](./spring-boot-2.x-samples/spring-security/README.md) |
| rbac | 基于拦截器的权限管理 | [查看详情](./spring-boot-2.x-samples/rbac/README.md) |
| ldap | LDAP 目录服务认证 | [查看详情](./spring-boot-2.x-samples/ldap/README.md) |

#### 任务调度

| 模块名称 | 主要功能 | 链接 |
|---------|---------|------|
| xxl-job | 分布式任务调度平台 | [查看详情](./spring-boot-2.x-samples/xxl-job/README.md) |
| quartz | 定时任务框架 | [查看详情](./spring-boot-2.x-samples/quartz/README.md) |

#### API 文档与工具

| 模块名称 | 主要功能 | 链接 |
|---------|---------|------|
| swagger | 在线 API 文档生成 | [查看详情](./spring-boot-2.x-samples/swagger/README.md) |
| japidocs | 静态 API 文档生成 | [查看详情](./spring-boot-2.x-samples/japidocs/README.md) |
| jackson | JSON 序列化与反序列化 | [查看详情](./spring-boot-2.x-samples/jackson/README.md) |

#### 工作流与状态管理

| 模块名称 | 主要功能 | 链接 |
|---------|---------|------|
| activiti | 工作流引擎 | [查看详情](./spring-boot-2.x-samples/activiti/README.md) |
| spring-state-machine | 状态机框架 | [查看详情](./spring-boot-2.x-samples/spring-state-machine/README.md) |

## 📝 配套博客教程

- [Spring Boot 2.x教程：静态资源访问](https://blog.zhuyizhuo.online/2020/06/11/spring-boot/spring-boot-resources-visit/)
- [Spring Boot 2.x教程：文档生成工具 swagger](https://blog.zhuyizhuo.online/2020/06/17/spring-boot/spring-boot-swagger/)
- [Spring Boot 2.x教程：数据库版本管理工具 Flyway](https://blog.zhuyizhuo.online/2020/06/21/spring-boot/spring-boot-flyway-database-version-control/)
- [Spring Boot 2.x教程：数据库版本管理工具 Liquibase](https://blog.zhuyizhuo.online/2020/07/04/spring-boot/spring-boot-liquibase-database-version-control)
- [前后端通信有字段需要加解密你会如何处理？](https://blog.zhuyizhuo.online/2020/07/12/spring-boot/spring-boot-jackson/)
- [用静态文档生成工具 JApiDocs 生成接口文档](https://blog.zhuyizhuo.online/2020/07/16/tool/japidocs/)

## 🚧 计划开发的功能

查看 [TODO.md](./TODO.md) 文件了解即将添加的功能模块，包括：
- Spring Boot 集成 mybatis plus
- Web Service 集成
- FastDFS 文件存储
- Sharding JDBC 分库分表
- Redis Template 详细教程
- Spring Retry 重试机制
- SkyWalking 分布式追踪
- 以及更多...

## 💡 如何贡献

1. 在 [issue](https://github.com/zhuyizhuo/spring-boot-samples/issues/new) 中提出您的想法或需求
2. Fork 仓库并提交 Pull Request
3. 关注项目更新并分享给更多开发者

## 🤝 支持项目

- 给项目点个 ⭐️ Star 支持一下
- 关注作者获取最新更新
- 分享给更多需要的朋友

## 🔗 推荐资源

- [个人博客](https://zhuyizhuo.online/)：分享技术学习与实践经验
- [代码生成器](https://zhuyizhuo.github.io/code-generator-doc/)：轻量级可扩展的代码生成工具
- [GitHub 主页](https://github.com/zhuyizhuo)：更多开源项目

## 📄 许可证

本项目采用 [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) 开源许可证
