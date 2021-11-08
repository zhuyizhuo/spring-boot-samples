package com.github.zhuyizhuo.actuator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping("test")
    public void test(){
        for (int i = 0; i < 9999; i++) {
            System.out.println("__" + i);
        }
    }

    @RequestMapping("new")
    public void newObj(){
        for (int i = 0; i < 9999; i++) {
            Integer integer = new Integer(i);
            System.out.println(integer.byteValue());
        }
    }
}
