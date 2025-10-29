package com.github.zhuyizhuo.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.github.zhuyizhuo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器，演示 Sentinel 编程式使用和注解式使用
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 编程式使用 Sentinel - 获取用户信息
     */
    @GetMapping("/programmatic/{userId}")
    public String getUserInfoProgrammatic(@PathVariable String userId) {
        // 定义资源名
        String resourceName = "getUserInfoProgrammatic";
        Entry entry = null;
        try {
            // 进入保护的资源
            entry = SphU.entry(resourceName);
            // 执行业务逻辑
            if (userId.equals("001")) {
                return "用户信息：张三，ID：001";
            } else {
                return "用户信息：未知用户，ID：" + userId;
            }
        } catch (BlockException e) {
            // 处理限流或熔断
            return "请求被限流或熔断，请稍后重试";
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

    /**
     * 编程式使用 Sentinel - 带上下文
     */
    @GetMapping("/context/{userId}")
    public String getUserWithContext(@PathVariable String userId) {
        try {
            // 创建上下文
            ContextUtil.enter("userContext", "appA");
            // 定义资源名
            String resourceName = "getUserWithContext";
            Entry entry = SphU.entry(resourceName);
            try {
                // 执行业务逻辑
                return "上下文环境中获取用户信息：" + userId;
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
        } catch (BlockException e) {
            return "上下文环境中请求被限流";
        } finally {
            ContextUtil.exit();
        }
    }

    /**
     * 注解式使用 Sentinel - 获取用户信息
     */
    @GetMapping("/{userId}")
    public String getUserInfo(@PathVariable String userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 注解式使用 Sentinel - 获取用户订单
     */
    @GetMapping("/{userId}/orders")
    public String getUserOrders(@PathVariable String userId) {
        return userService.getUserOrders(userId);
    }

    /**
     * 注解式使用 Sentinel - 创建用户
     */
    @PostMapping
    public String createUser(@RequestParam String username) {
        return userService.createUser(username);
    }

    /**
     * 热点参数限流演示
     */
    @GetMapping("/hotspot")
    public String hotspotDemo(@RequestParam String param1, @RequestParam String param2) {
        return "热点参数演示: param1=" + param1 + ", param2=" + param2;
    }
}