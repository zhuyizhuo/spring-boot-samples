package com.github.zhuyizhuo.samples.spring.security.jdbc.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        //当前认证通过的用户身份
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        return "hello world";
    }
}
