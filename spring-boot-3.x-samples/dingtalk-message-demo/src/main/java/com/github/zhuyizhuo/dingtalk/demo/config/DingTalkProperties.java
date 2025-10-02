package com.github.zhuyizhuo.dingtalk.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 钉钉配置属性类
 */
@Component
@ConfigurationProperties(prefix = "dingtalk")
public class DingTalkProperties {

    /**
     * 应用的AppKey
     */
    private String appKey;
    
    /**
     * 应用的AppSecret
     */
    private String appSecret;
    
    /**
     * 机器人Webhook
     */
    private String webhook;
    
    /**
     * 机器人签名密钥
     */
    private String secret;

    // 手动实现getter方法
    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}