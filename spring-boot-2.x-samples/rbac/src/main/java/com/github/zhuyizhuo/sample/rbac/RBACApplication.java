package com.github.zhuyizhuo.sample.rbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 权限控制 基于 session
 */
@SpringBootApplication
public class RBACApplication {

    public static void main(String[] args) {
        SpringApplication.run(RBACApplication.class, args);
    }

}