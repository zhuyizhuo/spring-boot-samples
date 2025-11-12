package com.github.zhuyizhuo.sentry.service;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import org.springframework.stereotype.Service;

/**
 * Sentry 服务层演示，用于测试异常传播和自定义监控
 */
@Service
public class SentryService {

    /**
     * 抛出异常用于测试自动捕获
     */
    public void throwException() {
        // 添加面包屑，记录操作历史
        Sentry.addBreadcrumb("执行服务层操作");
        Sentry.addBreadcrumb("准备抛出异常");
        
        // 记录额外的上下文信息
        Sentry.setExtra("service_name", "SentryService");
        Sentry.setExtra("method_name", "throwException");
        
        // 记录一个信息日志
        Sentry.captureMessage("这是服务层的信息日志", SentryLevel.INFO);
        
        // 抛出异常
        throw new IllegalArgumentException("服务层参数验证失败", new RuntimeException("原始异常"));
    }

    /**
     * 手动记录错误而不抛出
     */
    public void logError(String message) {
        try {
            // 模拟业务逻辑
            if (message == null || message.isEmpty()) {
                // 手动捕获并记录错误
                Sentry.captureMessage("空消息检测", SentryLevel.ERROR);
                Sentry.setExtra("error_detail", "消息参数为空");
            }
        } catch (Exception e) {
            // 记录异常详情
            Sentry.captureException(e);
        }
    }

    /**
     * 性能测试方法
     */
    public void performanceTest() {
        // 开始事务
        io.sentry.ITransaction transaction = Sentry.startTransaction("performance-test-transaction", "service-operation");
        
        try {
            // 创建子 span
            io.sentry.ISpan dbSpan = transaction.startChild("db-operation", "slow-query");
            try {
                // 模拟数据库操作
                Thread.sleep(100);
                dbSpan.setData("query_result", "success");
            } finally {
                dbSpan.finish();
            }
            
            // 另一个子 span
            io.sentry.ISpan businessSpan = transaction.startChild("business-logic", "data-processing");
            try {
                // 模拟业务逻辑处理
                Thread.sleep(50);
                businessSpan.setData("process_count", 100);
            } finally {
                businessSpan.finish();
            }
            
            // 设置事务状态成功
            transaction.setStatus(io.sentry.SpanStatus.OK);
        } catch (Exception e) {
            transaction.setThrowable(e);
            transaction.setStatus(io.sentry.SpanStatus.INTERNAL_ERROR);
        } finally {
            // 确保事务结束
            transaction.finish();
        }
    }
}