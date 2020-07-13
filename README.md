# Spring Boot 基础教程（2.x 版本）

## 项目简介
本项目是一个用来深度学习并实战 `spring boot` 的项目，目前已完成 **`7`** 个集成 sample，正在集成的有 **`3`** 个。

> 如果大家需要集成其他 sample，也可在 [issue](https://github.com/zhuyizhuo/spring-boot-samples/issues/new) 里提需求。我会添加在 [TODO](./TODO.md) 列表里，并在短时间内集成进本项目。

**如何支持**：
1. 点个`Star`并`Follow`我
2. 把该仓库分享给更多的朋友

## 所需环境
- JDK 1.8 +
- Maven 3.5 +
- IntelliJ IDEA
- Mysql 5.7 以上
- 个别项目使用了 lombok 需安装 lombok 插件(本项目将会慢慢去 lombok 化,方便不使用 lombok 的同学)

## 教程目录（2.x版本）

连载中...Star关注支持一下，随时获得更新信息！

### 博客教程

- [Spring Boot 2.x教程：静态资源访问](http://zhuyizhuo.online/2020/06/11/spring-boot-resources-visit/)
- [Spring Boot 2.x教程：文档生成工具 swagger](http://zhuyizhuo.online/2020/06/17/spring-boot-swagger/)
- [Spring Boot 2.x教程：数据库版本管理工具 Flyway](http://zhuyizhuo.online/2020/06/21/spring-boot-flyway-database-version-control/)
- [Spring Boot 2.x教程：数据库版本管理工具 Liquibase](http://zhuyizhuo.online/2020/07/04/spring-boot-liquibase-database-version-controller/)

### Web开发
- [前后端通信有字段需要加解密你会如何处理？](http://zhuyizhuo.online/2020/07/12/spring-boot-jackson/)

## 各 Module 介绍

| Module 名称                                                  | Module 介绍                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [spring-boot-samples-flyway](./spring-boot-2.x-samples/spring-boot-samples-flyway) | spring-boot 集成数据库版本管理工具 Flyway |
| [spring-boot-samples-resources](./spring-boot-2.x-samples/spring-boot-samples-resources) | spring-boot 访问内部或者外部磁盘静态资源 |
| [spring-boot-samples-log4j2](./spring-boot-2.x-samples/spring-boot-samples-log4j2)     | spring-boot 集成 log4j2 实现全局日志统一 ID 打印 |
| [spring-boot-samples-swagger](./spring-boot-2.x-samples/spring-boot-samples-swagger) | spring-boot 集成 swagger 生成在线文档 |
| [spring-boot-samples-jackson](./spring-boot-2.x-samples/spring-boot-samples-jackson) | spring-boot 使用自定义 jackson、filter、自定义 requestWrapper 实现自动序列化反序列化出参入参 |
| [spring-boot-samples-liquibase](./spring-boot-2.x-samples/spring-boot-samples-liquibase) | spring-boot 集成数据库版本管理工具 Liquibase |
| [spring-boot-samples-ldap](./spring-boot-2.x-samples/spring-boot-samples-ldap) | spring-boot 集成轻型目录访问协议 ldap |

## 推荐内容

- [我的博客](http://zhuyizhuo.online/)：分享平时学习和实践过的技术内容
- [代码生成器](http://zhuyizhuo.online/code-generator-doc/)：轻量级可扩展的代码生成器
- [GitHub](https://github.com/zhuyizhuo/spring-boot-samples)：Star 支持一下呗
- [Gitee](https://gitee.com/zhuyizhuo/spring-boot-samples)：Star 支持一下呗

## License

本项目采用 [SATA License](https://github.com/zTrix/sata-license)

> 你不知道这代表什么？简单来说，就是如果你使用了该项目，你需要马不停蹄地点一个 `Star`，接下来你便可以随意使用它。