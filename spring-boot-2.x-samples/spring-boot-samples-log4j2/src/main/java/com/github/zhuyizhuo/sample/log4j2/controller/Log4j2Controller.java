package com.github.zhuyizhuo.sample.log4j2.controller;

import com.github.zhuyizhuo.sample.log4j2.service.Log4j2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
@Slf4j
public class Log4j2Controller {

    @Autowired
    Log4j2Service log4j2Service;

    @RequestMapping("/demo")
    public String log(){
        log.info("demo is running..");
        log4j2Service.HelloLog4j2();
        return "SUCCESS";
    }
}
