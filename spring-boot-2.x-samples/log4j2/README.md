## spring-boot 集成 log4j2 日志
### 功能
1. 日志统一 ID 打印
2. 日志格式示例
3. 控制台彩色日志
4. 自定义日志文件路径

### 使用 log4j2 
1. 排除默认日志
```pom
<!-- 排除默认日志配置 -->
<exclusions>
    <exclusion>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-logging</artifactId>
    </exclusion>
</exclusions>
```
2. 引入 log4j2 依赖
```pom
<!-- 引入log4j2依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

3. 增加 JVM 启动参数(2.10版本以后，Log4j2默认关闭了Jansi)
-Dlog4j.skipJansi=false

### 项目演示
1. 启动 Log4j2Application
2. 访问 http://localhost:8080/log/demo,观察日志输出


### 参考链接
#### 官网 PatternLayout
 - https://logging.apache.org/log4j/2.x/manual/layouts.html#PatternLayout 
#### 官网 Appenders
 - https://logging.apache.org/log4j/2.x/manual/appenders.html