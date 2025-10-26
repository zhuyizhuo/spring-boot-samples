package com.github.zhuyizhuo.service.impl;

import com.github.zhuyizhuo.service.ThirdPartyService;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 第三方服务实现类，模拟第三方服务的延迟和失败
 */
@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {
    
    private final Random random = new Random();
    
    @Override
    public String processPayment(String orderId, double amount) {
        try {
            // 模拟延迟，随机100-300ms
            int delay = 100 + random.nextInt(200);
            TimeUnit.MILLISECONDS.sleep(delay);
            
            // 模拟随机失败，10%的失败率
            if (random.nextDouble() < 0.1) {
                throw new RuntimeException("支付网关错误");
            }
            
            return "支付处理成功，订单号：" + orderId + "，金额：" + amount;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "支付处理被中断";
        } catch (Exception e) {
            throw new RuntimeException("支付处理失败: " + e.getMessage());
        }
    }
    
    @Override
    public String sendSms(String phoneNumber, String message) {
        try {
            // 模拟延迟，随机50-150ms
            int delay = 50 + random.nextInt(100);
            TimeUnit.MILLISECONDS.sleep(delay);
            
            // 模拟随机失败，15%的失败率
            if (random.nextDouble() < 0.15) {
                throw new RuntimeException("短信服务暂时不可用");
            }
            
            return "短信发送成功，手机号：" + phoneNumber;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "短信发送被中断";
        } catch (Exception e) {
            throw new RuntimeException("短信发送失败: " + e.getMessage());
        }
    }
    
    @Override
    public String queryLogistics(String trackingNumber) {
        try {
            // 模拟随机延迟，可能很慢
            int delay;
            if (random.nextDouble() < 0.3) {
                // 30%的概率出现慢调用，延迟1-2秒
                delay = 1000 + random.nextInt(1000);
            } else {
                // 70%的概率正常延迟，50-100ms
                delay = 50 + random.nextInt(50);
            }
            
            TimeUnit.MILLISECONDS.sleep(delay);
            
            return "物流查询成功，运单号：" + trackingNumber + "，当前状态：运输中";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "物流查询被中断";
        } catch (Exception e) {
            throw new RuntimeException("物流查询失败: " + e.getMessage());
        }
    }
}