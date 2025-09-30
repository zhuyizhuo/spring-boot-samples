# Spring Boot 3.x Hello World示例

这是一个基于Spring Boot 3.x的Hello World示例项目，展示了Spring Boot 3.x的基础用法及参数传递方式。本项目包含基础API、多参数测试、对象参数测试等功能，并提供了可直接点击验证的Web页面。

## 技术栈
- Java 17
- Spring Boot 3.2.0
- Maven
- SpringDoc OpenAPI (替代Spring Boot 2.x中的Swagger)

## 功能说明
本示例包含以下REST API端点：
- `/api/hello` - 返回固定的问候语
- `/api/greeting` - 可接受name参数，返回个性化的问候语
- `/api/multi-params` - 展示多参数传递示例
- `/api/user-info-get` - 展示GET请求传递对象参数
- `/api/user-info-post` - 展示POST请求传递JSON对象参数

此外，项目还提供了一个Web测试页面，可直接点击按钮测试所有API功能。

## 如何运行

### 方法一：使用Maven命令行
```bash
# 进入项目根目录
cd spring-boot-3.x-samples/hello-world

# 构建项目
mvn clean package

# 运行应用
java -jar target/hello-world-1.0-SNAPSHOT.jar
```

### 方法二：直接在IDE中运行
在IDE中找到`HelloWorldApplication`类，右键点击并选择"Run As" -> "Java Application"

应用启动后，控制台会输出访问地址信息。

## 访问API
应用启动后，可以通过以下URL访问API：

- Hello API: http://localhost:8081/hello-world/api/hello
- Greeting API: http://localhost:8081/hello-world/api/greeting?name=John
- 多参数API: http://localhost:8081/hello-world/api/multi-params?name=John&age=30&city=Beijing
- GET对象参数API: http://localhost:8081/hello-world/api/user-info-get?username=John&email=john@example.com&age=30&address=Beijing
- POST对象参数API: http://localhost:8081/hello-world/api/user-info-post (需要发送JSON格式数据)

## 测试页面
项目提供了一个Web测试页面，可以直接点击按钮测试所有API：
- 测试页面: http://localhost:8081/hello-world/

## 查看API文档
Spring Boot 3.x使用SpringDoc OpenAPI代替了Spring Boot 2.x中的SpringFox Swagger。
可以通过以下URL访问自动生成的API文档：
- Swagger UI: http://localhost:8081/hello-world/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/hello-world/api-docs

## Spring Boot 3.x 主要变化
- 要求Java 17或更高版本
- 基于Jakarta EE 9/10 (包名从javax.* 变为jakarta.*)
- 移除了对一些旧技术的支持
- 改进了性能和内存使用
- 使用SpringDoc OpenAPI替代SpringFox Swagger
- 支持GraalVM原生镜像
- 增强了对Java模块系统的支持

## 其他说明
本示例是Spring Boot 3.x示例集合中的基础入门示例，后续会添加更多功能丰富的示例模块。