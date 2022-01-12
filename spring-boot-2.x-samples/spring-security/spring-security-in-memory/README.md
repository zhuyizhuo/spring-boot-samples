## 内存中配置用户名密码

```java
    @Override
    @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("zhangsan").password("321").authorities("p1").build());
        manager.createUser(User.withUsername("jack").password("1234").authorities("p2").build());
        return manager;
    }
```

### 配置权限

```java
@Override
    protected void configure(HttpSecurity http) throws Exception {
            //开启登录配置
            http.authorizeRequests()
                //表示访问 /user/hello 这个接口，需要具备 p1 这个权限
                .antMatchers("/user/hello").hasAuthority("p1")
                .antMatchers("/user/info").hasAuthority("p2")
                //访问此地址就不需要进行身份认证了，防止重定向死循环
                .antMatchers("/login.html").permitAll()
                 //表示剩余的其他接口，登录之后就能访问
                .anyRequest().authenticated()
                .and()
                .formLogin()
                 // 处理来自该链接的登陆请求 对应表单提交的 action
                .loginProcessingUrl("/login")
                // 自定义登陆成功的页面地址
                .successForwardUrl("/login-success")
                //定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
                .loginPage("/login.html")
                    .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }
```

### 验证权限访问

启动项目，访问  http://localhost:8080/

- 使用 `zhangsan`，密码 `321` 登陆
  - 访问 http://localhost:8080/user/info ，403 无权限
  - 访问 http://localhost:8080/user/hello ，可正常访问

- 访问 http://localhost:8080/logout  退出登录

- 使用 `jack` ，密码 `1234` 登陆

  - 访问 http://localhost:8080/user/info ，可正常访问
  - 访问 http://localhost:8080/user/hello ，403 无权限

  

## spring security 默认资源文件位置:
以 `findme.txt` 为例：

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