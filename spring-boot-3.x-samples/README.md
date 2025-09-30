# Spring Boot 3.x 示例集合

这是一个Spring Boot 3.x的示例项目集合，包含各种常见场景的Spring Boot应用示例。

## 项目结构
本项目采用多模块Maven结构，每个模块都是一个独立的Spring Boot示例应用。

```
spring-boot-3.x-samples/
├── hello-world/           # Hello World基础示例
├── pom.xml                # 父POM文件，定义公共依赖和版本管理
└── README.md              # 项目说明文档
```

## 技术栈
- Java 17
- Spring Boot 3.2.0
- Maven

## 示例模块列表
| 模块名称 | 说明 | 主要功能 |
|---------|------|---------|
| [hello-world](hello-world) | Spring Boot 3.x基础入门示例 | 简单REST API，展示Spring Boot基础功能 |
| （持续更新中...） | | |

## 如何使用

### 前提条件
- JDK 17或更高版本
- Maven 3.8或更高版本
- IDE（推荐IntelliJ IDEA、Eclipse或Spring Tool Suite）

### 构建整个项目
```bash
# 进入项目根目录
cd spring-boot-3.x-samples

# 构建所有模块
mvn clean install
```

### 运行单个示例模块
每个示例模块都是一个独立的Spring Boot应用，可以单独运行：

```bash
# 进入特定模块目录
cd spring-boot-3.x-samples/hello-world

# 运行应用
mvn spring-boot:run
```

## Spring Boot 3.x 主要特性
- **JDK 17+ 支持**：要求Java 17或更高版本
- **Jakarta EE 9/10**：基于Jakarta EE标准，包名从javax.*变更为jakarta.*
- **性能改进**：启动时间和内存使用都有优化
- **GraalVM原生镜像支持**：可以编译为原生镜像以获得更好的性能
- **SpringDoc OpenAPI**：替代Spring Boot 2.x中的SpringFox Swagger
- **Java模块系统支持**：更好地支持Java 9+的模块系统
- **新的deprecation策略**：更清晰的API生命周期管理

## 与Spring Boot 2.x的主要区别
1. **Java版本要求**：Spring Boot 3.x要求JDK 17+，而Spring Boot 2.x支持JDK 8+（部分版本支持到JDK 11+）
2. **Jakarta EE迁移**：从javax.*包迁移到jakarta.*包
3. **依赖升级**：升级了各种依赖库的版本
4. **移除了一些旧API**：删除了Spring Boot 2.x中已弃用的API
5. **文档系统变更**：从SpringFox Swagger变更为SpringDoc OpenAPI

## 贡献指南
如果你对本项目有任何建议或想要贡献新的示例模块，欢迎提交Pull Request或Issue。

## License
本项目采用MIT许可证 - 详见LICENSE文件