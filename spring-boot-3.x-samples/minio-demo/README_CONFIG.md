# MinIO Demo 配置说明

## 敏感信息处理方式

为了保护敏感信息（如MinIO服务器地址、访问密钥和密钥）不被提交到版本控制，本项目采用以下配置策略：

### 1. 外部配置文件

敏感信息应放置在外部配置文件 `application-minio.yml` 中，该文件**不应该被提交到版本控制**。

### 2. Git忽略规则

项目根目录的 `.gitignore` 文件已包含以下规则，确保包含敏感信息的配置文件不会被提交：
```
**/src/main/resources/application-local.yml
**/src/main/resources/application-*.yml
**/src/main/resources/application-*.properties
```

### 3. 环境变量支持

`application-minio.yml` 文件支持从环境变量中读取配置，格式如下：
```yaml
minio:
  url: ${MINIO_URL:http://localhost:9000}
  accessKey: ${MINIO_ACCESS_KEY:minioadmin}
  secretKey: ${MINIO_SECRET_KEY:minioadmin}
  bucketName: ${MINIO_BUCKET_NAME:minio-demo}
```

这意味着你可以：
- 直接在 `application-minio.yml` 中设置值
- 或通过环境变量覆盖这些值

## 本地开发设置步骤

1. 确保 `application-minio.yml` 文件存在于 `src/main/resources` 目录中
2. 在该文件中配置你的MinIO服务器信息
3. 启动应用程序

## 配置验证

应用启动时会验证必要的配置项，如果缺少必要的配置，会抛出明确的错误信息，指导你如何设置这些配置。

## 注意事项

- 不要将 `application-minio.yml` 添加到版本控制中
- 不要在 `minio.properties` 文件中存储敏感信息
- 在生产环境中，建议使用环境变量或密钥管理服务来管理敏感信息