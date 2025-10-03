package com.github.zhuyizhuo.wechat.demo.config;

import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WechatConfig {

    @Value("${wechat.mp.app-id}")
    private String appId;

    @Value("${wechat.mp.secret}")
    private String secret;

    @Bean
    public WxMpDefaultConfigImpl wxMpDefaultConfigImpl() {
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(appId);
        config.setSecret(secret);
        return config;
    }

    @Bean
    public WxMpService wxMpService(WxMpDefaultConfigImpl config) {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);
        return wxMpService;
    }

    @Bean
    public WxMpMessageRouter wxMpMessageRouter(WxMpService wxMpService) {
        WxMpMessageRouter router = new WxMpMessageRouter(wxMpService);
        // 可以在这里配置消息路由规则
        return router;
    }

}