package com.github.zhuyizhuo.sample.rbac.controller;

import com.github.zhuyizhuo.sample.rbac.constant.AuthConstant;
import com.github.zhuyizhuo.sample.rbac.controller.req.LoginReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/sys")
public class LoginController {

    @GetMapping("login")
    public Object login(@RequestBody LoginReq loginReq, HttpSession session){
        log.info("login req:{}", loginReq);
        session.setAttribute(AuthConstant.LOGIN_SESSION_NAME, loginReq.getUsername());
        return "{\"code\":\"00000\",\"msg\":\"登陆成功\"}";
    }

    @GetMapping("logout")
    public Object logout(HttpSession session){
        session.removeAttribute(AuthConstant.LOGIN_SESSION_NAME);
        return "{\"code\":\"00000\",\"msg\":\"成功\"}";
    }
}
