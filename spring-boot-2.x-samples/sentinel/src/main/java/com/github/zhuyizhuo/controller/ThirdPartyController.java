package com.github.zhuyizhuo.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.github.zhuyizhuo.service.ThirdPartyService;
import com.github.zhuyizhuo.config.SentinelConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 第三方服务控制器，演示信号量隔离和熔断策略
 */
@RestController
@RequestMapping("/api/third-party")
public class ThirdPartyController {

    @Autowired
    private ThirdPartyService thirdPartyService;

    /**
     * 支付处理接口 - 配置信号量隔离和熔断
     */
    @SentinelResource(
            value = "thirdPartyPayment",
            blockHandler = "handleBlockException",
            blockHandlerClass = SentinelConfig.class,
            fallback = "handleFallbackException",
            fallbackClass = SentinelConfig.class
    )
    @PostMapping("/payment")
    public String processPayment(@RequestParam String orderId, @RequestParam double amount) {
        return thirdPartyService.processPayment(orderId, amount);
    }

    /**
     * 短信发送接口 - 配置信号量隔离和熔断
     */
    @SentinelResource(
            value = "thirdPartySms",
            blockHandler = "handleBlockException",
            blockHandlerClass = SentinelConfig.class,
            fallback = "handleFallbackException",
            fallbackClass = SentinelConfig.class
    )
    @PostMapping("/sms")
    public String sendSms(@RequestParam String phoneNumber, @RequestParam String message) {
        return thirdPartyService.sendSms(phoneNumber, message);
    }

    /**
     * 物流查询接口 - 配置信号量隔离和基于慢调用比例的熔断
     */
    @SentinelResource(
            value = "thirdPartyLogistics",
            blockHandler = "handleBlockException",
            blockHandlerClass = SentinelConfig.class,
            fallback = "handleFallbackException",
            fallbackClass = SentinelConfig.class
    )
    @GetMapping("/logistics/{trackingNumber}")
    public String queryLogistics(@PathVariable String trackingNumber) {
        return thirdPartyService.queryLogistics(trackingNumber);
    }
}