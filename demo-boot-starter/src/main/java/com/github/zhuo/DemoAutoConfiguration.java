package com.github.zhuo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DemoProperties.class)
@ConditionalOnClass(DemoService.class)
@ConditionalOnProperty(prefix = "zhuo.demo", value = "enabled", matchIfMissing = true)
public class DemoAutoConfiguration {

    @Autowired
    private DemoProperties properties;

    @Bean
    @ConditionalOnMissingBean(DemoService.class)
    public DemoService tianService() {
        return new DemoService(properties);
    }

}
