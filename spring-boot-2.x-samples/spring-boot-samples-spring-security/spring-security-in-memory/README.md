spring security 默认资源文件位置:
```
src/main/resources/public/findme.txt
src/main/resources/static/findme.txt
src/main/resources/resources/findme.txt - (yes, resources^2!)
src/main/resources/findme.txt - not found!!
```

参考链接: https://spring.io/blog/2013/12/19/serving-static-web-content-with-spring-boot

SpringBoot 默认允许访问资源文件位置
`/css/**`, `/js/**`, `/images/**`, and `/**/favicon.ico`。 
You could, for example, have a file named 
`src/main/resources/public/images/hello.jpg` and, without adding any extra configuration,
 it would be accessible at `http://localhost:8080/images/hello.jpg`