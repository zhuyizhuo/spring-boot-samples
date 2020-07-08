package com.github.zhuyizhuo.samples.spring.security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class SpringSecurityController {

    @RequestMapping("/login")
    public String login(){

        return "Login Success !";
    }
}
