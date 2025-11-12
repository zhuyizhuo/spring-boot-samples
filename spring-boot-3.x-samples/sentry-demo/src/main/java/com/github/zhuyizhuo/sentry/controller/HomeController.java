package com.github.zhuyizhuo.sentry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 首页控制器，用于重定向到测试页面
 */
@Controller
public class HomeController {

    /**
     * 根路径重定向到index.html测试页面
     */
    @GetMapping(value = {"", "/", "/home"})
    public RedirectView redirectToTestPage() {
        return new RedirectView("index.html");
    }
}