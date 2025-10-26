package com.github.zhuyizhuo.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sentinel 配置类
 */
@Configuration
public class SentinelConfig {

    /**
     * 配置 SentinelResource 注解支持的切面
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    /**
     * 全局的熔断降级处理函数
     */
    public static String handleBlockException(BlockException ex) {
        return "请求被限流或熔断，请稍后重试";
    }

    /**
     * 全局的异常处理函数
     */
    public static String handleFallbackException(Throwable ex) {
        return "系统异常，请稍后重试: " + ex.getMessage();
    }
}