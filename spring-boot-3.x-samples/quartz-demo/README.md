# Spring Boot Quartz 示例项目

这是一个 Spring Boot 集成 Quartz 的示例项目，提供了定时任务的创建、执行、暂停、恢复和删除等功能。

## 功能特性

- ✅ 定时任务的完整生命周期管理（创建、执行、暂停、恢复、删除）
- ✅ 可视化的任务管理界面
- ✅ 基于 JDBC 的任务持久化存储，支持表前缀配置（QRTZ_）
- ✅ 支持 Cron 表达式配置
- ✅ 默认初始化一个示例任务
- ✅ 任务执行状态监控

## 技术栈

- Spring Boot 3.2.0
- Quartz 调度框架
- Spring Data JPA
- H2 数据库
- Thymeleaf 模板引擎
- Bootstrap 5

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+

### 运行项目

1. 克隆项目

```bash
git clone https://github.com/your-username/spring-boot-samples.git
cd spring-boot-samples/spring-boot-3.x-samples/quartz-demo
```

2. 构建并运行

```bash
mvn clean spring-boot:run
```

3. 访问应用

- 首页：http://localhost:8083/
- 任务管理页面：http://localhost:8083/quartz/index
- H2 数据库控制台：http://localhost:8083/h2-console

### H2 数据库配置

- JDBC URL: `jdbc:h2:mem:quartzdb`
- 用户名: `sa`
- 密码: 空

## 项目结构

```
src/main/
├── java/com/example/quartzdemo/
│   ├── config/           # 配置类
│   │   └── QuartzConfig.java
│   ├── controller/       # 控制器
│   │   └── QuartzJobController.java
│   ├── dto/              # 数据传输对象
│   │   └── JobRequest.java
│   ├── job/              # 定时任务类
│   │   └── PrintTimeJob.java
│   ├── service/          # 服务接口和实现
│   │   ├── QuartzJobService.java
│   │   └── impl/QuartzJobServiceImpl.java
│   └── QuartzDemoApplication.java  # 应用入口
└── resources/
    ├── templates/        # Thymeleaf模板
    │   ├── index.html
    │   └── quartz/
    │       ├── index.html
    │       └── job-form.html
    └── application.properties  # 应用配置
```

## Quartz 配置说明

本项目使用 JDBC 存储模式，主要配置项如下：

```properties
# 使用JDBC存储模式
spring.quartz.job-store-type=jdbc
# 调度器实例名称
spring.quartz.properties.org.quartz.scheduler.instanceName=QuartzDemoScheduler
# 线程池大小
spring.quartz.properties.org.quartz.threadPool.threadCount=5
# 表前缀配置
spring.quartz.properties.org.quartz.jobStore.tablePrefix=QRTZ_
# 自动创建表
spring.quartz.jdbc.initialize-schema=always
```

### 内存模式与JDBC模式的区别

- **内存模式**：任务信息存储在内存中，应用重启后数据丢失，适用于开发测试环境
- **JDBC模式**：任务信息持久化到数据库中，应用重启后可恢复，支持集群部署，适用于生产环境

## 使用说明

### 创建定时任务

1. 访问任务创建页面：http://localhost:8083/quartz/job/form
2. 填写任务信息：
   - 任务名称：唯一标识任务的名称
   - 任务组：任务所属的组
   - 触发器名称：触发器的名称
   - 触发器组：触发器所属的组
   - 任务类名：选择要执行的任务类
   - Cron表达式：设置任务执行的时间规则
   - 任务描述：任务的简要描述
3. 点击「创建任务」按钮

### 管理定时任务

在任务管理页面（http://localhost:8083/quartz/index）可以：

- 查看所有定时任务的状态
- 立即执行任务
- 暂停任务
- 恢复任务
- 删除任务

### 自定义定时任务

要创建自定义定时任务，请按照以下步骤操作：

1. 创建一个新的任务类，继承 `QuartzJobBean` 或实现 `org.quartz.Job` 接口
2. 在 `executeInternal` 方法中实现任务逻辑
3. 将新任务类添加到 `job-form.html` 页面的选项列表中
4. 重启应用

## 示例任务

项目默认初始化了一个每5秒执行一次的打印时间任务：
- 任务名称：defaultPrintTimeJob
- 任务组：defaultJobGroup
- 执行内容：打印当前时间到日志中

## Cron 表达式说明

Cron 表达式是一个字符串，包含6或7个由空格分隔的字段，表示时间的各个组成部分。格式如下：

```
秒 分 时 日 月 周 [年]
```

常用示例：
- `0/5 * * * * ?` - 每5秒执行一次
- `0 * * * * ?` - 每分钟执行一次
- `0 0 * * * ?` - 每小时执行一次
- `0 0 12 * * ?` - 每天中午12点执行
- `0 0 0 * * ?` - 每天凌晨0点执行

## 注意事项

1. 本项目已配置为 JDBC 模式，使用 H2 数据库存储任务信息。生产环境建议使用持久化数据库如 MySQL、PostgreSQL 等。
2. 所有 Quartz 表都带有 QRTZ_ 前缀，方便与其他业务表进行区分管理。
3. 定时任务的执行日志会输出到控制台。
4. 任务执行过程中的异常会被 Quartz 框架捕获并记录。
5. 请确保 Cron 表达式格式正确，否则任务将无法正常调度。

## License

This project is licensed under the MIT License.