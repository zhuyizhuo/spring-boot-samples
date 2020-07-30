package com.github.zhuyizhuo.samples.spring.security.config;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zhuo
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements EnvironmentAware {
    private Environment environment;
//    VerifyCodeFilter
    /** 在内存中配置用户信息 */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("james")
                .roles("admin")
                // 123 的密文
                .password("$2a$10$dCUxPoQcTlYNfdQRPIM3/uPK47b3HM69RyvbeiQS7LNPB6REFI9DS");
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
//        http.addFilterBefore(null, UsernamePasswordAuthenticationFilter.class)
                //开启登录配置
                http.authorizeRequests()
                //表示访问 /hello 这个接口，需要具备 admin 这个角色
                .antMatchers("/hello").hasRole("admin")
                //访问此地址就不需要进行身份认证了，防止重定向死循环
                .antMatchers("/login.html").permitAll()
                 //表示剩余的其他接口，登录之后就能访问
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/doLogin")
                //定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
                .loginPage("/login.html")
                //登录处理接口
                //定义登录时，用户名的 key，默认为 username
                .usernameParameter("uname")
                //定义登录时，用户密码的 key，默认为 password
                .passwordParameter("passwd")
                //登录成功的处理器  不配置此处理器 登陆成功后访问原来访问路径 配置后 登陆成功后执行处理器内容
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        PrintWriter out = httpServletResponse.getWriter();
                        out.write("success");
                        out.flush();
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        PrintWriter out = httpServletResponse.getWriter();
                        out.write("fail");
                        out.flush();
                    }
                })
                //和表单登录相关的接口统统都直接通过
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        httpServletResponse.setContentType("application/json;charset=utf-8");
                        PrintWriter out = httpServletResponse.getWriter();
                        out.write("logout success");
                        out.flush();
                    }
                })
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        String encode = new BCryptPasswordEncoder().encode("123");
        System.out.println(encode);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
