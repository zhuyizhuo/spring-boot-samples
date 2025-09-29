# Spring Boot MinIO 集成示例

## 项目介绍

本项目是一个 Spring Boot 集成 MinIO 的完整示例，提供了一套完整的文件管理解决方案。通过该示例，您可以快速学习如何在 Spring Boot 应用中集成 MinIO 实现文件的上传、下载、删除、管理等操作。

## 功能特性

- **灵活的文件上传**：支持 MultipartFile 和 InputStream 两种上传方式
- **便捷的文件下载**：支持通过文件名直接下载文件
- **文件删除**：支持删除指定的文件
- **预签名 URL**：生成具有时效性的文件临时访问链接
- **文件列表管理**：支持列出桶内文件，支持按前缀查询和递归查询
- **桶管理**：支持创建桶和检查桶是否存在
- **本地模拟实现**：支持在没有 MinIO 服务器的情况下使用本地模拟实现进行开发和测试
- **完整的日志记录**：使用 SLF4J 记录各操作的详细日志，便于调试和问题排查
- **单元测试**：提供完整的单元测试覆盖

## 环境要求

- JDK 1.8 或更高版本
- Maven 3.6 或更高版本
- 可选：MinIO 服务器（如不使用可通过本地模拟模式运行）

## 配置说明

为了保护敏感信息，项目采用了分离配置文件的方式：主配置文件(`application.yml`)包含公共配置，本地配置文件(`application-local.yml`)包含敏感信息。本地配置文件已被添加到`.gitignore`中，不会被提交到版本控制系统。

### 1. 创建本地配置文件

在 `src/main/resources/` 目录下创建 `application-local.yml` 文件，并添加以下敏感信息配置：

```yaml
# 本地配置文件 - 包含敏感信息，不应提交到版本控制系统
minio:
  endpoint: http://minio的IP:端口  # MinIO 服务器地址
  accessKey: 改为自己的访问密钥     # 访问密钥
  secretKey: 改为自己的密钥        # 密钥
  bucketName: 改为自己的桶名       # 默认桶名
  domain: http://minio的IP:端口/  # MinIO 域名
```

### 2. 主配置文件内容

主配置文件 `application.yml` 包含公共配置和占位符，会自动从本地配置文件中获取敏感信息：

```yaml
# 主配置文件 - 敏感信息已移至application-local.yml
minio:
  endpoint: ${minio.endpoint}
  accessKey: ${minio.accessKey}
  secretKey: ${minio.secretKey}
  bucketName: ${minio.bucketName}
  dirPrefix: upload/                # 文件存储前缀
  domain: ${minio.domain}
  mock:
    enabled: false                   # 是否启用本地模拟模式，默认 false

# 激活本地配置文件
spring:
  profiles:
    active: local
  servlet:
    multipart:
      max-file-size: 10MB          # 最大文件大小
      max-request-size: 10MB       # 最大请求大小

server:
  port: 8081  # 应用端口
```

### 3. 环境变量覆盖

如果需要在不同环境中使用不同的配置，可以通过环境变量覆盖配置值。Spring Boot 会自动将环境变量转换为配置属性。例如：

```bash
# 通过环境变量启动应用
java -jar target/minio-1.0.0.jar --minio.endpoint=http://your-minio-server:9000 --minio.accessKey=your-access-key --minio.secretKey=your-secret-key
```

## 快速开始

### 1. 启动方式选择

#### 方式一：使用实际 MinIO 服务器

可以使用 Docker 快速启动 MinIO 服务器：

```bash
docker run -p 9000:9000 -p 9001:9001 minio/minio server /data --console-address ":9001"
```

启动后，可以通过 `http://localhost:9001` 访问 MinIO 控制台，使用默认账号 `minioadmin`/`minioadmin` 登录。

#### 方式二：使用本地模拟模式

如果没有 MinIO 服务器或希望快速开发测试，可以启用本地模拟模式：

```yaml
minio:
  mock:
    enabled: true  # 启用本地模拟模式
```

模拟模式使用内存存储，不需要实际的 MinIO 服务器。系统会自动检测 MinIO 服务器连接状态，如连接失败也会自动切换到模拟模式。

### 2. 构建并运行项目

```bash
# 构建项目
mvn clean package

# 运行项目
java -jar target/minio-1.0.0.jar
```

### 3. 访问服务

项目启动成功后，会在控制台输出访问地址：

```
=========================================
	MinIO文件服务已启动成功！
	前端访问地址: http://localhost:8081/index.html
=========================================
```

## 接口文档

### 文件上传

- **URL**: `/api/minio/upload`
- **Method**: POST
- **参数**: `file` (MultipartFile)
- **返回**: 文件URL

### 文件下载

- **URL**: `/api/minio/download`
- **Method**: GET
- **参数**: `fileName` (String)
- **返回**: 文件下载响应

### 文件删除

- **URL**: `/api/minio/delete`
- **Method**: DELETE
- **参数**: `fileName` (String)
- **返回**: 删除结果

### 生成预签名URL

- **URL**: `/api/minio/presigned-url`
- **Method**: GET
- **参数**: 
  - `fileName` (String)
  - `expireTime` (int, 可选，默认3600秒)
- **返回**: 预签名URL

### 获取文件列表

- **URL**: `/api/minio/list`
- **Method**: GET
- **参数**: 
  - `bucketName` (String, 可选)
  - `prefix` (String, 可选)
  - `recursive` (boolean, 可选，默认true)
- **返回**: 文件列表

### 创建桶

- **URL**: `/api/minio/bucket`
- **Method**: POST
- **参数**: `bucketName` (String)
- **返回**: 创建结果

### 检查桶是否存在

- **URL**: `/api/minio/bucket/exists`
- **Method**: GET
- **参数**: `bucketName` (String)
- **返回**: 检查结果

## 代码结构

```
src/main/java/com/github/zhuyizhuo/sample/minio/
├── MinioApplication.java         # 应用主入口
├── config/
│   ├── MinioConfig.java          # MinIO 配置类
│   └── MinioServiceConfig.java   # MinIO 服务配置类（用于切换实际实现和模拟实现）
├── service/
│   ├── MinioService.java         # MinIO 服务接口
│   └── impl/
│       ├── MinioServiceImpl.java # MinIO 服务实际实现
│       └── MinioServiceMockImpl.java # MinIO 服务模拟实现
└── controller/
    └── MinioController.java      # MinIO 控制器
```

### 核心类说明

1. **MinioService**: 定义了文件操作的核心接口
2. **MinioServiceImpl**: 实际调用 MinIO 客户端的实现类
3. **MinioServiceMockImpl**: 本地模拟实现，不依赖 MinIO 服务器
4. **MinioConfig**: 读取和管理 MinIO 相关配置
5. **MinioServiceConfig**: 负责根据配置或连接状态选择合适的服务实现

## 本地模拟实现说明

项目提供了完整的 MinIO 本地模拟实现，主要特点包括：

1. 使用 ConcurrentHashMap 作为内存存储，模拟 MinIO 的文件和桶存储
2. 实现了 MinioService 接口的所有方法，包括上传、下载、删除、生成预签名URL等
3. 提供了与实际 MinIO 服务相同的 API 行为和响应格式
4. 支持动态切换，可通过配置文件或自动检测连接状态切换

模拟实现适用于：
- 开发和测试环境，无需配置真实 MinIO 服务器
- 网络受限环境
- 快速原型开发

## 测试支持

项目包含完整的单元测试，位于 `src/test/java` 目录下。测试类包括：

- **MinioServiceImplTest**: 测试文件操作的各项功能
- **TestConfig**: 测试配置类，提供测试所需的模拟对象

运行测试：

```bash
mvn test
```

## Postman 接口测试

项目根目录下提供了 Postman 接口测试集合文件 `MinIO-API-Collection.postman_collection.json`，包含了所有 API 接口的测试用例。

### 使用方法

1. **导入 Postman 集合**
   - 打开 Postman 应用
   - 点击左上角的 "Import" 按钮
   - 选择 "Upload Files"，然后选择项目根目录下的 `MinIO-API-Collection.postman_collection.json` 文件
   - 点击 "Import" 完成导入

2. **运行测试接口**
   - 导入成功后，在 Postman 左侧边栏可以看到 "MinIO API Collection" 集合
   - 展开集合，可以看到所有可用的 API 接口测试用例
   - 确保项目已启动（运行在 http://localhost:8081）
   - 选择要测试的接口，点击 "Send" 按钮发送请求

3. **接口说明**
   - **文件上传**：支持上传文件到 MinIO，需在 form-data 中选择文件
   - **文件下载**：通过文件名下载文件
   - **文件删除**：删除指定文件
   - **生成预签名URL**：生成临时访问链接，可设置过期时间
   - **获取文件列表**：获取桶内文件列表，支持前缀过滤
   - **创建桶**：创建新的存储桶
   - **检查桶是否存在**：检查指定存储桶是否存在

4. **测试文件**
   - 项目根目录下提供了 `test-upload.txt` 文件，可用于测试文件上传功能

## 注意事项

1. 在生产环境中，请确保正确配置 MinIO 服务器地址、访问密钥、密钥等信息
2. 默认的文件存储桶名称为 `springboot-sample`，应用会自动检查并创建该桶
3. 文件上传大小限制默认设置为 10MB，可以在 `application.yml` 中修改
4. 如需在生产环境使用，请确保正确配置安全措施，如 HTTPS、访问控制等
5. 预签名 URL 默认有效期为 3600 秒（1小时），可以通过参数调整
6. 模拟模式不适合生产环境使用，仅用于开发和测试

## 常见问题

### 1. 无法连接到 MinIO 服务器

- 检查 MinIO 服务器地址是否正确，以及服务器是否正常运行
- 可以临时启用模拟模式继续开发：`minio.mock.enabled: true`
- 系统会自动检测连接状态，如连接失败会自动切换到模拟模式

### 2. 文件上传失败

- 检查 MinIO 服务的存储桶是否存在，应用会自动创建桶
- 检查是否有足够的存储空间
- 检查文件大小是否超过限制

### 3. 访问权限问题

- 检查 MinIO 访问密钥和密钥是否正确
- 确保具有足够的操作权限

### 4. 模拟模式与实际服务的区别

- 模拟模式使用内存存储，应用重启后数据会丢失
- 模拟模式不支持所有 MinIO 高级功能，仅实现了核心文件操作
- 模拟模式下生成的预签名 URL 是模拟的，仅用于测试