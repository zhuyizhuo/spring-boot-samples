package com.github.zhuyizhuo.samples.spring.security.config;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuo
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    public String info(){
        return "hello world";
    }
}
