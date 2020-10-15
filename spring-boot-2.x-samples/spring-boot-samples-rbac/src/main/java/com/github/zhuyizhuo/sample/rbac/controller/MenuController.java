package com.github.zhuyizhuo.sample.rbac.controller;

import com.github.zhuyizhuo.sample.rbac.constant.AuthConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("menu")
public class MenuController {

    @RequestMapping("anonymous")
    public String anonymous(HttpSession session){
        return (session.getAttribute(AuthConstant.LOGIN_SESSION_NAME)==null
                ? "" : session.getAttribute(AuthConstant.LOGIN_SESSION_NAME)) + " visit anonymous menu.";
    }

    @RequestMapping("login")
    public String login(HttpSession session){
        return session.getAttribute(AuthConstant.LOGIN_SESSION_NAME) + " visit login menu.";
    }

    @RequestMapping("permission")
    public String permission(){
        return "this method cant be visit";
    }
}
