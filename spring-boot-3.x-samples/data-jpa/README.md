# Data JPA 示例

这个模块展示了如何在Spring Boot 3.x中集成JPA (Java Persistence API) 和MySQL数据库，实现基本的CRUD操作，并提供了完整的前端用户管理界面。本示例专为0基础学习JPA的开发者设计，通过实际代码示例和详细说明，帮助您快速掌握JPA的核心概念和使用方法。

## 技术栈
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.3.0
- springdoc-openapi (Swagger 3)
- Lombok
- 前端：HTML + Tailwind CSS + JavaScript
- Font Awesome 图标库

## 功能说明
本示例实现了完整的用户管理功能，包括：
- 后端API：获取所有用户列表、根据ID获取用户详情、创建新用户、更新用户信息、删除用户
- 前端界面：响应式用户管理系统，支持用户的增删改查、搜索、分页等功能
- API文档：集成Swagger 3，提供完整的API文档和测试功能

## 数据库配置
在运行应用前，请确保你已经安装了MySQL数据库，并创建了相应的数据库：

```sql
CREATE DATABASE spring_boot_3_sample CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

在`src/main/resources/`目录下创建`application-mysql.yml`文件，并添加以下内容：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/spring_boot_3_sample?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
    username: your_username
    password: your_password
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
# 打包（跳过测试）
mvn clean package -DskipTests

# 运行jar包
java -jar target/data-jpa-1.0-SNAPSHOT.jar
```

## 访问方式
应用启动后，可以通过以下URL访问系统：

### 前端管理界面
- `http://localhost:8080/data-jpa/` - 用户管理系统前端页面
  - 支持用户的增删改查、搜索、分页等功能
  - 提供数据统计和可视化展示

### 用户管理API
- GET `http://localhost:8080/data-jpa/api/users` - 获取所有用户
- GET `http://localhost:8080/data-jpa/api/users/{id}` - 根据ID获取用户
- POST `http://localhost:8080/data-jpa/api/users` - 创建新用户
  - 请求体示例：
    ```json
    {
      "username": "johndoe",
      "password": "password123",
      "email": "john.doe@example.com",
      "fullName": "John Doe"
    }
    ```
- PUT `http://localhost:8080/data-jpa/api/users/{id}` - 更新用户信息
  - 请求体示例同创建用户
- DELETE `http://localhost:8080/data-jpa/api/users/{id}` - 删除用户

## API文档查看方式
本项目集成了Swagger 3 (springdoc-openapi)，可以通过以下URL访问API文档：

http://localhost:8080/data-jpa/swagger-ui.html

## Spring Boot 3.x 与 JPA 的主要变化
1. 使用Jakarta EE替代了JEE (javax.persistence -> jakarta.persistence)
2. 支持Java 17及以上版本
3. JPA接口和注解有小幅更新
4. Hibernate版本升级到6.x系列，提供了更多性能优化

## JPA 核心概念介绍

### 什么是JPA
JPA (Java Persistence API) 是Java EE的一部分，它提供了一种对象关系映射(ORM)规范，用于将Java对象映射到关系型数据库表。Spring Data JPA是Spring对JPA的封装，简化了JPA的开发。

### 实体类(Entity)注解说明

#### 常用注解
| 注解 | 说明 | 示例 |
|-----|------|------|
| @Entity | 声明这是一个实体类，映射到数据库表 | `@Entity` |
| @Table | 指定实体类映射的数据库表名 | `@Table(name = "jpa_users")` |
| @Id | 声明主键字段 | `@Id` |
| @GeneratedValue | 指定主键生成策略 | `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| @Column | 声明普通字段属性 | `@Column(name = "username", nullable = false, unique = true, length = 50)` |
| @PrePersist | 实体持久化前执行的方法 | `@PrePersist` |
| @PreUpdate | 实体更新前执行的方法 | `@PreUpdate` |

#### 主键生成策略
| 策略 | 说明 |
|-----|------|
| IDENTITY | 数据库自增，MySQL常用 |
| AUTO | 自动选择合适的策略 |
| SEQUENCE | 使用数据库序列，Oracle常用 |
| TABLE | 使用表模拟序列 |

### JPA Repository 方法命名规范详解

Spring Data JPA最强大的特性之一是可以通过方法名自动生成SQL查询。以下是常用的方法命名规范：

#### 1. 查询方法基础结构
查询方法名通常遵循以下格式：
```
[prefix]By[FieldName][Operator][FieldName][Operator]...
```

#### 2. 常用前缀(Prefix)
| 前缀 | 说明 | 返回类型 | 示例 |
|-----|------|---------|------|
| find | 查找单个或多个实体 | Optional<T> 或 List<T> | findByUsername, findByEmail |
| get | 获取单个或多个实体 | T 或 List<T> | getByUsername |
| read | 读取单个或多个实体 | T 或 List<T> | readByEmail |
| query | 查询单个或多个实体 | T 或 List<T> | queryByUsername |
| count | 统计符合条件的记录数 | long | countByUsername |
| exists | 检查是否存在符合条件的记录 | boolean | existsByUsername |
| delete | 删除符合条件的记录 | void | deleteByUsername |
| remove | 移除符合条件的记录 | void | removeByEmail |

#### 3. 常用操作符(Operator)
| 操作符 | 说明 | SQL对应 | 示例 |
|-------|------|---------|------|
| And | 逻辑与 | AND | findByUsernameAndEmail |
| Or | 逻辑或 | OR | findByUsernameOrEmail |
| Is,Equals | 等于 | = | findByUsernameIs, findByUsernameEquals |
| Between | 在某个范围之间 | BETWEEN | findByAgeBetween |
| LessThan | 小于 | < | findByAgeLessThan |
| LessThanEqual | 小于等于 | <= | findByAgeLessThanEqual |
| GreaterThan | 大于 | > | findByAgeGreaterThan |
| GreaterThanEqual | 大于等于 | >= | findByAgeGreaterThanEqual |
| After | 之后 | > | findByCreatedAtAfter |
| Before | 之前 | < | findByCreatedAtBefore |
| IsNull | 为空 | IS NULL | findByEmailIsNull |
| IsNotNull,NotNull | 不为空 | IS NOT NULL | findByEmailIsNotNull |
| Like | 模糊匹配 | LIKE | findByUsernameLike |
| NotLike | 不匹配 | NOT LIKE | findByUsernameNotLike |
| StartingWith | 以...开始 | LIKE 'xxx%' | findByUsernameStartingWith |
| EndingWith | 以...结束 | LIKE '%xxx' | findByUsernameEndingWith |
| Containing | 包含 | LIKE '%xxx%' | findByUsernameContaining |
| OrderBy | 排序 | ORDER BY | findByOrderByUsernameAsc |
| Not | 不等于 | != | findByUsernameNot |
| In | 在集合中 | IN | findByUsernameIn |
| NotIn | 不在集合中 | NOT IN | findByUsernameNotIn |
| True | 为true | = true | findByActiveTrue |
| False | 为false | = false | findByActiveFalse |

#### 4. 分页和排序
| 方法命名 | 说明 | 参数示例 |
|---------|------|---------|
| findByXxxOrderByYyyAsc | 按Yyy升序排序 | 无 |
| findByXxxOrderByYyyDesc | 按Yyy降序排序 | 无 |
| findByXxx(Pageable pageable) | 分页查询 | PageRequest.of(0, 10) |
| findByXxx(Sort sort) | 排序查询 | Sort.by("createdAt").descending() |
| findByXxx(Pageable pageable, Sort sort) | 分页并排序 | PageRequest.of(0, 10, Sort.by("createdAt").descending()) |

### 项目中JPA方法示例解析

在本项目的`UserRepository`接口中，我们使用了以下JPA方法命名规范：

```java
// 根据用户名查找用户 - findBy[FieldName]
Optional<User> findByUsername(String username);

// 根据邮箱查找用户 - findBy[FieldName]
Optional<User> findByEmail(String email);

// 检查用户名是否已存在 - existsBy[FieldName]
boolean existsByUsername(String username);

// 检查邮箱是否已存在 - existsBy[FieldName]
boolean existsByEmail(String email);
```

#### 代码解析
1. `findByUsername`：自动生成查询SQL `SELECT * FROM jpa_users WHERE username = ?`
2. `findByEmail`：自动生成查询SQL `SELECT * FROM jpa_users WHERE email = ?`
3. `existsByUsername`：自动生成查询SQL `SELECT COUNT(*) FROM jpa_users WHERE username = ?`，返回结果大于0则为true
4. `existsByEmail`：自动生成查询SQL `SELECT COUNT(*) FROM jpa_users WHERE email = ?`，返回结果大于0则为true

### 进阶：自定义查询

除了通过方法名生成查询外，Spring Data JPA还支持使用`@Query`注解编写自定义JPQL或SQL查询：

```java
// 使用JPQL查询
@Query("SELECT u FROM User u WHERE u.username = :username")
Optional<User> findUserByUsername(@Param("username") String username);

// 使用原生SQL查询
@Query(value = "SELECT * FROM jpa_users WHERE email = :email", nativeQuery = true)
Optional<User> findUserByEmailNative(@Param("email") String email);

// 更新操作
@Modifying
@Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
int updatePassword(@Param("id") Long id, @Param("password") String password);


## JPA 最佳实践

### 1. 使用Optional返回可能为空的结果
```java
// 推荐做法
Optional<User> findByUsername(String username);

// 使用示例
Optional<User> userOptional = userRepository.findByUsername("admin");
userOptional.ifPresent(user -> {
    // 用户存在时的处理逻辑
    System.out.println("找到用户: " + user.getFullName());
});

// 或者使用orElse提供默认值
User user = userRepository.findByUsername("admin")
    .orElse(new User());

// 使用orElseThrow抛出异常
User requiredUser = userRepository.findByUsername("admin")
    .orElseThrow(() -> new RuntimeException("用户不存在"));
```

### 2. 避免在实体类中使用复杂业务逻辑
实体类应该保持简单，主要负责数据映射，复杂的业务逻辑应该放在Service层。

### 3. 合理使用事务注解
在Service层使用`@Transactional`注解来管理事务，避免在Repository层使用。

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public User saveUser(User user) {
        // 业务逻辑处理
        return userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
```

### 4. 注意性能优化
- 使用分页查询处理大量数据
- 避免N+1查询问题，可以使用`@EntityGraph`或自定义查询优化
- 合理使用缓存

## 常见问题解答

### 1. JPA与MyBatis的区别？
- JPA是ORM规范，自动生成SQL，开发效率高，学习成本较低
- MyBatis需要手动编写SQL，灵活性更高，适合复杂查询场景
- JPA适合CRUD操作简单的项目，MyBatis适合SQL优化要求高的项目

### 2. 如何解决N+1查询问题？
可以使用`@EntityGraph`注解或者在`@Query`中使用JOIN FETCH来避免N+1查询：

```java
// 使用@EntityGraph
@EntityGraph(attributePaths = {"roles"})
List<User> findByUsername(String username);

// 或者使用JOIN FETCH
@Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
Optional<User> findUserWithRoles(@Param("username") String username);
```

### 3. 如何进行复杂查询？
对于复杂查询，可以使用以下几种方式：
- 使用`@Query`注解编写JPQL或原生SQL
- 使用Specification API进行动态查询
- 使用Criteria API构建复杂查询条件

### 4. 如何处理关系映射？
JPA支持多种关系映射：
- @OneToOne：一对一关系
- @OneToMany：一对多关系
- @ManyToOne：多对一关系
- @ManyToMany：多对多关系

## 测试

### 单元测试和集成测试
该项目包含单元测试和集成测试，使用H2内存数据库进行测试，不影响实际的MySQL数据库。

```bash
# 运行测试
mvn test
```

### 前端界面测试
访问前端管理界面后，可以直接通过界面进行功能测试，包括：
- 添加新用户
- 查看用户列表
- 编辑用户信息
- 删除用户
- 搜索用户
- 分页浏览

## 前端界面功能特点
- 响应式设计，适配不同屏幕尺寸
- 现代化UI设计，使用Tailwind CSS构建
- 完整的增删改查功能
- 实时数据统计展示
- 搜索和分页功能
- 加载状态和错误处理
- 平滑的动画和过渡效果
- 深色模式支持

## 项目结构
主要目录结构：
```
src/main/
├── java/                  # Java源代码
├── resources/             # 资源文件
│   ├── application.yml    # 应用配置文件
│   └── static/            # 静态资源
│       ├── index.html     # 主页面
│       ├── css/           # 样式文件
│       └── js/            # JavaScript文件
```