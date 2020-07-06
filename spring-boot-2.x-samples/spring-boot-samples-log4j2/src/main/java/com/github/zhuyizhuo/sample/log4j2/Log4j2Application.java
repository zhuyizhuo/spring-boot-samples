package com.github.zhuyizhuo.sample.log4j2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Log4j2Application {

    public static void main(String[] args) {
        log.info("Log4j2Application 项目启动..");
        SpringApplication.run(Log4j2Application.class, args);
    }
}
