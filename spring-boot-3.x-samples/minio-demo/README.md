# Spring Boot 3.x 集成 MinIO 演示项目

## 项目简介

这是一个基于 Spring Boot 3.x 集成 MinIO 对象存储服务的演示项目。通过该项目，您可以学习如何在 Spring Boot 应用中集成 MinIO，实现文件的上传、下载、删除、列表、信息获取和分享等功能。

## 功能特性

- ✅ 文件上传（支持默认存储桶和指定存储桶）
- ✅ 文件下载
- ✅ 文件删除
- ✅ 文件列表获取
- ✅ 文件信息查看
- ✅ 存储桶创建和检查
- ✅ 预签名 URL 生成（文件分享）
- ✅ 美观的 Web 界面进行操作
- ✅ RESTful API 接口
- ✅ 单独的 MinIO 配置文件

## 技术栈

- Spring Boot 3.x
- MinIO Java SDK 8.5.8
- Lombok
- SpringDoc OpenAPI (Swagger)
- Bootstrap 5
- Axios

## 环境要求

1. JDK 17 或更高版本
2. Maven 3.8 或更高版本
3. MinIO 服务器（本地或远程）
4. 浏览器（Chrome、Firefox、Safari 等）

## 快速开始

### 1. 配置 MinIO 服务器

确保您已经安装并启动了 MinIO 服务器。如果没有，可以参考 [MinIO 官方文档](https://min.io/docs/minio/linux/index.html) 进行安装和配置。

### 2. 配置项目

为了保护敏感信息，项目采用了外部化配置的方式。有两种配置方式：

#### 方式一：使用配置文件（本地开发推荐）

创建 `src/main/resources/application-minio.yml` 文件（该文件不应提交到版本控制），添加以下配置：

```yaml
# MinIO 服务器配置 - 此文件不应提交到版本控制
minio:
  url: http://localhost:9000
  accessKey: ${MINIO_ACCESS_KEY:minioadmin}
  secretKey: ${MINIO_SECRET_KEY:minioadmin}
  bucketName: ${MINIO_BUCKET_NAME:test-bucket}
  region: ${MINIO_REGION:us-east-1}
  
  # 连接配置
  connectTimeout: 5000
  writeTimeout: 30000
  readTimeout: 30000
  cacheTTL: 86400
```

注意：SSL配置已通过URL协议(http/https)自动处理，MinIO客户端会根据URL中的协议自动设置secure属性。

#### 方式二：使用环境变量（生产环境推荐）

直接设置环境变量，系统会自动读取：
- `MINIO_URL` - MinIO服务器地址
- `MINIO_ACCESS_KEY` - 访问密钥
- `MINIO_SECRET_KEY` - 密钥
- `MINIO_BUCKET_NAME` - 默认存储桶名称
- `MINIO_REGION` - 区域

### 3. 配置文件说明

- `application.yml` - 主配置文件，包含公共配置信息
- `application-minio.yml` - 包含敏感配置，不应提交到版本控制

### 3. 构建和运行项目

使用 Maven 构建并运行项目：

```bash
# 构建项目
mvn clean package

# 运行项目
java -jar target/minio-demo-1.0.0.jar
```

或者，您也可以直接在 IDE 中运行 `MinioDemoApplication.java` 类的 `main` 方法。

### 4. 访问应用

项目启动后，可以通过以下地址访问：

- **测试页面**：http://localhost:8080/minio-test.html
- **Swagger API 文档**：http://localhost:8080/swagger-ui/index.html

## API 接口说明

项目提供了完整的 RESTful API 接口，用于操作 MinIO 对象存储。以下是主要接口：

### 文件操作

- **上传文件到默认存储桶**：`POST /api/minio/upload/default`
- **上传文件到指定存储桶**：`POST /api/minio/upload`
- **下载默认存储桶中的文件**：`GET /api/minio/download/default`
- **下载指定存储桶中的文件**：`GET /api/minio/download`
- **删除默认存储桶中的文件**：`DELETE /api/minio/default`
- **删除指定存储桶中的文件**：`DELETE /api/minio`
- **列出默认存储桶中的文件**：`GET /api/minio/list/default`
- **列出指定存储桶中的文件**：`GET /api/minio/list`
- **获取默认存储桶中文件的信息**：`GET /api/minio/info/default`
- **获取指定存储桶中文件的信息**：`GET /api/minio/info`
- **生成默认存储桶中文件的分享链接**：`GET /api/minio/share/default`
- **生成指定存储桶中文件的分享链接**：`GET /api/minio/share`

### 存储桶操作

- **创建存储桶**：`POST /api/minio/bucket`
- **检查存储桶是否存在**：`GET /api/minio/bucket/exists`

## Web 界面使用指南

项目提供了直观的 Web 界面，方便您进行文件操作。

### 1. 上传文件

在 "上传文件" 选项卡中，您可以选择上传文件到默认存储桶或指定存储桶。上传时可以选择是否自定义对象名称。

### 2. 文件列表

在 "文件列表" 选项卡中，您可以查看默认存储桶或指定存储桶中的所有文件。对于每个文件，您可以进行下载、删除、查看信息和生成分享链接等操作。

### 3. 存储桶管理

在 "存储桶管理" 选项卡中，您可以创建新的存储桶或检查存储桶是否存在。

### 4. 帮助

在 "帮助" 选项卡中，您可以查看项目的使用说明和注意事项。

## 项目结构

```
src/main/java/com/github/zhuyizhuo/minio/demo/
├── MinioDemoApplication.java      # 主应用类
├── config/                        # 配置类
│   └── MinioConfig.java           # MinIO 配置类（包含配置验证逻辑）
├── service/                       # 服务接口
│   └── MinioService.java          # MinIO 服务接口
├── service/impl/                  # 服务实现
│   └── MinioServiceImpl.java      # MinIO 服务实现类
└── controller/                    # 控制器
    └── MinioController.java       # MinIO 控制器

src/main/resources/
├── static/                        # 静态资源
│   └── minio-test.html            # Web 测试页面
├── application.yml                # 主应用配置文件
└── application-minio.yml          # MinIO 敏感配置（不应提交到版本控制）
```

## 注意事项

1. 请确保 MinIO 服务器已正确配置并运行
2. 上传的文件大小受 `application.yml` 中的 `spring.servlet.multipart.max-file-size` 和 `spring.servlet.multipart.max-request-size` 配置限制
3. 分享链接的有效期默认为 86400 秒（24 小时），可以自定义
4. 默认存储桶名称为 `application-minio.yml` 中配置的 `minio.bucketName` 值或环境变量 `MINIO_BUCKET_NAME`
5. SSL配置已通过URL协议(http/https)自动处理，MinIO客户端会根据URL中的协议自动设置secure属性
6. **安全提示**：请不要将 `application-minio.yml` 文件提交到版本控制中，确保敏感信息安全
7. 项目启动时会验证MinIO配置的有效性，如配置缺失会提供明确的错误提示

## 常见问题

### 1. 连接 MinIO 服务器失败

- 请检查 `application-minio.yml` 中的配置是否正确
- 确保 MinIO 服务器正在运行
- 检查网络连接和防火墙设置

### 2. 上传文件失败

- 请检查存储桶是否存在
- 检查文件大小是否超过限制
- 检查 MinIO 服务器的存储空间是否足够

### 3. 下载文件失败

- 请检查文件是否存在
- 检查您是否有足够的权限

## 扩展建议

1. **添加文件类型限制**：根据业务需求，限制上传的文件类型
2. **实现文件分片上传**：对于大文件，可以实现分片上传功能
3. **添加文件预览功能**：支持常见文件类型（如图片、视频、文档等）的在线预览
4. **集成用户认证和授权**：添加用户系统，实现细粒度的权限控制
5. **实现文件版本控制**：支持文件的多个版本管理
6. **添加日志记录**：记录文件操作日志，便于审计和问题排查

## License

This project is licensed under the MIT License.