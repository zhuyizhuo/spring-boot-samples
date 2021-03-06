package com.github.zhuyizhuo.samples.spring.security.jdbc.config;

import com.github.zhuyizhuo.samples.spring.security.jdbc.dto.MenuDto;
import com.github.zhuyizhuo.samples.spring.security.jdbc.service.MenuService;
import com.github.zhuyizhuo.samples.spring.security.jdbc.service.UserService;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.util.List;

/**
 * Spring Security 配置
 * @author zhuo
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements EnvironmentAware {
    private Environment environment;

    final UserService userService;
    final MenuService menuService;

    public SecurityConfig(UserService userService, MenuService menuService) {
        this.userService = userService;
        this.menuService = menuService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 请求地址不需要拦截的话 直接过滤掉该地址，即该地址不走 Spring Security 过滤器链
        String property = environment.getProperty("auth.ignore");
        String[] split = StringUtils.tokenizeToStringArray(property, ",");
        web.ignoring().antMatchers(split);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //开启登录配置
        List<MenuDto> menuList = menuService.queryMenu();
        http.authorizeRequests()
            //访问此地址就不需要进行身份认证了，防止重定向死循环
            .antMatchers("/login.html").permitAll();

        if (menuList.size() > 0){
            for (int i = 0, size = menuList.size(); i < size; i++) {
                MenuDto menuDto = menuList.get(i);
                http.authorizeRequests()
                        .antMatchers(menuDto.getUrl()).hasAuthority(menuDto.getPerms());
            }
        }
        http.authorizeRequests()
                //表示剩余的其他接口，登录之后就能访问
                .anyRequest().authenticated();

        http.formLogin()
                 // 处理来自该链接的登陆请求 对应表单提交的 action
                .loginProcessingUrl("/doLogin")
                // 自定义登陆成功的页面地址
                .successForwardUrl("/login/login-success")
                //定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
                .loginPage("/login.html")
                //登录处理接口
                //定义登录时，用户名的 key，默认为 username
//                .usernameParameter("username")
                //定义登录时，用户密码的 key，默认为 password
//                .passwordParameter("password")
                //登录成功的处理器  不配置此处理器 登陆成功后访问原来访问路径 配置后 登陆成功后执行处理器内容
                //    即 配置了handler 了  则  successForwardUrl 失效
//                .successHandler((httpServletRequest,httpServletResponse, authentication) -> {
//                        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                        PrintWriter out = httpServletResponse.getWriter();
//                        out.write("success");
//                        out.flush();
//                })
                .failureHandler((httpServletRequest,httpServletResponse, authentication) -> {
                        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        PrintWriter out = httpServletResponse.getWriter();
                        out.write("login fail");
                        out.flush();
                })
                //和表单登录相关的接口统统都直接通过
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
//               配置了 handler 则  logoutUrl 失效
//                .logoutSuccessHandler((httpServletRequest,httpServletResponse, authentication) -> {
//                        httpServletResponse.setContentType("application/json;charset=utf-8");
//                        PrintWriter out = httpServletResponse.getWriter();
//                        out.write("logout success");
//                        out.flush();
//                })
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        String encode = new BCryptPasswordEncoder().encode("123");
        System.out.println(encode);
    }
}
