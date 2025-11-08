package com.github.zhuyizhuo.sentinel.service;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.github.zhuyizhuo.sentinel.handler.CustomBlockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 演示服务类
 * 提供各种业务方法，用于展示Sentinel的限流、熔断等功能
 */
@Service
public class DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    /**
     * 使用注解方式进行限流保护的简单方法
     * @return 响应消息
     */
    @SentinelResource(
            value = "helloService",
            blockHandler = "handleFlowException",
            blockHandlerClass = CustomBlockHandler.class
    )
    public String hello() {
        logger.info("[正常] hello方法被调用");
        return "Hello Sentinel! This is a protected service.";
    }

    /**
     * hello方法的降级方法
     * @param throwable 异常信息
     * @return 降级响应
     */
    public String helloFallback(Throwable throwable) {
        logger.warn("[降级] hello方法触发降级，异常信息: {}", throwable.getMessage());
        return "Service fallback activated! Please try again later.";
    }

    /**
     * 模拟可能失败的业务方法，用于演示熔断功能
     * @param param 输入参数
     * @return 响应消息
     */
    @SentinelResource(
            value = "demoService",
            blockHandler = "handleDegradeException",
            blockHandlerClass = CustomBlockHandler.class,
            fallback = "demoFallback"
    )
    public String demo(String param) {
        logger.info("[正常] demo方法被调用，参数: {}", param);
        // 模拟业务处理
        if (param != null && param.contains("error")) {
            logger.warn("[异常] demo方法触发业务异常，参数: {}", param);
            throw new RuntimeException("模拟业务异常");
        }
        
        try {
            // 模拟耗时操作
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return "Demo service processed successfully with param: " + param;
    }

    /**
     * demo方法的降级方法
     * @param param 输入参数
     * @param throwable 异常信息
     * @return 降级响应
     */
    public String demoFallback(String param, Throwable throwable) {
        logger.warn("[降级] demo方法触发降级，参数: {}, 异常信息: {}", param, throwable.getMessage());
        return "Demo service fallback activated for param: " + param;
    }

    /**
     * 使用编码方式进行限流保护的方法
     * @param param 输入参数
     * @return 响应消息
     */
    public String codingStyleLimit(String param) {
        logger.info("[正常] codingStyleLimit方法被调用，参数: {}", param);
        Entry entry = null;
        try {
            // 资源名称
            entry = SphU.entry("codingStyleResource");
            
            // 业务逻辑
            logger.info("[正常] codingStyleResource通过限流检查");
            return "Coding style limit passed with param: " + param;
        } catch (BlockException e) {
            // 限流处理
            logger.warn("[限流] codingStyleResource触发限流，参数: {}", param);
            return "Coding style limit blocked! Please try again later.";
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

    /**
     * 用于热点参数限流演示的方法
     * @param hotParam 热点参数
     * @param normalParam 普通参数
     * @return 响应消息
     */
    @SentinelResource(
            value = "hotParamService",
            blockHandler = "handleFlowException",
            blockHandlerClass = CustomBlockHandler.class
    )
    public String hotParamMethod(String hotParam, String normalParam) {
        logger.info("[正常] hotParamMethod方法被调用，热点参数: {}, 普通参数: {}", hotParam, normalParam);
        return "Hot param method processed: hotParam=" + hotParam + ", normalParam=" + normalParam;
    }

    /**
     * 用于系统保护演示的方法
     * @return 响应消息
     */
    @SentinelResource(
            value = "systemProtectionService",
            blockHandler = "handleSystemException",
            blockHandlerClass = CustomBlockHandler.class
    )
    public String systemProtection() {
        logger.info("[正常] systemProtection方法被调用");
        // 模拟高CPU使用率的操作
        long result = 0;
        for (long i = 0; i < 1000000; i++) {
            result += i;
        }
        logger.info("[正常] systemProtection方法执行完成");
        return "System protection check passed. Result: " + result;
    }
}