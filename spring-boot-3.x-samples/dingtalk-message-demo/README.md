# 钉钉消息推送示例项目

本项目是一个基于Spring Boot 3.x的钉钉消息推送示例项目，演示了如何使用钉钉SDK发送文本消息、Markdown消息和工作通知消息。

## 功能特性

- 发送钉钉文本消息（支持@功能）
- 发送钉钉Markdown格式消息（支持@功能）
- 发送钉钉工作通知消息
- 提供REST API接口进行测试
- 集成Swagger文档
- 提供Web前端测试页面
- 应用启动时自动输出可访问页面信息

## 技术栈

- Spring Boot 3.x
- 钉钉SDK (dingtalk)
- SpringDoc OpenAPI
- Tailwind CSS（前端页面）
- Font Awesome（图标）

## 快速开始

### 1. 配置钉钉参数

项目使用独立的配置文件来存储敏感的钉钉配置信息，避免将这些信息提交到代码仓库中。

**步骤1：创建配置文件**

在`src/main/resources/`目录下创建`application-dingtalk.yml`文件，并添加以下内容：

```yaml
# 钉钉配置
# 此文件为本地配置，包含敏感信息，不应提交到版本控制系统

dingtalk:
  # 应用的AppKey，需要替换为实际的AppKey
  appKey: "your-dingtalk-appkey"
  # 应用的AppSecret，需要替换为实际的AppSecret
  appSecret: "your-dingtalk-appsecret"
  # 机器人Webhook，需要替换为实际的Webhook
  webhook: "https://oapi.dingtalk.com/robot/send?access_token=your-token"
  # 机器人签名密钥，需要替换为实际的secret
  secret: "your-dingtalk-robot-secret"
```

**步骤2：确保主配置文件已正确引用**

主配置文件`application.yml`中已配置自动加载`dingtalk`环境配置：

```yaml
spring:
  profiles:
    active: dev, dingtalk
```

**注意**：实际使用时，请将上述参数替换为您自己的实际配置。application-dingtalk.yml文件已添加到.gitignore中，不会被提交到代码仓库。

### 2. 启动应用

使用Maven命令启动应用：

```bash
mvn spring-boot:run
```

启动成功后，控制台会自动输出可访问的页面信息，包括Swagger API文档和前端测试页面的访问地址。

### 访问API文档

应用启动后，可以通过以下地址访问Swagger API文档：

```
http://localhost:8082/dingtalk/swagger-ui.html
```

## 使用前端测试页面

应用启动后，可以通过以下地址访问前端测试页面：

```
http://localhost:8082/dingtalk/dingtalk-message-test.html
```

前端测试页面提供了以下功能：
- 发送文本消息（支持配置@功能）
- 发送Markdown格式消息（支持配置@功能）
- 发送工作通知消息
- 显示请求和响应结果
- 响应式设计，适配不同屏幕尺寸

使用方法简单直观，只需填写相应的表单字段，然后点击"发送"按钮即可。

## API接口说明

### 发送文本消息

```
POST /api/dingtalk/message/text
```

请求参数：
- `content` (必填): 消息内容
- `atMobiles` (可选): 需要@的手机号列表
- `isAtAll` (可选): 是否@所有人（默认false）

示例请求体：
```json
{
  "content": "这是一条测试文本消息",
  "atMobiles": ["13800138000"],
  "isAtAll": false
}
```

### 发送Markdown消息

```
POST /api/dingtalk/message/markdown
```

请求参数：
- `title` (必填): 消息标题
- `text` (必填): 消息内容（Markdown格式）
- `atMobiles` (可选): 需要@的手机号列表
- `isAtAll` (可选): 是否@所有人（默认false）

示例请求体：
```json
{
  "title": "测试Markdown消息",
  "text": "# 这是一条测试Markdown消息\n## 副标题\n- 项目地址\n- 测试内容",
  "atMobiles": ["13800138000"],
  "isAtAll": false
}
```

### 发送工作通知消息

```
POST /api/dingtalk/message/work-notice
```

请求参数：
- `userId` (必填): 用户ID
- `title` (必填): 消息标题
- `content` (必填): 消息内容

示例请求体：
```json
{
  "userId": "user123",
  "title": "工作通知",
  "content": "这是一条工作通知消息"
}```

## 注意事项

1. **配置文件说明**：
   - 请确保已正确配置`application-dingtalk.yml`中的钉钉相关参数
   - 所有配置参数都需要替换为实际的有效值
   - application-dingtalk.yml文件已添加到.gitignore中，不会被提交到代码仓库，确保敏感信息安全

2. **机器人配置**：
   - 使用机器人发送消息需要在钉钉群中添加自定义机器人并获取Webhook和签名密钥
   - 添加机器人时，建议设置关键词过滤，以提高安全性

3. **工作通知配置**：
   - 发送工作通知消息需要在钉钉开放平台创建应用并获取AppKey和AppSecret
   - 创建应用后，需要为应用分配相应的权限

4. **接口调用限制**：
   - 钉钉API有调用频率限制，请合理控制调用频率
   - 机器人消息和工作通知消息有不同的发送限制和生效范围

5. **环境要求**：
   - JDK 17或更高版本
   - Maven 3.6.0或更高版本
   - 网络连接正常，能够访问钉钉开放平台API