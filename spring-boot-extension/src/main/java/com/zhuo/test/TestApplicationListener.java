package com.zhuo.test;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

/**
 * ApplicationListener扩展点实现
 * 用于监听Spring应用上下文的各种事件
 * 当前实现：监控应用生命周期事件，执行相应的处理逻辑
 */
@Component
public class TestApplicationListener implements ApplicationListener<ApplicationEvent> {
    
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 根据不同的事件类型执行不同的处理逻辑
        if (event instanceof ApplicationPreparedEvent) {
            handleApplicationPreparedEvent((ApplicationPreparedEvent) event);
        } else if (event instanceof ApplicationStartedEvent) {
            handleApplicationStartedEvent((ApplicationStartedEvent) event);
        } else if (event instanceof ApplicationReadyEvent) {
            handleApplicationReadyEvent((ApplicationReadyEvent) event);
        } else if (event instanceof ApplicationFailedEvent) {
            handleApplicationFailedEvent((ApplicationFailedEvent) event);
        } else if (event instanceof ContextRefreshedEvent) {
            handleContextRefreshedEvent((ContextRefreshedEvent) event);
        } else if (event instanceof ContextStartedEvent) {
            handleContextStartedEvent((ContextStartedEvent) event);
        } else if (event instanceof ContextStoppedEvent) {
            handleContextStoppedEvent((ContextStoppedEvent) event);
        } else if (event instanceof ContextClosedEvent) {
            handleContextClosedEvent((ContextClosedEvent) event);
        }
        // 可以根据需要监听更多类型的事件
    }
    
    private void handleApplicationPreparedEvent(ApplicationPreparedEvent event) {
        System.out.println("[TestApplicationListener] 应用已准备就绪，上下文即将被刷新");
        // 应用准备阶段的处理逻辑
    }
    
    private void handleApplicationStartedEvent(ApplicationStartedEvent event) {
        System.out.println("[TestApplicationListener] 应用已启动，CommandLineRunner和ApplicationRunner即将执行");
        // 应用启动后的处理逻辑
    }
    
    private void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        System.out.println("[TestApplicationListener] 应用已完全就绪，可以开始处理请求");
        // 应用完全就绪后的处理逻辑
    }
    
    private void handleApplicationFailedEvent(ApplicationFailedEvent event) {
        System.err.println("[TestApplicationListener] 应用启动失败！");
        Throwable exception = event.getException();
        if (exception != null) {
            System.err.println("[TestApplicationListener] 失败原因: " + exception.getMessage());
            // 可以在这里添加失败通知、日志记录等处理
        }
    }
    
    private void handleContextRefreshedEvent(ContextRefreshedEvent event) {
        System.out.println("[TestApplicationListener] Spring应用上下文已刷新完成");
        // 上下文刷新后的处理逻辑
    }
    
    private void handleContextStartedEvent(ContextStartedEvent event) {
        System.out.println("[TestApplicationListener] Spring应用上下文已启动");
        // 上下文启动后的处理逻辑
    }
    
    private void handleContextStoppedEvent(ContextStoppedEvent event) {
        System.out.println("[TestApplicationListener] Spring应用上下文已停止");
        // 上下文停止后的处理逻辑
    }
    
    private void handleContextClosedEvent(ContextClosedEvent event) {
        System.out.println("[TestApplicationListener] Spring应用上下文已关闭");
        // 上下文关闭前的处理逻辑，如资源释放、清理工作等
        cleanupResources();
    }
    
    /**
     * 清理资源的方法
     */
    private void cleanupResources() {
        System.out.println("[TestApplicationListener] 开始清理应用资源...");
        // 模拟资源清理过程
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[TestApplicationListener] 应用资源清理完成");
    }
}