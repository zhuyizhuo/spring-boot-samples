# Spring Boot 集成 Quartz 定时调度

## 📋 模块介绍

本模块演示了在 Spring Boot 项目中集成 Quartz 定时调度框架的完整实现，包括任务的创建、调度、持久化和集群配置。

## ✨ 功能特点

- ✅ 基于数据库的任务持久化存储
- ✅ 支持 Quartz 集群模式
- ✅ 多种任务调度方式（简单调度、Cron 表达式调度等）
- ✅ 集成 Spring Boot 自动配置

## 🛠 技术栈

- Spring Boot 2.x
- Quartz 定时调度框架
- MySQL 数据库
- Druid 连接池

## 🔧 配置说明

### 1. 数据库配置

项目使用 MySQL 数据库存储 Quartz 任务信息。首先需要执行项目中的 SQL 脚本创建必要的表结构：

```bash
# 执行 SQL 脚本创建 Quartz 表结构
mysql -u username -p database_name < sql/quartz.sql
```

### 2. 应用配置

核心配置位于 `application.yml` 文件中：

```yaml
spring:
  quartz:
    job-store-type: jdbc # 持久化到数据库
    properties:
      org:
        quartz:
          datasource: # 数据库配置
            driver-class-name: com.mysql.cj.jdbc.Driver
            url: jdbc:mysql://your-mysql-host:port/database?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
            username: your-username
            password: your-password
          scheduler: # 调度器配置
            instancName: clusteredScheduler
            instanceId: AUTO
          jobStore: # 任务存储配置
            class: org.quartz.impl.jdbcjobstore.JobStoreTX
            driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
            tablePrefix: QRTZ_
            isClustered: true # 启用集群
            clusterCheckinInterval: 1000
            useProperties: false
          threadPool: # 线程池配置
            class: org.quartz.simpl.SimpleThreadPool
            threadCount: 20
            threadPriority: 5
```

## 🚀 快速开始

### 1. 准备工作

- 确保已安装 MySQL 数据库
- 执行 SQL 脚本创建 Quartz 所需表结构
- 修改 `application.yml` 中的数据库连接配置

### 2. 运行项目

```bash
# 方式一：通过 Maven 运行
mvn spring-boot:run

# 方式二：通过 IDE 运行主应用类
# 注意：目前主应用类尚未实现，需要创建
```

## 📝 任务类型示例

Quartz 支持多种类型的任务调度，包括：

### 1. 简单调度任务（Simple Trigger）
按照固定的时间间隔重复执行任务

### 2. Cron 表达式调度任务（Cron Trigger）
按照 Cron 表达式定义的时间规则执行任务

### 3. 日历调度（Calendar-based Trigger）
基于日历的复杂调度规则

## 📁 表结构说明

Quartz 使用一系列表来存储任务和调度信息，主要包括：

- **QRTZ_JOB_DETAILS**：存储任务详情
- **QRTZ_TRIGGERS**：存储触发器信息
- **QRTZ_CRON_TRIGGERS**：存储 Cron 表达式触发器
- **QRTZ_SIMPLE_TRIGGERS**：存储简单触发器
- **QRTZ_FIRED_TRIGGERS**：存储已触发的触发器
- **QRTZ_LOCKS**：存储锁信息，用于集群环境

## 🔍 常见问题

### 1. 任务无法持久化
- 确认 `job-store-type` 配置为 `jdbc`
- 检查数据库连接是否正确
- 确认 Quartz 表结构已正确创建

### 2. 集群模式下任务重复执行
- 确保所有节点使用相同的 `scheduler.instanceName`
- 确认 `isClustered` 配置为 `true`
- 检查数据库连接配置是否一致

## 📚 参考资料

- [Quartz 官方文档](https://www.quartz-scheduler.org/documentation/)
- [Spring Boot 官方文档 - Quartz](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-quartz)