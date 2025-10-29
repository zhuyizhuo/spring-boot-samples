# Spring Cloud Gateway 示例项目

## 项目简介

本项目演示了如何使用Spring Cloud Gateway构建API网关，包含了路由配置、过滤器使用、自定义过滤器等功能。Spring Cloud Gateway是一个基于Spring Boot和Spring WebFlux的API网关，提供了强大的路由功能和过滤器机制。

## 主要功能

1. **基础路由配置**：演示如何配置不同类型的路由规则
2. **全局过滤器**：展示如何实现和使用全局过滤器
3. **自定义路由过滤器**：展示如何创建可在配置文件中使用的自定义过滤器
4. **路由断言**：演示多种断言机制（路径、时间、请求头）
5. **内置过滤器**：展示路径重写、前缀去除等常用过滤器
6. **重试机制**：展示如何配置失败重试策略

## 技术栈

- Java 17
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- Spring Cloud Gateway
- Spring Boot Actuator

## 快速开始

### 环境要求

- JDK 17或更高版本
- Maven 3.8或更高版本

### 运行项目

```bash
# 进入项目目录
cd spring-cloud-gateway-demo

# 构建项目
mvn clean package

# 运行应用
mvn spring-boot:run
```

应用启动后，Gateway服务将在 **http://localhost:8080** 运行。

## 路由规则示例

### 1. 基本路由

将 `/baidu/**` 路径请求转发到百度，去除一级路径前缀：

```
GET http://localhost:8080/baidu
# 实际转发到 https://www.baidu.com
```

### 2. 路径重写路由

将 `/api/**` 路径请求重写后转发到 httpbin.org：

```
GET http://localhost:8080/api/get
# 实际转发到 https://httpbin.org/get
```

### 3. 基于时间的路由

仅在指定时间后生效的路由：

```
GET http://localhost:8080/time/get
# 转发到 https://httpbin.org/get
```

### 4. 基于请求头的路由

仅当请求包含特定请求头时才生效的路由：

```
GET http://localhost:8080/header/get
Header: X-Request-Id: 123456
# 转发到 https://httpbin.org/get
```

## 自定义组件

### 全局过滤器 (CustomGlobalFilter)

自定义全局过滤器，为所有请求添加自定义请求头并记录请求信息。

### 自定义路由过滤器工厂 (CustomRouteFilterFactory)

可在配置文件中配置的自定义过滤器，用于灵活添加特定请求头。

要使用此过滤器，可在路由配置中添加：

```yaml
filters:
  - name: CustomRoute
    args:
      headerName: X-Custom-Header
      headerValue: custom-value
```

或使用快捷方式：

```yaml
filters:
  - CustomRoute=X-Custom-Header,custom-value
```

## 监控端点

应用集成了Spring Boot Actuator，可通过以下端点查看和管理Gateway：

- **健康检查**: http://localhost:8080/actuator/health
- **Gateway路由信息**: http://localhost:8080/actuator/gateway/routes
- **所有端点**: http://localhost:8080/actuator

## 扩展指南

### 添加限流功能

要启用限流功能，需要：
1. 添加Redis依赖
2. 实现KeyResolver
3. 配置RequestRateLimiter过滤器

### 集成服务发现

本项目已添加Nacos服务发现客户端依赖，要启用服务发现路由：

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
```

然后可以使用服务ID作为URI：

```yaml
routes:
  - id: service_route
    uri: lb://service-name
    predicates:
      - Path=/service/**
```

## 许可证

本项目采用MIT许可证。