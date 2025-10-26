package com.github.zhuyizhuo.service;

/**
 * 模拟第三方服务接口
 */
public interface ThirdPartyService {
    
    /**
     * 模拟第三方支付接口
     */
    String processPayment(String orderId, double amount);
    
    /**
     * 模拟第三方短信发送接口
     */
    String sendSms(String phoneNumber, String message);
    
    /**
     * 模拟第三方物流查询接口
     */
    String queryLogistics(String trackingNumber);
}