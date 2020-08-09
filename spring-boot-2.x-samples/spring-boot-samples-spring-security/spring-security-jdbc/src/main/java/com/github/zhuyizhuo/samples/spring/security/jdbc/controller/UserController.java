package com.github.zhuyizhuo.samples.spring.security.jdbc.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
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


    @RequestMapping("/info/p1")
    public String info(){
        //当前认证通过的用户身份
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return "hello " + currentUserName + ", you has p1 authentication";
        }
        return "hello world, has p1 authentication";
    }

    @RequestMapping("/info/p2")
    public String list(){
        //当前认证通过的用户身份
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return "hello " + currentUserName + ", you has p2 authentication";
        }
        return "hello world, has p2 authentication";
    }
}
