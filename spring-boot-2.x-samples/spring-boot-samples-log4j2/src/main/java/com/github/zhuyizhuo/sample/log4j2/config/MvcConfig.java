package com.github.zhuyizhuo.sample.log4j2.config;

import com.github.zhuyizhuo.sample.log4j2.interceptor.Log4j2Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    Log4j2Interceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (interceptor == null) {
            interceptor = new Log4j2Interceptor();
        }
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**");
    }
}
