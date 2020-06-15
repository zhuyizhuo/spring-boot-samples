package com.github.zhuyizhuo.sample;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/***
 * 自定义静态资源访问
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile/**").addResourceLocations("file:" + CustomizeProperties.getUploadPath() + "/");
    }
}
