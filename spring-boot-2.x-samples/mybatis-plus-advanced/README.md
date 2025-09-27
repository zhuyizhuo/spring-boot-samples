# Spring Boot 集成 MyBatis-Plus 高级示例

## 📋 模块介绍

本模块是对基础 MyBatis-Plus 集成的进阶示例，展示了更多高级特性的使用方法，包括：
- 自定义SQL查询
- 条件构造器的高级用法
- 分页插件的配置与使用
- 逻辑删除功能
- 代码生成器的使用
- 自定义全局操作
- 自动填充功能

## ✨ 功能特点

- ✅ 基于 MyBatis-Plus 的自动 CRUD 操作
- ✅ 自定义 SQL 语句和复杂查询
- ✅ 高级条件构造器查询
- ✅ 分页查询功能与自定义分页对象
- ✅ 逻辑删除功能实现
- ✅ 代码生成器配置与使用
- ✅ 自定义全局拦截器
- ✅ 完整的 RESTful API 示例
- ✅ 完整的单元测试用例
- ✅ 自动填充创建时间和更新时间

## 🛠 技术栈

- Spring Boot 2.x
- MyBatis-Plus 3.5.2
- MySQL 8.0.31 (使用 com.mysql:mysql-connector-j 驱动)
- Spring Boot Starter Web
- MyBatis-Plus Generator
- Freemarker

## 📦 依赖配置

请参考 `pom.xml` 文件中的依赖配置。MySQL 驱动已更新为推荐的新坐标：`com.mysql:mysql-connector-j`。

## 🔧 配置说明

在 `application.yml` 文件中配置数据库连接信息和 MyBatis-Plus 相关配置：

```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:mysql://81.70.18.89:3306/springboot?useSSL=false&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: management
    password: Management3.2

# MyBatis-Plus 配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  mapper-locations: classpath:mapper/**/*.xml
```

## 🗄️ 数据库结构

项目使用了增强的 `user` 表结构，增加了逻辑删除字段：

```sql
-- 用户表结构
DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    id          BIGINT      NOT NULL COMMENT '主键ID'
        PRIMARY KEY,
    name        VARCHAR(30) NULL COMMENT '姓名',
    age         INT         NULL COMMENT '年龄',
    email       VARCHAR(50) NULL COMMENT '邮箱',
    create_time DATETIME    NULL COMMENT '创建时间',
    update_time DATETIME    NULL COMMENT '更新时间',
    deleted     TINYINT(1)  NULL DEFAULT 0 COMMENT '逻辑删除标记'
);

-- 测试数据
INSERT INTO user (id, name, age, email, create_time, update_time) 
VALUES (1, 'Jone', 18, 'test1@baomidou.com', NOW(), NOW());
INSERT INTO user (id, name, age, email, create_time, update_time) 
VALUES (2, 'Jack', 20, 'test2@baomidou.com', NOW(), NOW());
INSERT INTO user (id, name, age, email, create_time, update_time) 
VALUES (3, 'Tom', 28, 'test3@baomidou.com', NOW(), NOW());
INSERT INTO user (id, name, age, email, create_time, update_time) 
VALUES (4, 'Sandy', 21, 'test4@baomidou.com', NOW(), NOW());
INSERT INTO user (id, name, age, email, create_time, update_time) 
VALUES (5, 'Billie', 24, 'test5@baomidou.com', NOW(), NOW());
```

## 🚀 快速开始

### 环境要求

- JDK 1.8+ (项目已测试在 1.8.0_172 版本下工作正常)
- Maven 3.5+ (项目已测试在 3.5.4 版本下工作正常)
- MySQL 5.7+

### 运行步骤

1. 确保 MySQL 数据库可访问
2. 执行上面的 SQL 脚本创建表和插入测试数据
3. 根据需要修改 `application.yml` 中的数据库连接信息
4. 运行 `MybatisPlusAdvancedApplication` 类的 `main` 方法

### 测试方式

项目包含多种测试方式：

1. **单元测试**：
   ```bash
   # 运行所有测试
   mvn test
   
   # 运行特定测试
   mvn test -Dtest=SimpleSpringBootTest
   mvn test -Dtest=DatabaseConnectionTest
   ```

2. **直接运行应用**：
   ```bash
   mvn spring-boot:run
   ```

3. **JDBC连接测试**：
   ```bash
   # 编译项目
   mvn compile test-compile
   
   # 运行JDBC测试类（不依赖Spring）
   # 注意：使用提供的批处理文件运行以避免类路径问题
   ```

## 🔍 主要功能演示

### 1. 基础 CRUD 操作

MyBatis-Plus 提供了丰富的 CRUD 方法，无需编写 SQL 语句即可完成常见操作。

### 2. 条件构造器的高级用法

示例代码展示了如何使用 Wrapper 接口进行复杂条件查询：

```java
// 条件构造器示例
LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper
    .like(User::getName, "J")
    .ge(User::getAge, 18)
    .orderByDesc(User::getCreateTime);
List<User> userList = userMapper.selectList(queryWrapper);
```

### 3. 分页查询

MyBatis-Plus 提供了强大的分页功能：

```java
// 分页查询示例
Page<User> page = new Page<>(1, 10);
LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.gt(User::getAge, 20);
Page<User> userPage = userMapper.selectPage(page, queryWrapper);
```

### 4. 逻辑删除

通过简单配置即可实现逻辑删除功能，保持数据完整性：

```java
// 逻辑删除示例
userMapper.deleteById(1L); // 实际上执行的是update操作，设置deleted字段为1
```

### 5. 代码生成器

测试代码中包含了代码生成器的配置和使用方法，可以快速生成实体类、Mapper、Service等代码。

## 📁 项目结构

```
src/main/java/com/github/zhuyizhuo/sample/mybatis/plus/advanced/
├── MybatisPlusAdvancedApplication.java # 应用程序入口
├── config/                             # 配置类
│   └── MyBatisPlusConfig.java          # MyBatis-Plus配置（分页插件等）
├── entity/                             # 实体类
│   └── User.java                       # 用户实体类
├── mapper/                             # Mapper接口
│   └── UserMapper.java                 # 用户Mapper
├── service/                            # Service层
│   ├── UserService.java                # 用户Service接口
│   └── impl/                           # Service实现
│       └── UserServiceImpl.java        # 用户Service实现类
├── controller/                         # Controller层
│   └── UserController.java             # 用户控制器
├── handler/                            # 处理器
│   └── MyMetaObjectHandler.java        # 自定义元对象处理器（自动填充）
└── test/                               # 测试类
    ├── AppTestMain.java                # 简单的应用启动测试
    └── SimpleJdbcTest.java             # 简单的JDBC连接测试

# 测试目录结构
src/test/java/com/github/zhuyizhuo/sample/mybatis/plus/advanced/test/
├── CodeGeneratorTest.java              # 代码生成器测试
├── DatabaseConnectionTest.java         # 数据库连接测试
├── DirectJdbcConnectionTest.java       # 直接JDBC连接测试
├── SampleTest.java                     # 示例测试
└── SimpleSpringBootTest.java           # Spring Boot容器测试
```

## 📝 注意事项

1. 确保在 `application.yml` 中正确配置数据库连接信息
2. 如需使用代码生成器，请根据实际情况修改测试类中的配置参数
3. 本示例中的端口号为8082，避免与其他示例冲突
4. 项目使用的MySQL驱动已更新为新的推荐坐标 `com.mysql:mysql-connector-j`
5. 测试类中可能存在依赖注入提示信息，但不影响测试运行
6. 如需直接运行JDBC测试类，请使用提供的批处理文件以避免类路径问题
7. 默认配置使用远程MySQL服务器，如需使用本地数据库，请修改连接信息

## 🔍 已知问题

1. 某些测试类运行时可能会显示依赖注入警告信息，但测试仍会通过
2. 直接运行应用程序可能需要较长时间下载依赖
3. 在PowerShell环境中运行Java类时可能会遇到类路径问题，建议使用批处理文件或IDE运行

## 💡 常见问题解决

### 数据库连接问题

如果遇到数据库连接问题，请检查：
- 数据库服务器是否可访问
- 用户名和密码是否正确
- 防火墙设置是否允许连接
- 数据库是否已创建

### 依赖注入警告

测试中出现的依赖注入警告通常不影响功能，主要是测试类设计的原因。如果需要修复，可以调整测试类的配置方式。

### 运行环境问题

确保使用兼容的JDK和Maven版本。项目已在JDK 1.8.0_172和Maven 3.5.4环境下验证通过。

## 📚 参考文档

- [MyBatis-Plus 官方文档](https://baomidou.com/guide/)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)