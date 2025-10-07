# Spring Boot H2 Demo

一个基于Spring Boot 3.x集成H2内存数据库的示例项目，实现了用户管理的CRUD功能，并提供了RESTful API、Swagger文档和直观的测试页面。

## 功能特性

- ✅ 用户信息的增删改查(CRUD)操作
- ✅ RESTful API设计规范
- ✅ H2内存数据库集成，无需额外数据库配置
- ✅ Swagger/OpenAPI文档自动生成
- ✅ 直观的前端测试页面
- ✅ H2数据库控制台可视化管理
- ✅ 完整的项目结构和代码注释

## 技术栈

- **后端框架**: Spring Boot 3.2.5
- **数据库**: H2 Database (内存模式)
- **ORM**: Spring Data JPA
- **API文档**: SpringDoc OpenAPI 2.3.0
- **构建工具**: Maven
- **前端**: Tailwind CSS, Font Awesome, Fetch API
- **开发语言**: Java 17
- **工具类**: Lombok

## 环境要求

- JDK 17 或更高版本
- Maven 3.6 或更高版本
- 浏览器 (推荐Chrome/Firefox最新版)

## 快速开始

### 1. 克隆仓库

```bash
git clone https://github.com/zhuyizhuo/spring-boot-samples.git
cd spring-boot-samples/spring-boot-3.x-samples/h2-demo
```

### 2. 构建项目

```bash
mvn clean package
```

### 3. 运行应用

可以通过以下任一方式运行:

#### 方式一: Maven Spring Boot插件

```bash
mvn spring-boot:run
```

#### 方式二: Java命令运行jar包

```bash
java -jar target/h2-demo-1.0-SNAPSHOT.jar
```

#### 方式三: 直接运行主类

在IDE中直接运行 `H2DemoApplication.java` 类的main方法

### 4. 访问应用

应用启动后，可以通过以下地址访问:

- **测试页面**: [http://localhost:8080](http://localhost:8080)
- **API文档**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **H2数据库控制台**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

## H2数据库控制台配置

访问H2控制台时，使用以下配置:

- **JDBC URL**: `jdbc:h2:mem:testdb`
- **用户名**: `sa`
- **密码**: 空 (无需输入)

## API接口说明

| 接口地址 | 请求方法 | 功能描述 |
|---------|---------|---------|
| `/api/users` | GET | 获取所有用户列表 |
| `/api/users/{id}` | GET | 根据ID获取单个用户 |
| `/api/users` | POST | 创建新用户 |
| `/api/users/{id}` | PUT | 更新用户信息 |
| `/api/users/{id}` | DELETE | 删除用户 |

详细API文档可查看Swagger UI。

## 项目结构

```
h2-demo/
├── src/main/java/com/github/zhuyizhuo/h2/demo/
│   ├── H2DemoApplication.java       # 应用入口类
│   ├── model/                       # 数据模型
│   │   └── User.java                # 用户实体类
│   ├── repository/                  # 数据访问层
│   │   └── UserRepository.java      # 用户数据访问接口
│   ├── service/                     # 业务逻辑层
│   │   ├── UserService.java         # 用户服务接口
│   │   └── impl/UserServiceImpl.java # 用户服务实现
│   └── controller/                  # 控制器层
│       └── UserController.java      # 用户API控制器
├── src/main/resources/
│   ├── application.yml              # 应用配置文件
│   └── static/index.html            # 前端测试页面
└── pom.xml                          # Maven依赖配置
```

## 许可证

本项目基于MIT许可证开源 - 详见 [LICENSE](LICENSE) 文件