# Data JPA 示例

这个模块展示了如何在Spring Boot 3.x中集成JPA (Java Persistence API) 和MySQL数据库，实现基本的CRUD操作。

## 技术栈
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.3.0
- springdoc-openapi (Swagger 3)
- Lombok

## 功能说明
本示例实现了基本的用户管理功能，包括：
- 获取所有用户列表
- 根据ID获取用户详情
- 创建新用户
- 更新用户信息
- 删除用户

## 数据库配置
在运行应用前，请确保你已经安装了MySQL数据库，并创建了相应的数据库：

```sql
CREATE DATABASE spring_boot_3_sample CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后修改 `src/main/resources/application.properties` 文件中的数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spring_boot_3_sample?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## 运行方法

### 使用Maven命令运行
```bash
# 在模块根目录下
mvn spring-boot:run

# 或者在项目根目录下
mvn -pl data-jpa spring-boot:run
```

### 打包运行
```bash
# 打包
mvn clean package

# 运行jar包
java -jar target/data-jpa-1.0-SNAPSHOT.jar
```

## API访问方式
应用启动后，可以通过以下URL访问API：

### 用户管理API
- GET `http://localhost:8081/data-jpa/api/users` - 获取所有用户
- GET `http://localhost:8081/data-jpa/api/users/{id}` - 根据ID获取用户
- POST `http://localhost:8081/data-jpa/api/users` - 创建新用户
  - 请求体示例：
    ```json
    {
      "username": "johndoe",
      "password": "password123",
      "email": "john.doe@example.com",
      "fullName": "John Doe"
    }
    ```
- PUT `http://localhost:8081/data-jpa/api/users/{id}` - 更新用户信息
  - 请求体示例同创建用户
- DELETE `http://localhost:8081/data-jpa/api/users/{id}` - 删除用户

## API文档查看方式
本项目集成了Swagger 3 (springdoc-openapi)，可以通过以下URL访问API文档：

http://localhost:8081/data-jpa/swagger-ui.html

## Spring Boot 3.x 与 JPA 的主要变化
1. 使用Jakarta EE替代了JEE (javax.persistence -> jakarta.persistence)
2. 支持Java 17及以上版本
3. JPA接口和注解有小幅更新
4. Hibernate版本升级到6.x系列，提供了更多性能优化

## 测试
该项目包含单元测试和集成测试，使用H2内存数据库进行测试，不影响实际的MySQL数据库。

```bash
# 运行测试
mvn test
```