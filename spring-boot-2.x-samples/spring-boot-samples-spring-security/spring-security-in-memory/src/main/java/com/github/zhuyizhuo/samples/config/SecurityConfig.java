package com.github.zhuyizhuo.samples.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author zhuo
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /** 在内存中配置用户信息 */
    @Override
    @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("zhangsan").password("321").authorities("p1").build());
        manager.createUser(User.withUsername("jack").password("1234").authorities("p2").build());
        return manager;
    }

    /** 在内存中配置用户信息 存在该配置 则 UserDetailsService 的配置失效  */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("james")
//                .roles("p1","p2")
//                // 123 的密文
//                .password("321");
//    }

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

    /**
     *  不加密的密码校验
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
