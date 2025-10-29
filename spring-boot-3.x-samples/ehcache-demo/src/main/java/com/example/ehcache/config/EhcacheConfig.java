package com.example.ehcache.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Ehcache 配置类
 * 配置通过注解和XML两种方式支持
 * <p>
 * 说明：
 * 1. @EnableCaching 启用Spring Cache支持
 * 2. 缓存具体配置通过以下两种方式：
 *    - 注解方式：在Service方法上使用@Cacheable、@CachePut、@CacheEvict等注解
 *    - XML方式：通过ehcache.xml文件配置，可在application.yml中启用
 */
@Configuration
@EnableCaching
public class EhcacheConfig {
    // Spring Boot会自动检测并配置Ehcache
    // 如果需要自定义配置，可以在这里添加相关Bean
}