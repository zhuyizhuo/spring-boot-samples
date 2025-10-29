package com.github.zhuyizhuo.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.github.zhuyizhuo.config.SentinelConfig;
import org.springframework.stereotype.Service;

/**
 * 用户服务类，演示 Sentinel 注解使用
 */
@Service
public class UserService {

    /**
     * 获取用户信息接口
     * blockHandler: 指定限流/熔断时的处理函数
     * fallback: 指定异常时的处理函数
     */
    @SentinelResource(value = "getUserInfo", 
                     blockHandler = "handleBlockException", 
                     blockHandlerClass = SentinelConfig.class,
                     fallback = "handleFallbackException",
                     fallbackClass = SentinelConfig.class)
    public String getUserInfo(String userId) {
        // 模拟业务逻辑
        if (userId.equals("001")) {
            return "用户信息：张三，ID：001";
        } else if (userId.equals("002")) {
            return "用户信息：李四，ID：002";
        } else {
            throw new RuntimeException("用户不存在");
        }
    }

    /**
     * 查询用户订单接口
     * 默认使用全局处理函数
     */
    @SentinelResource(value = "getUserOrders",
                     blockHandler = "handleBlockException",
                     blockHandlerClass = SentinelConfig.class)
    public String getUserOrders(String userId) {
        // 模拟业务逻辑
        return "用户 " + userId + " 的订单列表";
    }

    /**
     * 创建用户接口
     * 自定义限流处理函数
     */
    @SentinelResource(value = "createUser",
                     blockHandler = "createUserBlockHandler",
                     fallback = "createUserFallback")
    public String createUser(String username) {
        // 模拟业务逻辑
        if (username.length() < 2) {
            throw new IllegalArgumentException("用户名长度不能小于2");
        }
        return "用户 " + username + " 创建成功";
    }

    /**
     * 自定义限流处理函数
     */
    public String createUserBlockHandler(String username, BlockException ex) {
        return "创建用户请求被限流，请稍后重试: " + username;
    }

    /**
     * 自定义异常处理函数
     */
    public String createUserFallback(String username, Throwable ex) {
        return "创建用户失败: " + ex.getMessage();
    }
}