如果多个资源目录同时存在，则会按照以下优先级获取资源：
/META-INF/resources > resources > static > public
文件在高优先级目录中不存在,则会继续在下一个目录中继续查找

1. 启动项目并访问如下 url 验证:
http://localhost:8081/avatar.png
http://localhost:8081/customize.txt
http://localhost:8081/demo.txt
http://localhost:8081/demofile.txt

可删除 /META-INF/resources 下的 demo.txt 继续访问
http://localhost:8081/demo.txt
查看结果，验证目录的优先级。
依此类推其他目录。

2. application.properties 中配置 customize.upload-path 为本地目录，在目录中创建一个 local.txt 文件，内容为 Hello world. 
访问 http://localhost:8081/local.txt 查看是否显示文件内容。