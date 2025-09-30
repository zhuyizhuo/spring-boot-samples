# Spring Boot 集成 MyBatis-Plus

## 📋 模块介绍

本模块演示了在 Spring Boot 项目中集成 MyBatis-Plus 的完整实现，包括基本的 CRUD 操作、分页查询、条件构造器等 MyBatis-Plus 的核心功能。

## ✨ 功能特点

- ✅ 基于 MyBatis-Plus 的自动 CRUD 操作
- ✅ 分页查询功能
- ✅ 条件构造器使用
- ✅ 代码生成器支持
- ✅ 完整的单元测试用例

## 🛠 技术栈

- Spring Boot 2.x
- MyBatis-Plus 3.5.2
- MySQL 8.0.31

## 📦 依赖配置

核心依赖配置如下：

```xml
<dependencies>
    <!-- Spring Boot 核心依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    
    <!-- 测试依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- MyBatis-Plus 集成 -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.5.2</version>
    </dependency>
    
    <!-- MySQL 驱动 -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

## 🔧 配置说明

在 `application.yml` 文件中配置数据库连接信息：

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/springboot
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your-password-here
```

## 🗄️ 数据库结构

项目使用了简单的 `user` 表结构：

```sql
-- 用户表结构
drop table if exists user;
create table user
(
    id    bigint      not null comment '主键ID'
        primary key,
    name  varchar(30) null comment '姓名',
    age   int         null comment '年龄',
    email varchar(50) null comment '邮箱'
);

-- 测试数据
INSERT INTO user (id, name, age, email) VALUES (1, 'Jone', 18, 'test1@baomidou.com');
INSERT INTO user (id, name, age, email) VALUES (2, 'Jack', 20, 'test2@baomidou.com');
INSERT INTO user (id, name, age, email) VALUES (3, 'Tom', 28, 'test3@baomidou.com');
INSERT INTO user (id, name, age, email) VALUES (4, 'Sandy', 21, 'test4@baomidou.com');
INSERT INTO user (id, name, age, email) VALUES (5, 'Billie', 24, 'test5@baomidou.com');
```

## 🚀 快速开始

### 环境要求

- JDK 8+
- MySQL 5.7+ 或 8.0+

### 运行步骤

1. 修改 `application.yml` 中的数据库连接配置，确保与您的实际环境匹配
2. 在对应的数据库中执行 `sql/user.sql` 文件，创建表结构并插入测试数据
3. 执行 `SampleTest` 单元测试，查看运行结果

## 📝 MyBatis-Plus 核心功能示例

### 1. 实体类定义

```java
@Data
@TableName("user")
public class User {
    @TableId
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
```

### 2. Mapper 接口定义

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // MyBatis-Plus 会自动实现基本的 CRUD 方法
    // 可以在这里添加自定义的 SQL 方法
}
```

### 3. 服务层实现

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    // 可以直接使用父类提供的方法
    // 也可以添加自定义的业务逻辑
}
```

### 4. 基本 CRUD 操作

```java
// 添加数据
User user = new User();
user.setName("张三");
user.setAge(25);
user.setEmail("zhangsan@example.com");
userMapper.insert(user);

// 根据 ID 查询
User user = userMapper.selectById(1L);

// 查询所有
List<User> userList = userMapper.selectList(null);

// 更新数据
User user = new User();
user.setId(1L);
user.setName("李四");
userMapper.updateById(user);

// 删除数据
userMapper.deleteById(1L);
```

### 5. 条件构造器使用

```java
// 条件查询示例
QueryWrapper<User> queryWrapper = new QueryWrapper<>();
queryWrapper.like("name", "张")    // 模糊查询
           .gt("age", 20)         // 大于
           .orderByDesc("id");    // 排序
List<User> userList = userMapper.selectList(queryWrapper);

// 也可以使用 Lambda 条件构造器
LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
lambdaQueryWrapper.like(User::getName, "张")
                 .gt(User::getAge, 20)
                 .orderByDesc(User::getId);
List<User> userList = userMapper.selectList(lambdaQueryWrapper);
```

### 6. 分页查询

```java
// 配置分页插件（在配置类中）
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
}

// 使用分页查询
Page<User> page = new Page<>(1, 10); // 第1页，每页10条
QueryWrapper<User> queryWrapper = new QueryWrapper<>();
queryWrapper.orderByDesc("id");
IPage<User> userPage = userMapper.selectPage(page, queryWrapper);

// 获取分页数据
List<User> records = userPage.getRecords();    // 数据列表
long total = userPage.getTotal();              // 总条数
long current = userPage.getCurrent();          // 当前页
long pages = userPage.getPages();              // 总页数
```

## 🔍 常见问题

### 1. 数据库连接失败
- 确认 MySQL 服务是否已启动
- 检查数据库连接配置（URL、用户名、密码）是否正确
- 确保数据库驱动版本与 MySQL 服务器版本兼容

### 2. SQL 执行错误
- 检查 SQL 语法是否正确
- 确认表结构是否与实体类定义一致
- 查看 MyBatis-Plus 生成的 SQL 语句进行调试

### 3. 分页查询不起作用
- 确认是否正确配置了分页插件
- 检查分页参数是否正确传递

## 📚 参考资料

- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [MyBatis-Plus GitHub 仓库](https://github.com/baomidou/mybatis-plus)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)