package com.zhuo.test;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * InitializingBean扩展点优化
 * 在Bean的属性设置完成后执行初始化操作
 * 当前实现：执行属性验证、资源初始化等操作
 */
@Component
public class TestInitializingBean implements InitializingBean {
    
    private String initMessage = "Default Initialization Message";
    private boolean initialized = false;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("[TestInitializingBean] 开始执行初始化操作");
        
        // 验证属性
        validateProperties();
        
        // 初始化资源
        initializeResources();
        
        // 记录初始化完成
        initialized = true;
        
        System.out.println("[TestInitializingBean] 初始化操作完成，消息: " + initMessage);
    }
    
    /**
     * 验证属性
     */
    private void validateProperties() {
        System.out.println("[TestInitializingBean] 验证Bean属性");
        
        // 示例：验证必要属性是否设置
        if (initMessage == null || initMessage.isEmpty()) {
            throw new IllegalStateException("初始化消息不能为空");
        }
        
        System.out.println("[TestInitializingBean] 属性验证通过");
    }
    
    /**
     * 初始化资源
     */
    private void initializeResources() {
        System.out.println("[TestInitializingBean] 初始化资源");
        
        // 示例：模拟资源初始化过程
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 更新初始化消息
        initMessage = "Initialized at " + System.currentTimeMillis();
    }
    
    /**
     * 获取初始化状态
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * 获取初始化消息
     */
    public String getInitMessage() {
        return initMessage;
    }
    
    /**
     * 设置初始化消息
     */
    public void setInitMessage(String initMessage) {
        this.initMessage = initMessage;
    }
}
