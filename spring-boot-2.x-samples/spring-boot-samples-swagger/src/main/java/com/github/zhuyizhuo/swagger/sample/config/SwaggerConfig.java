package com.github.zhuyizhuo.swagger.sample.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    //是否开启swagger，正式环境一般是需要关闭的，可根据springboot的多环境配置进行设置
    @Value(value = "${swagger.enabled}")
    private Boolean swaggerEnabled;

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
//              页面上的 Base URL，如果配置了 host，swagger 页面的方法请求路径会被替换为 host 路径
//                .host("swagger.demo")
                .enable(swaggerEnabled)
                .apiInfo(apiInfo())
                .select()
                // 扫描所有有注解的 api，用这种方式更灵活
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 扫描指定包中的swagger注解
//              .apis(RequestHandlerSelectors.basePackage("com.github.zhuyizhuo.sample.controller"))
                .paths(PathSelectors.any())
                .build()
                // 生成文档 接口请求地址的前缀路径 可省略
                .pathMapping("/");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger Demo")
                //条款地址
                .termsOfServiceUrl("")
                //作者信息
                .contact(new Contact("zhuo","https://github.com/zhuyizhuo", "zhuyizhuo2019@gmail.com"))
//               版本
                .version("1.0")
                .description("演示 swagger 文档")
                .build();
    }
}
