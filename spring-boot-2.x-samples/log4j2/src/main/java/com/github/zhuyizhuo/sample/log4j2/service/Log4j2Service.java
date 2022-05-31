package com.github.zhuyizhuo.sample.log4j2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Log4j2Service {

    public void HelloLog4j2(){
        log.info("Hello log4j2 !");
    }
}
