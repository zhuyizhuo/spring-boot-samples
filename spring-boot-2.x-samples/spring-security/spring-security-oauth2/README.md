# Spring Security OAuth2 集成示例

本模块演示了如何在Spring Boot 2.x项目中集成OAuth2，实现认证和授权功能。

## 功能特性

- OAuth2 授权服务器实现
- OAuth2 资源服务器配置
- 基于角色的访问控制（RBAC）
- 多种授权模式支持（密码模式、授权码模式等）
- 内存存储令牌和用户信息

## 技术栈

- Spring Boot 2.7.5
- Spring Security
- Spring Security OAuth2
- Java 1.8

## 快速开始

### 测试页面

项目提供了一个Web测试页面，方便您测试OAuth2的各种功能：

访问地址：`http://localhost:8085/oauth2/oauth2-test.html`

测试页面功能：
- 获取访问令牌（使用密码模式）
- 刷新令牌
- 测试不同权限级别的API（公共API、用户API、管理员API）
- 复制令牌信息

### 配置说明

项目使用内存存储用户信息和客户端信息，默认配置如下：

**客户端信息**：
- clientId: `client-app`
- clientSecret: `client-secret`
- 授权类型: `password`, `authorization_code`, `refresh_token`, `implicit`
- 作用域: `read`, `write`
- 访问令牌有效期: 3600秒（1小时）
- 刷新令牌有效期: 86400秒（24小时）

**用户信息**：
- 普通用户: 用户名 `user`，密码 `password`，角色 `USER`
- 管理员用户: 用户名 `admin`，密码 `password`，角色 `USER`, `ADMIN`

### 启动应用

使用Maven命令启动应用：

```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8085/oauth2` 启动。

## API 端点

### 公共API（无需认证）

- `GET /api/public/hello` - 公共问候接口
- `GET /api/public/info` - 公共信息接口

### 用户API（需要USER角色）

- `GET /api/user/profile` - 获取当前用户信息
- `GET /api/user/data` - 获取用户数据

### 管理员API（需要ADMIN角色）

- `GET /api/admin/dashboard` - 管理员仪表盘
- `GET /api/admin/settings` - 系统设置

### OAuth2 端点

- `POST /oauth/token` - 获取访问令牌
- `GET /oauth/authorize` - 授权端点（授权码模式）
- `GET /oauth/check_token` - 验证令牌

## 获取访问令牌

### 密码模式（Password Grant）

使用以下命令获取访问令牌：

```bash
curl -X POST \
  http://localhost:8085/oauth2/oauth/token \
  -H 'Authorization: Basic Y2xpZW50LWFwcDpjbGllbnQtc2VjcmV0' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password&username=user&password=password&scope=read%20write'
```

### 授权码模式（Authorization Code Grant）

1. 访问授权页面：
   ```
   http://localhost:8085/oauth2/oauth/authorize?response_type=code&client_id=client-app&redirect_uri=http://localhost:8085/oauth2/callback&scope=read%20write
   ```

2. 登录后，将重定向到回调URL并附带授权码

3. 使用授权码获取访问令牌：
   ```bash
   curl -X POST \
     http://localhost:8085/oauth2/oauth/token \
     -H 'Authorization: Basic Y2xpZW50LWFwcDpjbGllbnQtc2VjcmV0' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -d 'grant_type=authorization_code&code={授权码}&redirect_uri=http://localhost:8085/oauth2/callback'
   ```

## 访问受保护的API

使用获取到的访问令牌访问受保护的API：

```bash
curl -X GET \
  http://localhost:8085/oauth2/api/user/data \
  -H 'Authorization: Bearer {访问令牌}'
```

## 刷新令牌

当访问令牌过期时，可以使用刷新令牌获取新的访问令牌：

```bash
curl -X POST \
  http://localhost:8085/oauth2/oauth/token \
  -H 'Authorization: Basic Y2xpZW50LWFwcDpjbGllbnQtc2VjcmV0' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=refresh_token&refresh_token={刷新令牌}'
```

## 注意事项

- 本示例使用内存存储令牌和用户信息，在实际生产环境中应使用持久化存储
- 密码模式（Password Grant）不推荐在生产环境中使用，建议使用授权码模式或其他更安全的授权模式
- 在实际生产环境中，应使用BCrypt等安全的密码编码器
- 客户端密钥和用户密码应进行适当的保护，不应明文存储

## 扩展阅读

- [Spring Security OAuth2 文档](https://projects.spring.io/spring-security-oauth/docs/oauth2.html)
- [OAuth 2.0 规范](https://tools.ietf.org/html/rfc6749)