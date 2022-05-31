package com.github.zhuyizhuo.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * list 注入示例
 */
@Configuration
@ConfigurationProperties("demo")
public class ListConfig {

    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

}
