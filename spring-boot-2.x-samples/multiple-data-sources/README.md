# Spring Boot 多数据源配置

## 📋 模块介绍

本模块演示了在 Spring Boot 项目中配置和使用多个数据源的完整实现，包括基础多数据源配置和与 MyBatis-Plus 框架的集成。

## ✨ 功能特点

- ✅ 配置多个独立数据源
- ✅ 支持 MyBatis-Plus 框架
- ✅ 提供单元测试验证
- ✅ 通过 HTTP 接口演示多数据源查询

## 🛠 技术栈

- Spring Boot 2.x
- MyBatis-Plus 3.5.2
- MySQL 数据库
- HikariCP 连接池（Spring Boot 默认）

## 🔧 配置说明

### 1. 数据源配置

在 `application.yml` 中配置两个不同的数据源：

```yaml
spring:
  datasource:
    local:
      driver-class-name: com.mysql.cj.jdbc.Driver
      jdbc-url: jdbc:mysql://your-host:port/db1?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
      username: your-username
      password: your-password
    origin:
      driver-class-name: com.mysql.cj.jdbc.Driver
      jdbc-url: jdbc:mysql://your-host:port/db2?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
      username: your-username
      password: your-password
```

> **注意**：使用 Spring Boot 默认的 HikariCP 连接池时，需要使用 `jdbc-url` 属性而不是 `url` 属性。

### 2. MyBatis-Plus 配置

```yaml
mybatis-plus:
  # 搜索指定包别名
  typeAliasesPackage: com.github.zhuo.**.domain, com.github.zhuo.**.model
  # 配置mapper的扫描，找到所有的mapper.xml映射文件
  mapperLocations: classpath*:mapper/**/*Mapper.xml
```

## 🚀 快速开始

### 准备工作

1. 准备两个不同的 MySQL 数据库实例
2. 修改 `application.yml` 中的数据库连接配置
3. 准备相应的数据表和测试数据

### 方式一：通过单元测试验证

1. 修改 mapper 中的 SQL 语句，使其适配您的数据库表结构
2. 运行单元测试类查看多数据源查询结果

### 方式二：通过 HTTP 接口验证

1. 启动 Spring Boot 应用程序
2. 访问 `http://localhost:8080/query` 查看多数据源查询结果

## 📁 项目结构

```
src/main/
├── java/com/github/zhuyizhuo/  # Java 源码目录（实际路径可能需要调整）
└── resources/                  # 资源目录
    ├── application.yml         # 应用配置文件
    ├── mapper/                 # 第一个数据源的 Mapper XML 文件
    │   └── QueryMapper.xml
    └── mapper2/                # 第二个数据源的 Mapper XML 文件
        └── QueryMapper.xml
```

## 🔍 实现原理

1. **数据源配置**：通过 Spring Boot 的配置机制，创建多个数据源实例
2. **SqlSessionFactory 配置**：为每个数据源创建独立的 SqlSessionFactory
3. **事务管理**：为每个数据源配置独立的事务管理器
4. **包路径扫描**：通过不同的包路径区分使用不同数据源的 Mapper 接口

## 📚 参考资料

- [Spring Boot 官方文档 - 数据源配置](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-sql)
- [MyBatis-Plus 官方文档](https://baomidou.com/guide/)