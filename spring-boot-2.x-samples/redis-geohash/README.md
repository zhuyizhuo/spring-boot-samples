# Redis Geo Hash 实现根据经纬度计算距离

## 📋 模块介绍

本模块演示了如何在 Spring Boot 项目中利用 Redis 的 GeoHash 功能实现地理位置计算，包括存储地理位置信息、计算两点间距离以及查找指定范围内的地理位置等功能。

## ✨ 功能特点

- ✅ 地理坐标数据存储与管理
- ✅ 两点间距离计算
- ✅ 查找指定范围内的地理位置
- ✅ 支持 Redis Geo 命令的完整封装
- ✅ 提供单元测试验证功能

## 🛠 技术栈

- Spring Boot 2.x
- Redis (GeoHash 功能)
- Spring Data Redis
- Lombok
- Fastjson
- JUnit

## 📦 依赖配置

核心依赖配置如下：

```xml
<dependencies>
    <!-- Redis 集成 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- 工具类库 -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
        <scope>provided</scope>
    </dependency>
    
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
    </dependency>
    
    <!-- 测试依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 🔧 配置说明

在 `application.yml` 文件中配置 Redis 连接信息：

```yaml
spring:
  redis:
    host: localhost    # Redis 服务器地址
    port: 6379         # Redis 服务器端口
    #password:         # Redis 密码（如果未设置密码则不需要）
    database: 0        # Redis 数据库索引（默认为0）
    timeout: 6000      # 连接超时时间（毫秒）
    
    # 连接池配置
    lettuce:
      pool:
        max-active: 100    # 连接池最大连接数
        max-wait: -1       # 连接池最大阻塞等待时间（使用负值表示没有限制）
        max-idle: 10       # 连接池中的最大空闲连接
        min-idle: 5        # 连接池中的最小空闲连接
```

## 🚀 快速开始

### 环境要求

- JDK 8+ 
- Redis 服务器（默认端口 6379，无需密码）

### 运行示例

1. 确保 Redis 服务器已启动
2. 首先运行单元测试 `GeoServiceTest` 中的 `testSaveCityInfoToRedis` 方法，将测试数据加载至 Redis
3. 依次运行其他单元测试方法，查看输出结果

## 📝 功能示例

### 1. 存储地理位置信息

```java
// 将城市的经纬度信息存储到 Redis 中
geoOperations.geoAdd("city", new Point(116.405285, 39.904989), "北京");
geoOperations.geoAdd("city", new Point(121.472644, 31.231706), "上海");
geoOperations.geoAdd("city", new Point(113.280637, 23.125178), "广州");
geoOperations.geoAdd("city", new Point(114.057868, 22.543099), "深圳");
```

### 2. 计算两点间距离

```java
// 计算北京到上海的距离（单位：千米）
Double distance = geoOperations.geoDist("city", "北京", "上海", RedisGeoCommands.DistanceUnit.KILOMETERS);
System.out.println("北京到上海的距离: " + distance + " 千米");
```

### 3. 查找指定范围内的地理位置

```java
// 查找距离北京1000公里内的所有城市
Circle circle = new Circle(new Point(116.405285, 39.904989), new Distance(1000, RedisGeoCommands.DistanceUnit.KILOMETERS));
GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOperations.geoRadius("city", circle);
```

## 🔍 常见问题

### 1. 连接 Redis 失败
- 确认 Redis 服务是否已启动
- 检查连接地址、端口和密码是否正确
- 确保网络连接通畅，防火墙未阻止连接

### 2. 数据未正确存储或查询
- 检查 Redis 键名是否正确
- 确认经纬度数据格式是否符合要求（经度在前，纬度在后）
- 查看日志是否有错误信息

## 📚 参考资料

- [Redis Geo 命令官方文档](https://redis.io/commands#geo)
- [Spring Data Redis 官方文档](https://docs.spring.io/spring-data/redis/docs/current/reference/html/#redis)
- [GeoHash 算法原理](https://en.wikipedia.org/wiki/Geohash)