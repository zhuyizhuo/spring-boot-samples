# Spring Boot 3.x AI 集成演示项目

这是一个演示如何使用Spring Boot 3.x集成多种AI服务的示例项目。项目展示了如何通过Spring AI框架与多种AI模型提供商（如硅基流动、DeepSeek等）进行交互，并提供多种AI功能演示。

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0--M4-blue)

## 功能特性

本项目提供了以下功能演示：

### 核心功能

1. **智能聊天** - 与AI模型进行对话交互
2. **代码生成** - 使用AI生成和解释代码
3. **内容处理** - 对文本、图像等内容进行处理
4. **实用工具** - 各种AI辅助工具

### 技术特性

- 多AI服务提供商支持（硅基流动、DeepSeek等）
- RESTful API设计
- Web界面交互
- 会话管理
- 速率限制
- H2数据库支持

## 技术栈

- **框架**：Spring Boot 3.2.5
- **语言**：Java 17
- **前端**：Thymeleaf, Bootstrap, jQuery
- **数据库**：H2（内存/文件）
- **AI框架**：Spring AI 1.0.0-M4
- **AI提供商支持**：
  - 硅基流动 (SiliconFlow)
  - DeepSeek
  - 通义千问 (Qwen)
  - GLM智谱
  - MiniMax
  - Ollama（本地）

## 项目结构

```
spring-ai-demo/
├── src/main/java/com/github/zhuyizhuo/ai/demo
│   ├── SpringAiDemoApplication.java          # 应用程序入口
│   ├── controller/                           # 控制器层
│   │   ├── HomeController.java
│   │   └── SpringAiController.java
│   ├── exception/                            # 异常处理
│   │   ├── ApiException.java
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   ├── model/                               # 数据模型
│   │   ├── AIMessage.java
│   │   ├── ChatRequest.java
│   │   ├── CodeRequest.java
│   │   ├── CodeResponse.java
│   │   ├── ContentRequest.java
│   │   ├── ContentResponse.java
│   │   ├── Conversation.java
│   │   ├── HealthStatus.java
│   │   ├── UtilityRequest.java
│   │   └── UtilityResponse.java
│   └── service/                             # 服务层
│       ├── AIService.java
│       ├── ConversationService.java
│       ├── OpenAIServiceImpl.java
│       └── SpringAiService.java
├── src/main/resources
│   ├── application.yml                      # 应用配置
│   ├── static/                              # 静态资源
│   └── templates/                           # Thymeleaf模板
│       ├── chat.html                        # 聊天页面
│       ├── code.html                        # 代码页面
│       ├── content.html                     # 内容页面
│       ├── dashboard.html                   # 仪表板
│       └── utility.html                     # 工具页面
└── pom.xml                                 # Maven配置文件
```

## 快速开始

### 前置条件

- Java 17+
- Maven 3.6+
- 可选的API密钥（取决于要使用的AI提供商）

### 运行步骤

1. **克隆项目**

```bash
git clone https://github.com/your-repo/spring-boot-samples.git
cd spring-boot-samples/spring-boot-3.x-samples/spring-ai-demo
```

2. **编译项目**

```bash
mvn clean package -DskipTests
```

3. **运行应用**

```bash
java -jar target/spring-ai-demo-1.0-SNAPSHOT.jar
```

4. **访问应用**

应用启动后，在浏览器中访问：
- 主页：`http://localhost:8083/`
- 聊天：`http://localhost:8083/api/chat`
- 仪表板：`http://localhost:8083/api/dashboard`
- 健康检查：`http://localhost:8083/actuator/health`
- H2控制台：`http://localhost:8083/h2-console`

### 配置文件

默认情况下，应用使用硅基流动配置。您可以通过编辑`application.yml`文件中的`spring.profiles.active`参数来切换AI提供商：

```yaml
spring:
  profiles:
    active: siliconflow
```

支持的配置包括：
- `siliconflow` - 硅基流动（默认）
- `deepseek` - DeepSeek
- `qwen` - 通义千问
- `glm` - GLM智谱
- `minimax` - MiniMax
- `ollama` - Ollama（本地）

## API说明

### 聊天API

- **请求方式**: POST
- **接口地址**: `/api/chat`
- **请求参数**:
  ```json
  {
    "message": "你好",
    "conversationId": "可选的会话ID"
  }
  ```
- **响应**:
  ```json
  {
    "response": "AI的回复",
    "conversationId": "会话ID"
  }
  ```

### 清除会话

- **请求方式**: DELETE
- **接口地址**: `/api/conversation?conversationId=会话ID`
- **响应**:
  ```json
  {
    "message": "会话已成功清除"
  }
  ```

## 开发指南

### 添加新的AI提供商

1. 在`application.yml`中添加新的配置块
2. 配置相应的API密钥和基础URL
3. 重新编译并运行应用

### 添加新功能

1. 创建新的模型类（在`model`包中）
2. 创建新的服务实现（在`service`包中）
3. 创建新的控制器方法（在`controller`包中）
4. 添加相应的前端页面（在`templates`目录中）

## 故障排除

### 常见问题

1. **启动失败**
   - 确保Java 17或更高版本已安装
   - 检查端口8083是否被占用
   - 查看日志以了解具体错误

2. **无法连接AI服务**
   - 检查API密钥是否有效
   - 确认网络连接正常
   - 检查防火墙设置

3. **数据库连接问题**
   - H2控制台用户：`sa`
   - H2控制台密码：`password`
   - JDBC URL: `jdbc:h2:mem:testdb`

### 日志

应用运行期间，日志将输出到控制台。启动时也会显示应用访问信息。

## 许可证

本项目使用Apache 2.0许可证 - 详情查看[LICENSE](../LICENSE)文件。

## 贡献

欢迎提交Issues和Pull Requests来改进本项目。