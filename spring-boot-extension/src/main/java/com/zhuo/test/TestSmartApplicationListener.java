package com.zhuo.test;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SmartApplicationListener扩展点实现
 * 智能事件监听器，可以根据事件类型进行精确监听
 * 当前实现：专门监听ApplicationReadyEvent事件，用于在应用完全就绪后执行特定任务
 */
@Component
public class TestSmartApplicationListener implements SmartApplicationListener {
    
    private static final AtomicInteger EVENT_COUNT = new AtomicInteger(0);
    
    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        // 只处理ApplicationReadyEvent类型的事件
        return ApplicationReadyEvent.class.isAssignableFrom(eventType);
    }
    
    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        // 对事件源类型不做限制
        return true;
    }
    
    @Override
    public int getOrder() {
        // 设置监听器的执行顺序，值越小优先级越高
        return 10;
    }
    
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        int count = EVENT_COUNT.incrementAndGet();
        System.out.println("[TestSmartApplicationListener] 监听到ApplicationReadyEvent事件，事件计数: " + count);
        
        if (event instanceof ApplicationReadyEvent) {
            handleApplicationReadyEvent((ApplicationReadyEvent) event);
        }
    }
    
    private void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        System.out.println("[TestSmartApplicationListener] 应用已完全就绪，可以处理请求");
        
        // 示例：执行应用就绪后的特定任务
        performHealthCheck();
        
        // 示例：预热缓存数据
        warmupCache();
        
        System.out.println("[TestSmartApplicationListener] 应用就绪后任务执行完成");
    }
    
    /**
     * 执行健康检查
     */
    private void performHealthCheck() {
        System.out.println("[TestSmartApplicationListener] 开始执行系统健康检查...");
        // 模拟健康检查过程
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[TestSmartApplicationListener] 系统健康检查完成，状态：正常");
    }
    
    /**
     * 预热缓存数据
     */
    private void warmupCache() {
        System.out.println("[TestSmartApplicationListener] 开始预热缓存数据...");
        // 模拟缓存预热过程
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[TestSmartApplicationListener] 缓存数据预热完成");
    }
}