## 第一步 执行 database_init.sql 文件, 并且将数据库配置改为自己的数据库配置

## 第二步 启动项目

## 第三步 访问 http://localhost:8080 

### 使用账号 mic 密码 123 登陆

- 访问 http://localhost:8080/user/info/p1  可正常访问
- 访问 http://localhost:8080/user/info/p2  无权限

### 访问路径 http://localhost:8080/logout 退出

### 使用账号 jack 密码 123 登陆

- 访问 http://localhost:8080/user/info/p1  无权限
- 访问 http://localhost:8080/user/info/p2  可正常访问



## 其他功能

可通过配置控制不需要过滤的请求路径，如下

```yaml
auth:
  ignore: /login
```

