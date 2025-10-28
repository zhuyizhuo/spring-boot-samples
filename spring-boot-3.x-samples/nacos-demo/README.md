# Spring Boot 3.x Nacos 集成示例

一个基于 Spring Boot 3.x 和 Nacos 的集成示例项目，展示了 Nacos 配置中心和服务发现的核心功能。

## 功能特性

- **Nacos 配置中心集成**：支持从 Nacos 获取配置信息，支持配置动态刷新
- **Nacos 服务注册与发现**：自动注册服务到 Nacos，支持服务发现
- **本地配置文件分离**：使用 application-local.yml 存放本地开发配置
- **交互式 HTML 测试页面**：提供友好的 Web 界面测试 Nacos 功能
- **启动时打印访问地址**：应用启动时自动打印所有可用的访问地址
- **API 文档自动生成**：集成 SpringDoc OpenAPI (Swagger)，自动生成 API 文档
- **健康检查**：提供服务健康状态检查接口

## 技术栈

- **Spring Boot**：3.2.0
- **Spring Cloud**：2023.0.0
- **Spring Cloud Alibaba**：2023.0.0.0
- **Nacos**：2.x (配置中心、服务发现)
- **SpringDoc OpenAPI**：2.3.0 (Swagger API 文档)
- **Java**：17
- **Maven**：3.6+ 
- **Tailwind CSS**：3.x (前端样式)
- **Font Awesome**：4.7.0 (图标)

## 环境要求

1. **JDK 17** 或更高版本
2. **Maven 3.6** 或更高版本
3. **Nacos Server 2.x**

## 配置结构说明

本项目采用清晰的配置分层结构，将通用配置和环境特定配置分离：

### 1. 配置文件分层

- **application.yml**: 包含通用配置，如端口、应用名称、Swagger配置和通用日志设置
- **application-local.yml**: 包含本地开发环境特定配置，如Nacos连接信息和本地测试配置

### 2. Nacos配置说明

本地开发环境的Nacos配置位于`application-local.yml`中，为了安全性，敏感信息已改为从环境变量获取。主要配置项如下：

```yaml
spring:
  config:
    activate:
      on-profile: local
    import: optional:nacos:nacos-demo-local.properties
  # Nacos配置中心配置
  cloud:
    nacos:
      config:
        # 基本配置
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
        namespace: ${NACOS_NAMESPACE:}
        group: ${NACOS_GROUP:DEFAULT_GROUP}
        username: ${NACOS_USERNAME:nacos}
        password: ${NACOS_PASSWORD:your_password}
        file-extension: properties
        # 确保编码设置正确
        encode: UTF-8
        # 简化配置，只保留必要项
        timeout: ${NACOS_TIMEOUT:5000}
        refresh-enabled: true
        # 禁用服务发现
        discovery:
          enabled: false
```

### 3. 本地备用配置

当Nacos不可用时，可以通过取消`application-local.yml`中的注释来使用本地配置：

```yaml
# 取消注释后可以不依赖Nacos进行本地开发测试
#nacos:
#  demo:
#    config:
#      name: "Nacos配置示例"
#      description: "这是一个测试配置，用于演示Nacos配置中心的动态刷新功能"
#      version: "1.0.0"
#      enabled: true
#      timeout: 30
#      maxConnections: 100
```

### 4. 配置优先级

Spring Boot配置加载优先级（从高到低）：
1. 命令行参数
2. Nacos配置中心的配置
3. 环境变量
4. `application-{profile}.yml`（如`application-local.yml`）
5. `application.yml`

### 5. 环境变量配置指南

为了提高安全性，项目中敏感的Nacos配置信息已改为从环境变量获取。以下是可用的环境变量配置项：

| 环境变量名 | 描述 | 默认值 | 示例 |
|----------|------|-------|------|
| NACOS_SERVER_ADDR | Nacos服务器地址和端口 | localhost:8848 | localhost:8848 |
| NACOS_NAMESPACE | Nacos命名空间ID | 空字符串 | public |
| NACOS_GROUP | Nacos配置分组 | DEFAULT_GROUP | MY_GROUP |
| NACOS_USERNAME | Nacos访问用户名 | nacos | my_username |
| NACOS_PASSWORD | Nacos访问密码 | 空字符串 | my_secure_password |
| NACOS_TIMEOUT | Nacos连接超时时间（毫秒） | 5000 | 10000 |

#### 设置环境变量的方法

**Windows (PowerShell):**
```powershell
$env:NACOS_SERVER_ADDR="localhost:8848"
$env:NACOS_USERNAME="my_user"
$env:NACOS_PASSWORD="my_password"
java -jar nacos-demo-1.0-SNAPSHOT.jar --spring.profiles.active=local
```

**Linux/MacOS (Bash):**
```bash
export NACOS_SERVER_ADDR="localhost:8848"
export NACOS_USERNAME="my_user"
export NACOS_PASSWORD="my_password"
java -jar nacos-demo-1.0-SNAPSHOT.jar --spring.profiles.active=local
```

**Docker环境:**
```bash
docker run -d \
  -e NACOS_SERVER_ADDR="nacos-server:8848" \
  -e NACOS_USERNAME="my_user" \
  -e NACOS_PASSWORD="my_password" \
  -p 8080:8080 \
  nacos-demo:1.0-SNAPSHOT
```

## 构建与启动

### 1. 确保 Nacos Server 已启动

请确保本地已经安装并启动了 Nacos Server 2.x。默认情况下，Nacos Server 运行在 `http://localhost:8848`。

### 2. 构建项目

在项目根目录下执行以下命令构建项目：

```bash
mvn clean install
```

### 3. 启动应用

构建成功后，可以通过以下方式启动应用：

#### 方式一：使用 Maven 命令

```bash
mvn spring-boot:run
```

#### 方式二：运行生成的 JAR 文件

```bash
java -jar target/nacos-demo-1.0-SNAPSHOT.jar
```

### 4. 访问服务

应用启动后，会自动打印所有可用的访问地址，例如：

```
----------------------------------------------------------	
应用 'nacos-demo' 已启动！
	本地访问地址: 	http://localhost:8080/
	网络访问地址: 	http://192.168.1.100:8080/
	Swagger文档: 	http://localhost:8080/swagger-ui.html
	API文档: 	http://localhost:8080/v3/api-docs
----------------------------------------------------------
```

## API 接口

### 1. 获取 Nacos 配置信息

- **URL**: `/api/nacos/config`
- **Method**: `GET`
- **Description**: 获取从 Nacos 配置中心或本地配置文件加载的配置信息
- **Response**: 
  ```json
  {
    "config": "配置内容",
    "enabled": true,
    "version": "1.0.0",
    "timestamp": "2023-10-23 15:30:45",
    "message": "配置信息来自Nacos配置中心或本地配置文件"
  }
  ```

### 2. 健康检查

- **URL**: `/api/health`
- **Method**: `GET`
- **Description**: 检查服务健康状态
- **Response**: 
  ```json
  {
    "status": "UP",
    "service": "nacos-demo",
    "time": "2023-10-23 15:30:45"
  }
  ```

### 3. 服务信息

- **URL**: `/api/service`
- **Method**: `GET`
- **Description**: 获取服务基本信息
- **Response**: 
  ```json
  {
    "serviceName": "nacos-demo",
    "description": "Spring Boot 3.x Nacos 集成示例",
    "features": ["Nacos 配置中心集成", "Nacos 服务注册与发现"...],
    "timestamp": "2023-10-23 15:30:45"
  }
  ```

## 测试页面

项目提供了一个交互式的 HTML 测试页面，方便用户测试 Nacos 配置功能：

- **访问地址**: `http://localhost:8080/`
- **功能**: 
  - 查看和刷新 Nacos 配置信息
  - 查看服务详情
  - 检查服务健康状态
  - 快速访问 API 文档和 Nacos 控制台

测试页面具有现代化的 UI 设计，支持响应式布局，在不同设备上都能提供良好的用户体验。

## 项目结构

```
src/main/java/com/github/zhuyizhuo/springboot/nacosdemo/
├── Application.java              # 应用入口类
├── config/                       # 配置类
│   └── ApplicationRunnerConfig.java  # 应用启动时的配置，用于打印访问地址
├── controller/                   # 控制器
│   └── NacosConfigController.java    # Nacos配置控制器
src/main/resources/
├── application.yml               # 主配置文件（包含通用配置）
├── application-local.yml         # 本地开发环境配置（包含Nacos连接信息）
└── static/                       # 静态资源
    └── index.html                # HTML测试页面
pom.xml                           # Maven依赖管理
```

### 配置文件说明

- **application.yml**: 存放通用配置，如端口、应用名称、Swagger配置和日志级别设置
- **application-local.yml**: 存放环境特定配置，包括Nacos连接信息和本地测试备用配置
- 这种分层设计确保了配置的清晰性和可维护性，同时支持多环境部署

## 工作原理

1. **配置管理**：
   - 应用启动时，优先加载 `application.yml` 中的通用配置
   - 然后加载激活的 profile 配置（`application-local.yml`），覆盖同名配置
   - 通过 `application-local.yml` 中的 `spring.config.import` 配置连接到 Nacos 配置中心
   - Nacos 配置中心的配置优先级高于本地配置文件
   - 使用 `@RefreshScope` 注解标记需要动态刷新配置的类
   - 当 Nacos 中的配置变更时，应用会自动刷新相关配置

2. **服务注册与发现**：
   - 应用启动时，通过 `@EnableDiscoveryClient` 注解自动注册到 Nacos 服务注册中心
   - 其他服务可以通过 Nacos 发现并调用本服务

3. **启动配置**：
   - `ApplicationRunnerConfig` 实现了 `ApplicationRunner` 接口，在应用启动后自动执行
   - 该类获取本机 IP 地址和端口信息，打印所有可用的访问地址

4. **测试页面**：
   - HTML 页面通过 AJAX 请求调用后端 API，获取配置信息、服务详情和健康状态
   - 页面使用 Tailwind CSS 实现响应式布局，提供现代化的用户界面

## 注意事项

1. **Nacos Server 配置**：
   - 默认情况下，项目配置连接到 `localhost:8848` 的 Nacos Server
   - 如果 Nacos Server 运行在其他地址，请修改 `application-local.yml` 中的 `server-addr` 配置

2. **本地配置文件**：
   - 项目使用 `spring.profiles.active=local` 激活本地配置文件
   - 开发环境下的配置建议放在 `application-local.yml` 文件中

3. **HTML 测试页面**：
   - 测试页面位于 `src/main/resources/static/index.html`
   - 页面使用 CDN 引入 Tailwind CSS 和 Font Awesome，需要互联网连接

## 扩展建议

1. **Nacos 配置分组**：可以在 `application-local.yml` 中添加 `group` 配置，实现配置分组管理

2. **配置加密**：对于敏感配置信息，可以集成 Spring Cloud Alibaba Nacos Config 的加密功能

3. **HTML 页面扩展**：可以根据业务需求，扩展测试页面的功能，如添加配置编辑和发布功能

4. **服务熔断**：可以集成 Spring Cloud Circuit Breaker 和 Resilience4j，实现服务熔断和限流

5. **消息推送**：可以集成 WebSocket，实现配置变更时的实时消息推送

## License

本项目基于 MIT 许可证开源。