package com.github.zhuyizhuo.sentinel.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sentinel配置类
 * 配置Sentinel注解支持和相关组件
 */
@Configuration
public class SentinelConfig {

    /**
     * 注入SentinelResource注解支持的切面
     * 用于处理@SentinelResource注解标记的方法
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}