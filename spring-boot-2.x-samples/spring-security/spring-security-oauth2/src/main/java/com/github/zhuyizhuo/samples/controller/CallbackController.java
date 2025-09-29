package com.github.zhuyizhuo.samples.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 回调控制器
 * 处理OAuth2授权码模式的回调请求
 * @author zhuo
 */
@Controller
public class CallbackController {

    /**
     * 处理授权码回调
     * 将授权码和状态参数传递给前端页面
     */
    @GetMapping("/callback")
    public String handleCallback(@RequestParam("code") String code,
                               @RequestParam("state") String state,
                               Model model) {
        // 将授权码和状态参数添加到模型中
        model.addAttribute("code", code);
        model.addAttribute("state", state);
        
        // 返回回调页面视图
        return "callback";
    }
}