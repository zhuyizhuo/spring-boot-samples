# Spring AI Demo

Spring Boot 3.x 集成 AI 示例项目，演示如何在 Spring Boot 应用中集成和使用 AI 能力，包括 OpenAI、Ollama（本地模型）和向量数据库检索功能。

## 功能特性

- 支持多种 AI 模型：OpenAI GPT 模型和本地 Ollama 模型
- 提供 RESTful API 接口进行文本生成
- 支持向量数据库检索功能
- 包含完整的服务层和控制层架构
- 提供 API 文档（通过 SpringDoc OpenAPI）

## 技术栈

- Spring Boot 3.2.5
- Java 17
- Spring AI 0.8.1
- SpringDoc OpenAPI
- Lombok

## 项目结构

```
src/main/java/com/github/zhuyizhuo/springai/demo/
├── SpringAiApplication.java    # 应用入口类
├── controller/                  # 控制器层
│   └── AIController.java        # AI 相关 API 接口
├── service/                     # 服务层
│   ├── AIService.java           # AI 服务接口
│   └── AIServiceImpl.java       # AI 服务实现
└── model/                       # 数据模型
    ├── AIRequest.java           # AI 请求模型
    └── AIResponse.java          # AI 响应模型
```

## 配置说明

在 `application.yml` 文件中配置以下内容：

```yaml
spring:
  ai:
    # OpenAI 配置
    openai:
      api-key: "your-openai-api-key"  # 替换为您的 OpenAI API Key
      chat:
        options:
          model: gpt-3.5-turbo
          temperature: 0.7
    # Ollama 配置（本地运行的模型）
    ollama:
      base-url: "http://localhost:11434"
      chat:
        options:
          model: llama3
          temperature: 0.7
    # Pinecone 配置
    pinecone:
      api-key: "your-pinecone-api-key"  # 替换为您的 Pinecone API Key
      environment: "gcp-starter"
      index-name: "spring-ai-demo"
```

## API 文档

启动应用后，可以通过以下 URL 访问 API 文档：

- Swagger UI: http://localhost:8083/swagger-ui.html
- OpenAPI JSON: http://localhost:8083/v3/api-docs

## 接口说明

### 1. 通用 AI 生成接口

- URL: `POST /api/ai/generate`
- 请求体示例：
  ```json
  {
    "modelType": "openai",
    "prompt": "请解释什么是人工智能",
    "systemPrompt": "你是一个AI助手"
  }
  ```

### 2. OpenAI 生成接口

- URL: `GET /api/ai/openai/generate?prompt=请解释什么是人工智能`

### 3. Ollama 生成接口

- URL: `GET /api/ai/ollama/generate?prompt=请解释什么是人工智能`

### 4. 向量检索接口

- URL: `GET /api/ai/retrieve?query=搜索关键词`

### 5. 健康检查接口

- URL: `GET /api/ai/health`

## 本地开发与运行

### 前提条件

- JDK 17 或更高版本
- Maven 3.6 或更高版本
- 如需使用 Ollama，请先安装并启动 Ollama 服务
- 如需使用 OpenAI 或 Pinecone，请准备有效的 API Key

### 构建与运行

1. 克隆项目：
   ```bash
   git clone https://github.com/zhuyizhuo/spring-boot-samples.git
   cd spring-boot-samples/spring-boot-3.x-samples/spring-ai-demo
   ```

2. 构建项目：
   ```bash
   mvn clean package
   ```

3. 运行应用：
   ```bash
   mvn spring-boot:run
   ```
   或
   ```bash
   java -jar target/spring-ai-demo-1.0-SNAPSHOT.jar
   ```

## 注意事项

1. 在使用 OpenAI 和 Pinecone 功能前，请确保配置了有效的 API Key。
2. 使用 Ollama 功能前，请确保本地已安装并启动了 Ollama 服务。
3. 示例中的 API Key 和配置为演示用途，实际使用时请替换为您自己的配置。
4. 在测试环境中，建议使用 Ollama 本地模型进行测试，以避免 API 调用费用。

## 版本历史

- 1.0.0: 初始版本，支持 OpenAI、Ollama 和向量数据库检索功能

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](../LICENSE) 文件