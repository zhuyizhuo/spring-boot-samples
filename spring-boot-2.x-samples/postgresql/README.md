# Spring Boot PostgreSQL Demo

这是一个面向零基础学习者的Spring Boot 2.x集成PostgreSQL数据库的示例项目。本项目演示了如何使用Spring Boot连接PostgreSQL数据库，实现基本的CRUD操作，并提供RESTful API接口进行用户管理。

## 目录

- [什么是PostgreSQL](#什么是postgresql)
- [技术栈](#技术栈)
- [PostgreSQL基础概念](#postgresql基础概念)
- [环境准备](#环境准备)
- [数据库配置](#数据库配置)
- [项目结构解析](#项目结构解析)
- [代码详解](#代码详解)
- [API接口说明](#api接口说明)
- [运行项目](#运行项目)
- [常见问题解答](#常见问题解答)

## 什么是PostgreSQL

PostgreSQL是一个功能强大的开源对象关系型数据库系统，它具有高度的可靠性、稳定性和良好的性能。PostgreSQL支持复杂查询、外键、事务、用户定义的类型和函数等特性，是许多企业级应用的理想选择。

与MySQL相比，PostgreSQL在复杂查询、并发处理、数据完整性和扩展性方面有一定优势，特别适合需要高级数据类型和复杂业务逻辑的应用。

## 技术栈

- **Spring Boot 2.7.17**：应用开发框架
- **Spring Data JPA**：ORM框架，简化数据库操作
- **PostgreSQL**：关系型数据库
- **Java 11**：编程语言
- **Lombok**：简化Java代码的工具库

## PostgreSQL基础概念

### 数据库与表
- **数据库(Database)**：存储相关数据的集合
- **表(Table)**：数据的基本存储单元，由行和列组成
- **行(Row)**：表中的一条记录
- **列(Column)**：表中的一个字段

### 常用数据类型
- **整数类型**：`INTEGER`, `BIGINT`, `SERIAL`（自增整数）
- **字符类型**：`VARCHAR(n)`（可变长度字符串）, `TEXT`（长文本）
- **布尔类型**：`BOOLEAN`
- **时间类型**：`DATE`, `TIME`, `TIMESTAMP`
- **其他类型**：`JSON`, `ARRAY`（PostgreSQL特有）

### 主键与外键
- **主键(Primary Key)**：唯一标识表中每条记录的字段或字段组合
- **外键(Foreign Key)**：建立表与表之间关联的字段

## 环境准备

### 安装PostgreSQL
1. 访问[PostgreSQL官网](https://www.postgresql.org/download/)下载并安装适合您操作系统的PostgreSQL版本
2. 安装过程中会设置超级用户`postgres`的密码，请记住此密码
3. 安装完成后，启动PostgreSQL服务

### 安装JDK
- 本项目需要Java 11或更高版本，请确保您已安装并配置好JDK

### 安装Maven
- 本项目使用Maven进行依赖管理和构建，请确保您已安装Maven

## 数据库配置

### 创建数据库
1. 使用PostgreSQL客户端工具（如pgAdmin或psql命令行）连接到PostgreSQL服务器
2. 创建一个名为`example_db`的数据库：
   ```sql
   CREATE DATABASE example_db;
   ```

### Schema配置
本项目使用`demo` schema而不是默认的`public` schema：
- 项目启动时会自动创建`demo` schema
- 所有数据库表和数据都会存储在`demo` schema中

### 配置数据库连接
1. 在项目中找到`src/main/resources/application-pgsql.properties.demo`文件
2. 复制该文件并重命名为`application-pgsql.properties`
3. 根据您的实际环境修改以下配置：
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/example_db
   spring.datasource.username=postgres
   spring.datasource.password=您的密码
   ```

> **注意**：`application-pgsql.properties`文件已添加到`.gitignore`中，不会被提交到版本控制系统，这有助于保护敏感信息。

## 项目结构解析

```
src/main/java/com/example/postgresql/
├── PostgreSqlApplication.java      # 应用主类，Spring Boot应用的入口点
├── config/                         # 配置类
├── controller/                     # 控制器层
│   └── UserController.java         # 用户管理的REST控制器
├── entity/                         # 实体类
│   └── User.java                   # 用户实体类
├── repository/                     # 数据访问层
│   └── UserRepository.java         # 用户数据访问接口
└── service/                        # 服务层
    ├── UserService.java            # 用户服务接口
    └── impl/                       # 服务实现
        └── UserServiceImpl.java    # 用户服务实现类

src/main/resources/
├── application.properties          # 主配置文件
├── application-pgsql.properties.demo # 数据库配置示例文件
├── init.sql                        # 数据库初始化SQL脚本（创建schema和表）
├── data.sql                        # 示例数据SQL脚本
└── static/                         # 静态资源目录
    └── index.html                  # 用户管理前端页面
```

## 代码详解

### 实体类（Entity）
`User`类是一个JPA实体，映射到数据库中`demo` schema的`users`表：
- 使用`@Entity`注解标识这是一个JPA实体
- 使用`@Table`注解指定映射的表名和schema
- 使用`@Data`注解（来自Lombok）自动生成getter、setter、toString等方法
- 使用`@Id`和`@GeneratedValue`注解指定主键和生成策略
- 使用`@Column`注解指定字段属性（如非空、唯一等）

### 数据访问层（Repository）
`UserRepository`接口继承自`JpaRepository`，提供了基本的CRUD操作和自定义查询方法：
- 继承`JpaRepository<User, Long>`意味着可以对User实体进行操作，主键类型为Long
- 定义了根据用户名和邮箱查询用户的方法
- 定义了检查用户名和邮箱是否存在的方法
- Spring Data JPA会自动为这些方法生成实现

### 服务层（Service）
`UserService`接口定义了业务逻辑方法，`UserServiceImpl`实现了这些方法：
- 使用`@Service`注解标识这是一个服务类
- 注入`UserRepository`用于数据库操作
- 实现了用户的创建、查询、更新和删除操作
- 在创建用户时检查用户名和邮箱是否已存在
- 使用`@Transactional`注解保证事务的一致性

### 控制器层（Controller）
`UserController`类提供了RESTful API接口：
- 使用`@RestController`注解标识这是一个REST控制器
- 使用`@RequestMapping`注解指定请求路径前缀
- 提供了创建、查询、更新和删除用户的API接口
- 使用`@RequestBody`注解接收JSON请求体
- 使用`@PathVariable`注解获取URL路径参数
- 返回适当的HTTP状态码和响应体

## API接口说明

### 创建用户
- **URL**: `/api/users`
- **方法**: `POST`
- **请求体**:
  ```json
  {
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "name": "测试用户",
    "phone": "13800138000"
  }
  ```
- **响应**:
  - 成功: `200 OK` + 创建的用户信息
  - 失败: `400 Bad Request` + 错误信息

### 获取所有用户
- **URL**: `/api/users`
- **方法**: `GET`
- **响应**: `200 OK` + 用户列表

### 根据ID获取用户
- **URL**: `/api/users/{id}`
- **方法**: `GET`
- **响应**:
  - 存在: `200 OK` + 用户信息
  - 不存在: `404 Not Found`

### 更新用户
- **URL**: `/api/users/{id}`
- **方法**: `PUT`
- **请求体**:
  ```json
  {
    "username": "updateduser",
    "password": "newpassword123",
    "email": "updated@example.com",
    "name": "更新后的用户",
    "phone": "13900139000"
  }
  ```
- **响应**:
  - 成功: `200 OK` + 更新后的用户信息
  - 失败: `400 Bad Request` + 错误信息

### 删除用户
- **URL**: `/api/users/{id}`
- **方法**: `DELETE`
- **响应**:
  - 成功: `200 OK` + 成功消息
  - 失败: `400 Bad Request` + 错误信息

## 运行项目

### 使用Maven运行
1. 打开命令行工具，进入项目根目录
2. 执行以下命令：
   ```bash
   mvn spring-boot:run
   ```

### 使用IDE运行
1. 使用IDE（如IntelliJ IDEA、Eclipse）导入Maven项目
2. 找到`PostgreSqlApplication`类
3. 右键点击并选择"Run"或"Debug"

### 验证项目运行

#### 前端界面
- 项目启动后，可以通过浏览器访问前端用户管理界面：`http://localhost:8080/`
- 界面提供了用户的增删改查功能，可以直接在浏览器中操作用户数据

#### API接口
- 可以通过API测试工具（如Postman）访问API接口
- 例如，访问`http://localhost:8080/api/users`获取所有用户信息

## 常见问题解答

### 连接数据库失败
- 检查PostgreSQL服务是否已启动
- 确认`application-pgsql.properties`中的连接信息是否正确
- 确保数据库`example_db`已创建
- 验证用户名和密码是否正确

### 找不到`application-pgsql.properties`文件
- 请确保已从`application-pgsql.properties.demo`复制并创建了`application-pgsql.properties`文件
- 检查文件是否位于`src/main/resources`目录下

### 端口被占用
- 可以在`application.properties`中修改`server.port`配置，使用其他端口

### 表自动创建失败
- 检查`spring.jpa.hibernate.ddl-auto`配置是否设置为`update`
- 确保PostgreSQL用户有足够的权限创建表和schema
- 检查`spring.jpa.properties.hibernate.default_schema`配置是否正确设置为`demo`

## 下一步学习

1. **添加分页功能**：使用Spring Data JPA的分页API实现数据分页
2. **添加数据校验**：使用JSR-380（Bean Validation）进行请求数据校验
3. **添加安全认证**：集成Spring Security实现用户认证和授权
4. **添加API文档**：集成Swagger生成API文档
5. **添加事务管理**：更细粒度地控制事务行为
6. **添加缓存**：集成Redis等缓存技术提高性能

通过学习这个示例项目，您应该能够掌握Spring Boot 2.x与PostgreSQL集成的基本方法，包括项目结构、配置、数据访问、业务逻辑和API接口开发。