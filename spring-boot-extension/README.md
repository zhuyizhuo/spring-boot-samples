# Spring Boot 扩展点示例项目

## 项目介绍

本项目是一个展示Spring Boot各种扩展点功能的示例应用。通过这个项目，您可以学习和了解Spring Boot提供的丰富扩展机制，以及如何利用这些扩展点来自定义和增强Spring Boot应用的行为。

## 已实现的扩展点

项目实现了以下Spring Boot核心扩展点：

### 1. 应用上下文初始化扩展
- **ApplicationContextInitializer** - 应用上下文初始化前的回调接口
- **EnvironmentPostProcessor** - 环境变量处理扩展点

### 2. Bean定义扩展
- **BeanDefinitionRegistryPostProcessor** - Bean定义注册后处理
- **BeanFactoryPostProcessor** - Bean工厂后处理器
- **ImportBeanDefinitionRegistrar** - 导入Bean定义注册器
- **FactoryBean** - 工厂Bean实现

### 3. Bean生命周期扩展
- **BeanPostProcessor** - Bean后置处理器
- **InstantiationAwareBeanPostProcessor** - 实例化感知Bean后置处理器
- **SmartInstantiationAwareBeanPostProcessor** - 智能实例化感知Bean后置处理器

### 4. Bean生命周期回调
- **InitializingBean** - Bean初始化回调接口
- **BeanFactoryAware** - BeanFactory感知接口
- **BeanNameAware** - Bean名称感知接口

### 5. 应用事件监听
- **ApplicationListener** - 应用事件监听器
- **SmartApplicationListener** - 智能应用事件监听器
- **SmartInitializingSingleton** - 智能初始化单例Bean回调

### 6. 应用启动扩展
- **CommandLineRunner** - 命令行运行器
- **ApplicationRunner** - 应用运行器

### 7. 条件装配
- **Condition** - 条件装配接口

### 8. Web控制器
- **ExtensionController** - 提供REST API端点，解决Whitelabel Error Page问题
  - `/` - 应用首页，展示基本信息和可用端点
  - `/hello` - 简单问候接口
  - `/status` - 应用状态接口

## 项目结构

```
src/main/java/com/zhuo/test/
├── ExtensionConfiguration.java            # 扩展配置类
├── ExtensionExampleConfig.java            # 示例配置类
├── TestApplicationContextInitializer.java # 应用上下文初始化器
├── TestApplicationListener.java           # 应用事件监听器
├── TestApplicationRunner.java             # 应用运行器
├── TestBeanDefinitionRegistryPostProcessor.java # Bean定义注册后处理器
├── TestBeanFactoryAware.java              # BeanFactory感知实现
├── TestBeanFactoryPostProcessor.java      # Bean工厂后处理器
├── TestBeanNameAware.java                 # Bean名称感知实现
├── TestBeanPostProcessor.java             # Bean后置处理器
├── TestCommandLineRunner.java             # 命令行运行器
├── TestCondition.java                     # 条件装配实现
├── TestEnvironmentPostProcessor.java      # 环境变量后处理器
├── TestFactoryBean.java                   # 工厂Bean实现
├── TestImportBeanDefinitionRegistrar.java # 导入Bean定义注册器
├── TestInitializingBean.java              # 初始化Bean实现
├── TestInstantiationAwareBeanPostProcessor.java # 实例化感知Bean后置处理器
├── TestSmartApplicationListener.java      # 智能应用事件监听器
├── TestSmartInitializingSingleton.java    # 智能初始化单例Bean实现
├── TestSmartInstantiationAwareBeanPostProcessor.java # 智能实例化感知Bean后置处理器
├── ExtensionController.java               # Web控制器（新增）
└── ZhuoApplication.java                   # 应用主类
```

## 配置说明

项目的主要配置位于 `src/main/resources/application.yml` 文件中，包括：

- 应用基本信息配置
- 扩展点示例配置
- 服务器配置（端口：8080，上下文路径：/extension）
- 日志配置
- 示例运行器配置
- 工厂Bean配置

## 如何运行

### 前提条件
- JDK 8或更高版本
- Maven 3.6或更高版本

### 编译项目

```bash
cd spring-boot-extension
mvn clean compile
```

### 启动应用

```bash
mvn spring-boot:run
```

或构建JAR包后运行：

```bash
mvn package
java -jar target/spring-boot-extension-1.0.0.jar
```

## 访问应用

应用启动后，可以通过以下地址访问：

- 首页：http://localhost:8080/extension/
- 问候接口：http://localhost:8080/extension/hello
- 状态接口：http://localhost:8080/extension/status

## 最新更新

1. **修复了访问地址格式问题**：修正了TestCommandLineRunner中IP地址访问链接的格式，确保生成标准的URL格式
2. **添加了Web控制器**：创建了ExtensionController类，提供REST API端点，解决了Whitelabel Error Page问题
3. **更新了README文档**：添加了详细的项目说明、扩展点列表、运行指南等信息

## 注意事项

- 本项目使用Spring Boot 2.7.5版本
- 开发环境：建议使用IntelliJ IDEA或Eclipse等IDE进行开发
- 运行时如有问题，请检查控制台日志获取详细错误信息

## 贡献说明

欢迎提交Issue和Pull Request来改进这个项目。在贡献代码前，请确保您的代码符合项目的代码风格和质量要求。