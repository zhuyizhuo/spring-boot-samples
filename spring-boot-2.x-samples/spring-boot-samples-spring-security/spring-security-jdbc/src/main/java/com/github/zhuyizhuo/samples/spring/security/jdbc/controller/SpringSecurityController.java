package com.github.zhuyizhuo.samples.spring.security.jdbc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuo
 */
@RestController
@RequestMapping("/security")
public class SpringSecurityController {

    @RequestMapping("/login")
    public String login(){
        return "Login Success !";
    }

    @RequestMapping("/doLogin")
    public String doLogin(){
        return " doLogin  !";
    }
}
