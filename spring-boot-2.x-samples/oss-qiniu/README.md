# 七牛云OSS接入示例

<div align="center">
  <img src="https://gw.alicdn.com/tfs/TB1vG9RrAL0gK0jSZFxXXa9pXa-200-200.png" width="120" height="120" alt="七牛云OSS" />
  <p>基于Spring Boot的七牛云OSS集成方案，提供完整的文件存储解决方案</p>
</div>

本模块是基于Spring Boot的七牛云OSS接入示例，提供了文件上传、下载、删除等常用操作的接口实现，以及直观的Web界面进行文件上传测试。

## 功能特性

- 文件上传（支持MultipartFile和InputStream两种方式）
- 生成带过期时间的下载URL
- 文件删除
- 存储空间（Bucket）检查
- 文件列表获取
- 可视化文件上传测试页面（支持拖拽上传和进度显示）
- 启动成功后自动打印访问地址信息

## 技术栈

- Spring Boot 2.7.5
- 七牛云OSS SDK 7.11.0
- Java 1.8

## 快速开始

### 1. 配置七牛云OSS参数

项目中提供了`application.properties.example`文件作为配置模板。请按照以下步骤配置：

1. 复制`src/main/resources/application.properties.example`文件并重命名为`application.properties`
2. 打开`application.properties`文件，填写你的七牛云OSS实际配置信息：

```properties
# Qiniu Cloud Access Key - 从七牛云控制台获取
qiniu.oss.access-key=your_access_key_here

# Qiniu Cloud Secret Key - 从七牛云控制台获取
qiniu.oss.secret-key=your_secret_key_here

# Qiniu Cloud Bucket Name - 你的存储空间名称
qiniu.oss.bucket-name=your_bucket_name_here

# Qiniu Cloud Domain - 你的域名（CDN域名或存储空间域名）
qiniu.oss.domain=https://your_domain.com

# Qiniu Cloud Region - 存储空间所在区域
# Available regions: huadong, huabei, huanan, beimei, xinjiapo
qiniu.oss.region=huadong

# Whether to use HTTPS
qiniu.oss.use-https=true
```

> ⚠️ 注意：必须填写实际的七牛云凭证信息，否则应用将无法正常启动！

### 2. 启动应用

运行`QiniuOssApplication`类的`main`方法启动应用，服务将在`8083`端口启动。

### 3. 访问文件上传测试页面

应用启动后，可以直接访问以下地址使用可视化的文件上传测试页面：

```
http://localhost:8083/oss-qiniu/upload.html
```

该页面提供了直观的文件上传界面，支持拖拽上传、进度显示和上传结果反馈。

### 4. REST API接口说明

除了可视化页面外，还可以通过以下REST API接口访问七牛云OSS服务：

#### 文件上传
- **URL**: `POST /oss-qiniu/api/qiniu-oss/upload`
- **参数**: `file` (文件)
- **返回**: 上传后的文件URL

#### 生成下载URL
- **URL**: `GET /oss-qiniu/api/qiniu-oss/download-url`
- **参数**: `fileName` (文件名), `expireTime` (过期时间，单位：秒，默认3600)
- **返回**: 带过期时间的下载URL

#### 文件删除
- **URL**: `DELETE /oss-qiniu/api/qiniu-oss/delete`
- **参数**: `fileName` (文件名)
- **返回**: 删除结果

#### 获取文件列表
- **URL**: `GET /oss-qiniu/api/qiniu-oss/list`
- **参数**: `bucketName` (存储空间名称，可选), `prefix` (文件前缀，可选), `limit` (限制数量，默认100)
- **返回**: 文件列表

#### 检查存储空间是否存在
- **URL**: `GET /oss-qiniu/api/qiniu-oss/bucket/exists`
- **参数**: `bucketName` (存储空间名称)
- **返回**: 检查结果

## 代码结构

```
├── src/main/java/com/github/zhuyizhuo/sample/oss/
│   ├── QiniuOssApplication.java        # 应用启动类
│   ├── config/                         # 配置类
│   │   └── QiniuOssConfig.java         # 七牛云OSS配置
│   ├── controller/                     # 控制器
│   │   └── QiniuOssController.java     # REST接口控制器
│   └── service/                        # 服务层
│       ├── QiniuOssService.java        # 服务接口
│       └── impl/                       # 服务实现
│           └── QiniuOssServiceImpl.java # 服务实现类
└── src/main/resources/
    └── application.properties          # 应用配置文件
```

## 注意事项

1. 请确保已在七牛云官网注册账号并创建存储空间。
2. 配置文件中的`access-key`和`secret-key`请使用自己账号的凭证。
3. 域名配置请根据实际情况设置，可以是七牛云提供的测试域名或自定义域名。
4. 区域选择请根据您创建的存储空间所在的区域设置，支持的区域包括：华东(huadong)、华北(huabei)、华南(huanan)、北美(beimei)、东南亚(xinjiapo)等。

## 参考文档

- [七牛云OSS Java SDK官方文档](https://developer.qiniu.com/kodo/1239/java)
- [Spring Boot官方文档](https://docs.spring.io/spring-boot/docs/2.7.5/reference/html/)