package com.github.zhuyizhuo.samples.spring.security.jdbc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuo
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/login-success")
    public String login(){
        return "Login Success !";
    }

}
