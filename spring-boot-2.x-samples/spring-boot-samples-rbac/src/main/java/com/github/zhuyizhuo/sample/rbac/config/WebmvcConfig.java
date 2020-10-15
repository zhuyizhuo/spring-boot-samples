package com.github.zhuyizhuo.sample.rbac.config;

import com.github.zhuyizhuo.sample.rbac.autoconfigure.AuthProperties;
import com.github.zhuyizhuo.sample.rbac.interceptor.VisitAuthManagerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebmvcConfig implements WebMvcConfigurer {

    @Autowired
    AuthProperties authProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new VisitAuthManagerInterceptor(authProperties)).addPathPatterns("/**");
    }
}
