package com.github.zhuyizhuo.async.sample.controller;

import com.github.zhuyizhuo.async.sample.domain.AsyncDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/async")
@RestController
public class AsyncController {

    @Autowired
    private AsyncDomain asyncDomain;

    @RequestMapping("test")
    public String test(){
        System.out.println("Controller threadName:" + Thread.currentThread().getName());
        asyncDomain.asyncTest();
        return "success!";
    }

}
