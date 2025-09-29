# Spring Boot 集成阿里云 OSS 示例

本模块提供了 Spring Boot 集成阿里云 OSS（对象存储服务）的完整示例代码，包括文件上传、下载、删除和生成预签名 URL 等功能。

## 功能特性

- 文件上传到阿里云 OSS
- 文件删除
- 生成临时访问的预签名 URL
- 支持流式文件上传

## 配置说明

在使用前，需要在 `application.yml` 文件中配置阿里云 OSS 的连接信息：

```yaml
# 阿里云OSS配置
aliyun:
  oss:
    endpoint: your-oss-endpoint # 例如：oss-cn-hangzhou.aliyuncs.com
    accessKeyId: your-access-key-id
    accessKeySecret: your-access-key-secret
    bucketName: your-bucket-name
    dirPrefix: upload/ # 上传文件的目录前缀
    domain: https://your-bucket-name.oss-cn-hangzhou.aliyuncs.com/ # 访问域名
```

请将上述配置项替换为您自己的阿里云 OSS 账号信息。

## 接口说明

### 文件上传

```
POST /api/oss/upload
```

**请求参数**：
- `file`：要上传的文件（MultipartFile）

**返回示例**：
```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": "https://your-bucket-name.oss-cn-hangzhou.aliyuncs.com/upload/xxxxxxxxxxxxxxxxxxxx.jpg"
}
```

### 文件删除

```
DELETE /api/oss/delete
```

**请求参数**：
- `fileName`：要删除的文件名（OSS 上的文件名，包含路径）

**返回示例**：
```json
{
  "code": 200,
  "message": "文件删除成功"
}
```

### 生成预签名 URL

```
GET /api/oss/presigned-url
```

**请求参数**：
- `fileName`：文件名称
- `expireTime`：过期时间（秒，默认 3600 秒）

**返回示例**：
```json
{
  "code": 200,
  "message": "预签名URL生成成功",
  "data": "https://your-bucket-name.oss-cn-hangzhou.aliyuncs.com/upload/xxxxxxxxxxxxxxxxxxxx.jpg?OSSAccessKeyId=xxxx&Expires=xxxxx&Signature=xxxxx"
}
```

## 代码结构

```
src/main/java/com/github/zhuyizhuo/sample/oss/
├── OssApplication.java          # 应用程序入口
├── config/
│   └── OssConfig.java           # OSS 配置类
├── service/
│   ├── OssService.java          # OSS 服务接口
│   └── impl/
│       └── OssServiceImpl.java  # OSS 服务实现
└── controller/
    └── OssController.java       # REST 控制器
```

## 注意事项

1. 请确保您的阿里云账号有足够的权限操作 OSS
2. 配置文件中的 AccessKeyId 和 AccessKeySecret 属于敏感信息，请妥善保管
3. 文件上传有大小限制，可在 `application.yml` 中配置 `spring.servlet.multipart.max-file-size` 和 `spring.servlet.multipart.max-request-size`
4. 生成的预签名 URL 有有效期限制，过期后需要重新生成
5. 在生产环境中，建议使用 IAM 角色或 STS Token 等更安全的认证方式代替直接配置 AccessKey