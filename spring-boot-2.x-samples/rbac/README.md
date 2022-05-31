## 使用 Interceptor 实现权限认证
1. 启动 RBACApplication
2. 依次访问以下地址验证权限
 - 不需登陆可访问
    - http://localhost:8080/menu/anonymous
 - 登陆后可访问 
    - http://localhost:8080/menu/login
 - 无论是否登陆都不可访问 
    - http://localhost:8080/menu/permission
3. 访问登陆接口
 - http://localhost:8080/sys/login
 请求参数格式如下,值可随意传入
```json 
 {
     "username":"james",
     "password":"123"
 }
```
再次访问第二步的网址验证权限