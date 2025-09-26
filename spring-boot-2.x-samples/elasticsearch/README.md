# Spring Boot 集成 Elasticsearch

## 📋 模块介绍

本模块演示了在 Spring Boot 项目中集成 Elasticsearch 的实现方法，提供了 Elasticsearch 的基本查询和操作功能。

## ✨ 功能特点

- ✅ Elasticsearch 客户端配置
- ✅ 基本的文档查询操作
- ✅ 支持不同版本的 Elasticsearch（当前配置为 7.17.0）

## 🛠 技术栈

- Spring Boot 2.x
- Elasticsearch 7.17.0
- Elasticsearch High Level REST Client
- Jackson 数据处理

## 📦 依赖配置

核心依赖配置如下：

```xml
<dependencies>
    <!-- Elasticsearch High Level REST Client -->
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-high-level-client</artifactId>
        <version>7.17.0</version>
    </dependency>
    
    <!-- Elasticsearch Low Level REST Client -->
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-client</artifactId>
        <version>7.17.0</version>
    </dependency>
    
    <!-- Jackson 用于 JSON 处理 -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.14.2</version>
    </dependency>
</dependencies>
```

## 🔧 使用指南

### 1. 配置 Elasticsearch 连接

请确保正确配置 Elasticsearch 连接信息。虽然当前代码中的 ESClientConfig 文件路径有问题，但您需要在实际使用时创建正确的配置文件。

### 2. 基本查询操作

1. 修改查询索引为您实际使用的 Elasticsearch 索引
2. 通过单元测试或创建应用程序类运行查询操作

## 🚀 快速开始

### 准备工作

1. 确保您已经安装了 Elasticsearch 服务
2. 确认 Elasticsearch 版本与项目中配置的版本兼容（当前为 7.17.0）
3. 创建相应的索引和文档数据

### 注意事项

- 当前模块的代码路径可能存在问题，请根据实际情况调整包路径和类名
- 确保 Elasticsearch 服务可访问，并具有正确的网络配置
- 根据您的 Elasticsearch 版本，可能需要调整相关依赖的版本号

## 📚 参考资料

- [Elasticsearch 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Elasticsearch Java High Level REST Client 文档](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html)
