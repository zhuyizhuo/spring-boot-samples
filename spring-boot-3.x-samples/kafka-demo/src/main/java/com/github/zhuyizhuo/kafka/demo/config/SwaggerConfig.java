package com.github.zhuyizhuo.kafka.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI配置类
 * 用于配置SpringDoc OpenAPI文档
 */
@Configuration
public class SwaggerConfig {

    /**
     * 配置OpenAPI信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kafka Demo API")
                        .version("1.0")
                        .description("Spring Boot 3.x Kafka集成示例项目API文档")
                        .contact(new Contact()
                                .name("Kafka Demo Team")
                                .email("demo@example.com")
                        )
                );
    }
    
    /**
     * 配置API分组，简化配置确保所有接口都能被捕获
     */
    @Bean
    public io.swagger.v3.oas.models.info.License license() {
        return new io.swagger.v3.oas.models.info.License().name("Apache 2.0").url("http://springdoc.org");
    }
}