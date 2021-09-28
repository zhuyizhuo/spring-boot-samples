# 自定义 spring boot starter
1、发包
2、新建 springboot 项目并引入本 starter, 添加如下依赖
```xml
<dependency>
    <groupId>com.zhuo</groupId>
    <artifactId>demo-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
3、新增配置
```
zhuo.demo.enabled=true
zhuo.demo.name=james
```
4、启动 spring boot 项目 看控制台输出