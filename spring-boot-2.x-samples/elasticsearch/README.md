# Spring Boot 集成 Elasticsearch

## 📋 模块介绍

本模块演示了在 Spring Boot 项目中集成 Elasticsearch 的实现方法，提供了基于配置文件的客户端管理、高级查询操作和工具类封装，方便开发者快速实现 Elasticsearch 数据操作功能。

## ✨ 功能特点

- ✅ 基于配置文件的 Elasticsearch 客户端配置
- ✅ 支持多节点集群连接
- ✅ 支持安全认证配置
- ✅ 连接池和超时参数优化
- ✅ 完整的文档 CRUD 操作工具类
- ✅ 灵活的查询条件构造和结果处理
- ✅ 无需 Spring Boot 上下文的独立使用能力

## 🛠 技术栈

- Spring Boot 2.x
- Elasticsearch 7.17.0
- Elasticsearch High Level REST Client
- Jackson 2.14.2 (JSON 处理)

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
    
    <!-- 日志组件 -->
    <dependency>
        <groupId>commons-logging</groupId>
        <artifactId>commons-logging</artifactId>
        <version>1.2</version>
    </dependency>
</dependencies>
```

## 🔧 使用指南

### 1. 配置 Elasticsearch 连接

在项目的 `src/main/resources` 目录下创建 `application.properties` 文件，并配置以下信息：

```properties
# Elasticsearch 连接配置
elasticsearch.urls=localhost
elasticsearch.port=9200
elasticsearch.scheme=http

# 认证配置（可选）
elasticsearch.username=
elasticsearch.password=

# 超时配置
elasticsearch.connection-timeout=10000
elasticsearch.socket-timeout=30000

# 连接池配置
elasticsearch.max-total-connections=30
elasticsearch.default-max-per-route=10
```

**多节点配置示例：**
```properties
elasticsearch.urls=es-node1,es-node2,es-node3
```

### 2. 客户端使用示例

#### 直接使用 ESClientConfig

```java
import com.github.zhuyizhuo.config.ESClientConfig;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticsearchDemo {
    public static void main(String[] args) {
        try (RestHighLevelClient client = ESClientConfig.createClient()) {
            // 使用客户端执行操作...
            System.out.println("客户端连接成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

#### 使用 EsClientUtil 工具类

```java
import com.github.zhuyizhuo.util.EsClientUtil;

public class EsUtilDemo {
    public static void main(String[] args) {
        try {
            // 搜索示例
            String result = EsClientUtil.searchWithFilterAndSort(
                "your-index", 
                "field:value", 
                "sort-field:desc", 
                1, 
                10
            );
            System.out.println("搜索结果: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭客户端资源
            EsClientUtil.closeClient();
        }
    }
}
```

### 3. 工具类功能介绍

- `ElasticsearchUtil` - 提供完整的索引管理和文档操作功能
- `EsClientUtil` - 简化的客户端工具，提供常用查询操作的封装

## 🚀 快速开始

### 准备工作

1. 确保您已经安装并运行了 Elasticsearch 服务（版本兼容 7.17.0）
2. 按照上述方法配置 `application.properties`
3. 使用提供的示例代码进行连接测试

### 项目结构

```
src/main/java/com/github/zhuyizhuo/
├── config/
│   └── ESClientConfig.java     # 客户端配置类
├── util/
│   ├── ElasticsearchUtil.java  # 核心工具类
│   └── EsClientUtil.java       # 简化工具类
├── TestES.java                 # 示例程序
└── TestEsClientUtil.java       # 工具类测试程序
```

### 核心类说明

- **ESClientConfig** - 从配置文件加载连接参数，创建和配置 RestHighLevelClient 实例
- **ElasticsearchUtil** - 提供完整的 Elasticsearch 操作方法，包括索引管理和文档 CRUD
- **EsClientUtil** - 提供简化的常用操作，方便快速集成使用

## 📝 最佳实践

1. **单例模式**：在应用中维护一个客户端实例，避免频繁创建和关闭
2. **异常处理**：妥善处理客户端操作可能出现的异常
3. **资源管理**：使用 try-with-resources 确保客户端资源正确关闭
4. **版本兼容性**：确保客户端版本与 Elasticsearch 服务端版本兼容
5. **连接池优化**：根据业务需求调整连接池和超时参数

## 📚 参考资料

- [Elasticsearch 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Elasticsearch Java High Level REST Client 文档](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html)
- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
