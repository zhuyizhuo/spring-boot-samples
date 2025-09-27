package com.github.zhuyizhuo.sample.mybatis.plus.advanced;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.github.zhuyizhuo.sample.mybatis.plus.advanced.mapper")
public class MybatisPlusAdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusAdvancedApplication.class, args);
    }
}