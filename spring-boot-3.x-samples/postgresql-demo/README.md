# Spring Boot PostgreSQL Demo

这是一个面向零基础学习者的Spring Boot 3.x集成PostgreSQL数据库的示例项目。本项目演示了如何使用Spring Boot连接PostgreSQL数据库，实现基本的CRUD操作，并提供一个简单的Web界面进行用户管理。

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
- [使用Web界面](#使用web界面)
- [常见问题解答](#常见问题解答)
- [下一步学习](#下一步学习)

## 什么是PostgreSQL

PostgreSQL是一个功能强大的开源对象关系型数据库系统，它具有高度的可靠性、稳定性和良好的性能。PostgreSQL支持复杂查询、外键、事务、用户定义的类型和函数等特性，是许多企业级应用的理想选择。

与MySQL相比，PostgreSQL在复杂查询、并发处理、数据完整性和扩展性方面有一定优势，特别适合需要高级数据类型和复杂业务逻辑的应用。

## 技术栈

- **Spring Boot 3.2.0**：应用开发框架
- **Spring Data JPA**：ORM框架，简化数据库操作
- **PostgreSQL**：关系型数据库
- **Java 17**：编程语言
- **Lombok**：简化Java代码的工具库
- **Thymeleaf/Bootstrap**：Web界面（通过静态HTML实现）

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
- **主键(Primary Key)**：唯一标识表中每条记录的列或列组合
- **外键(Foreign Key)**：建立表之间关联的列

## 环境准备

### 安装JDK
1. 下载并安装JDK 17或更高版本
2. 配置环境变量 `JAVA_HOME`
3. 验证安装：`java -version`

### 安装Maven
1. 下载并安装Maven 3.6或更高版本
2. 配置环境变量 `MAVEN_HOME`
3. 验证安装：`mvn -version`

### 安装PostgreSQL

#### Windows安装
1. 访问 [PostgreSQL官网](https://www.postgresql.org/download/windows/)
2. 下载并运行安装程序
3. 按照向导完成安装（记住设置的密码）
4. 安装完成后，PostgreSQL服务会自动启动

#### macOS安装
1. 使用Homebrew安装：`brew install postgresql`
2. 启动PostgreSQL服务：`brew services start postgresql`

#### Linux安装（Ubuntu/Debian）
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### 连接PostgreSQL
安装完成后，可以使用以下方式连接PostgreSQL：

1. **命令行工具**：
   ```bash
   # Windows
   psql -U postgres
   
   # macOS/Linux
   sudo -u postgres psql
   ```

2. **图形界面工具**：推荐使用 pgAdmin、DBeaver 或 DataGrip

## 数据库配置

### 1. 创建数据库和用户
使用psql或pgAdmin连接到PostgreSQL后，执行以下SQL命令：

```sql
-- 创建数据库
CREATE DATABASE springboot_db;

-- 创建用户（如果需要）
CREATE USER pgdemo WITH PASSWORD '451xzcz.asf';

-- 授予用户对数据库的所有权限
GRANT ALL PRIVILEGES ON DATABASE springboot_db TO pgdemo;
```

### 2. 配置连接参数

为了安全起见，项目的敏感数据库配置已移至单独的配置文件。我们提供了示例配置文件，您可以按照以下步骤配置数据库连接：

1. 在 `src/main/resources/` 目录下找到 `application-pgsql.properties.demo` 文件
2. 复制该文件并命名为 `application-pgsql.properties`
3. 根据您的实际环境修改以下内容：

```properties
# 数据库连接配置
spring.datasource.url=jdbc:postgresql://localhost:5432/springboot_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**重要注意事项**：
- 请确保 `application-pgsql.properties` 文件已添加到 `.gitignore` 文件中，不要将包含敏感信息的配置文件提交到版本控制系统
- 生产环境中，建议使用环境变量或配置中心来管理敏感配置信息

主配置文件 `application.properties` 中已配置了引用该敏感配置文件的方式：

```properties
# PostgreSQL配置
# 敏感信息已移至application-pgsql.properties文件
# 请确保此文件不被提交到版本控制系统
spring.config.import=optional:classpath:application-pgsql.properties
spring.datasource.driver-class-name=org.postgresql.Driver
```

## 项目结构解析

```
src/main/java/com/example/postgresqldemo/
├── PostgreSqlDemoApplication.java    # 应用主类
├── config/                           # 配置类目录
├── entity/                           # 实体类目录
│   └── User.java                     # 用户实体类
├── repository/                       # 数据访问层目录
│   └── UserRepository.java           # 用户数据访问接口
├── service/                          # 业务逻辑层目录
│   ├── UserService.java              # 用户服务接口
│   └── impl/                         # 服务实现目录
│       └── UserServiceImpl.java      # 用户服务实现
└── controller/                       # 控制器层目录
    └── UserController.java           # 用户控制器

src/main/resources/
├── application.properties            # 应用配置文件
└── static/
    └── index.html                    # Web界面
```

## 代码详解

### 实体类 (User.java)

```java
@Entity
@Table(name = "users")
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String name;

    @Column
    private String phone;
}
```

**关键注解说明**：
- `@Entity`：标记这是一个JPA实体类
- `@Table(name = "users")`：指定对应的数据库表名
- `@Id`：标记主键字段
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`：指定主键生成策略为自动增长
- `@Column`：配置字段属性，如是否可为空、是否唯一等
- `@Data`：Lombok注解，自动生成getter、setter等方法

### 数据访问层 (UserRepository.java)

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 根据用户名查询用户
    Optional<User> findByUsername(String username);

    // 根据邮箱查询用户
    Optional<User> findByEmail(String email);

    // 检查用户名是否存在
    boolean existsByUsername(String username);

    // 检查邮箱是否存在
    boolean existsByEmail(String email);
}
```

**说明**：
- 继承 `JpaRepository<User, Long>` 可以获得基本的CRUD操作方法
- Spring Data JPA会根据方法名自动生成SQL查询
- `Optional<User>` 表示返回结果可能为空

### 业务逻辑层 (UserServiceImpl.java)

```java
@Service
public class UserServiceImpl implements UserService {
    // ...
    
    @Override
    @Transactional
    public User createUser(User user) {
        // 检查用户名和邮箱是否已存在
        if (existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在: " + user.getUsername());
        }
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已存在: " + user.getEmail());
        }
        return userRepository.save(user);
    }
    // ...
}
```

**说明**：
- `@Service`：标记这是一个服务层组件
- `@Transactional`：标记事务边界，确保数据操作的原子性
- 实现了业务逻辑，如重复用户名和邮箱的检查

### 控制器层 (UserController.java)

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    // ...
    
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    // ...
}
```

**说明**：
- `@RestController`：标记这是一个REST控制器
- `@RequestMapping("/api/users")`：指定基础URL路径
- `@PostMapping`：处理HTTP POST请求
- `@RequestBody`：将请求体转换为Java对象
- `ResponseEntity`：封装HTTP响应

## API接口说明

### 1. 创建用户

**POST /api/users**

请求体：
```json
{
  "username": "zhangsan",
  "password": "123456",
  "email": "zhangsan@example.com",
  "name": "张三",
  "phone": "13800138000"
}
```

响应：
```json
{
  "id": 1,
  "username": "zhangsan",
  "password": "123456",
  "email": "zhangsan@example.com",
  "name": "张三",
  "phone": "13800138000"
}
```

### 2. 获取所有用户

**GET /api/users**

响应：
```json
[
  {
    "id": 1,
    "username": "zhangsan",
    "password": "123456",
    "email": "zhangsan@example.com",
    "name": "张三",
    "phone": "13800138000"
  }
]
```

### 3. 根据ID获取用户

**GET /api/users/{id}**

响应：
```json
{
  "id": 1,
  "username": "zhangsan",
  "password": "123456",
  "email": "zhangsan@example.com",
  "name": "张三",
  "phone": "13800138000"
}
```

### 4. 更新用户

**PUT /api/users/{id}**

请求体：
```json
{
  "username": "zhangsan",
  "password": "newpassword",
  "email": "zhangsan@example.com",
  "name": "张三",
  "phone": "13900139000"
}
```

响应：
```json
{
  "id": 1,
  "username": "zhangsan",
  "password": "newpassword",
  "email": "zhangsan@example.com",
  "name": "张三",
  "phone": "13900139000"
}
```

### 5. 删除用户

**DELETE /api/users/{id}**

响应：
```json
{
  "message": "用户删除成功"
}
```

## 运行项目

### 方法一：使用Maven运行

```bash
cd spring-boot-samples/spring-boot-3.x-samples/postgresql-demo
mvn clean spring-boot:run
```

### 方法二：打包后运行

```bash
cd spring-boot-samples/spring-boot-3.x-samples/postgresql-demo
mvn clean package
java -jar target/postgresql-demo-0.0.1-SNAPSHOT.jar
```

## 使用Web界面

项目启动后，可以通过浏览器访问以下地址使用Web界面：

```
http://localhost:8080/
```

Web界面提供了以下功能：
- 新增用户
- 查看用户列表
- 编辑用户信息
- 删除用户

界面上会显示明确的状态标识（"新增"或"修改"），帮助您识别当前操作模式。

## 常见问题解答

### 1. 连接数据库失败

**问题**：启动项目时出现数据库连接错误。

**解决方案**：
- 确认PostgreSQL服务已启动
- 检查数据库连接配置是否正确
- 验证数据库用户名和密码是否正确
- 确保数据库已创建且用户有足够权限

### 2. 表不存在错误

**问题**：运行时出现表不存在的错误。

**解决方案**：
- 检查 `spring.jpa.hibernate.ddl-auto` 配置是否为 `update` 或 `create`
- 确保实体类使用了正确的 `@Entity` 和 `@Table` 注解

### 3. 用户名或邮箱重复错误

**问题**：创建或更新用户时提示用户名或邮箱已存在。

**解决方案**：
- 选择不同的用户名和邮箱
- 检查数据库中是否已存在相同的用户名或邮箱

### 4. 如何修改数据库连接信息

在 `src/main/resources/application-pgsql.properties` 文件中修改数据库连接配置：

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 5. application-pgsql.properties 文件不存在怎么办？

**问题**：启动项目时找不到 application-pgsql.properties 文件。

**解决方案**：
- 在 `src/main/resources/` 目录下找到 `application-pgsql.properties.demo` 示例文件
- 复制该文件并重命名为 `application-pgsql.properties`
- 根据您的实际环境修改数据库连接配置信息
- 确保该文件已添加到 `.gitignore` 文件中

## 下一步学习

1. **PostgreSQL进阶**：学习PostgreSQL的高级特性，如索引、视图、存储过程等
2. **Spring Data JPA深入**：学习更复杂的查询方法、JPQL、原生SQL等
3. **安全认证**：添加Spring Security实现用户认证和授权
4. **分页查询**：实现数据分页展示功能
5. **数据校验**：使用Bean Validation实现更完善的数据校验

## 扩展阅读

- [PostgreSQL官方文档](https://www.postgresql.org/docs/)
- [Spring Boot官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA官方文档](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

## 配置文件

`application.properties` 包含以下主要配置：

- PostgreSQL数据库连接信息
- JPA/Hibernate配置
- 服务器端口配置

## 注意事项

1. 本示例为演示目的，密码未加密存储。在生产环境中，请使用BCrypt等方式加密密码。
2. 实际项目中，建议添加更完善的异常处理和请求参数验证。
3. 考虑添加分页功能以处理大量数据。