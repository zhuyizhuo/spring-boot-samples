# Spring Boot 3.x 集成 Ehcache 示例

本项目演示了如何在 Spring Boot 3.x 中集成 Ehcache 3，实现高效的缓存管理。

## 技术栈

- Spring Boot 3.2.0
- Spring Cache
- Ehcache 3.10.0
- Java 17

## 项目结构

```
src/main/java/com/example/ehcache/
├── EhcacheDemoApplication.java      # Spring Boot 启动类
├── config/
│   └── EhcacheConfig.java           # Ehcache 配置类
├── controller/
│   └── UserController.java          # RESTful API 控制器
├── entity/
│   └── User.java                    # 用户实体类
└── service/
    ├── UserService.java             # 用户服务接口
    └── impl/
        └── UserServiceImpl.java     # 用户服务实现类（带缓存）
```

## 功能特性

1. **缓存注解的使用**：
   - `@Cacheable`：缓存方法结果
   - `@CachePut`：更新缓存
   - `@CacheEvict`：清除缓存

2. **多级缓存支持**：
   - 堆内存缓存
   - 堆外内存缓存

3. **缓存配置方式**：
   - 注解配置
   - XML配置（可选）

4. **缓存过期策略**：
   - 基于时间的过期策略

## 快速开始

### 1. 运行项目

```bash
cd spring-boot-3.x-samples/ehcache-demo
mvn spring-boot:run
```

### 2. 测试缓存功能

应用启动后，可以通过以下接口测试缓存功能：

#### 测试缓存性能
```bash
# 测试缓存性能（连续调用两次，观察第二次是否走缓存）
curl http://localhost:8081/api/users/test-cache/1
```

#### 获取用户（测试缓存读取）
```bash
# 第一次调用（慢，走数据库）
curl http://localhost:8081/api/users/1
# 第二次调用（快，走缓存）
curl http://localhost:8081/api/users/1
```

#### 获取所有用户
```bash
curl http://localhost:8081/api/users
```

#### 清除缓存
```bash
curl -X POST http://localhost:8081/api/users/clear-cache
```

## 缓存配置说明

### 1. 注解配置

在 `UserServiceImpl.java` 中使用了 Spring Cache 注解进行缓存管理：

- `@Cacheable(value = "userCache", key = "#id")`：缓存用户信息
- `@Cacheable(value = "userListCache")`：缓存用户列表
- `@CachePut(value = "userCache", key = "#user.id")`：更新用户缓存
- `@CacheEvict(value = {"userCache", "userListCache"}, allEntries = true)`：清除缓存

### 2. XML配置（可选）

项目提供了 `ehcache.xml` 配置文件，可以在 `application.yml` 中启用：

```yaml
spring:
  cache:
    jcache:
      config: classpath:ehcache.xml
```

## 观察缓存效果

启动应用后，可以通过控制台日志观察缓存的工作情况：

- 第一次访问会显示 "从数据库查询用户: X"，表示缓存未命中
- 第二次访问相同数据会直接返回，不会显示上述日志，表示缓存命中
- 控制台会显示每次请求的处理时间，可以明显看到缓存命中时的性能提升

## 注意事项

1. 缓存的对象需要实现 `Serializable` 接口
2. 合理设置缓存过期时间，避免数据过期问题
3. 在更新数据时，确保同步更新缓存，避免缓存不一致
4. 可以根据实际需求调整缓存的内存大小和过期策略

## 扩展阅读

- [Spring Cache 官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache)
- [Ehcache 官方文档](https://www.ehcache.org/documentation/)
- [Spring Boot Cache 支持](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.caching)