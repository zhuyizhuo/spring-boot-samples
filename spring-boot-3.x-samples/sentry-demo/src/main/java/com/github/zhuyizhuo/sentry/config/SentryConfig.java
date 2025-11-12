package com.github.zhuyizhuo.sentry.config;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

/**
 * Sentry 自定义配置类
 */
@Configuration
public class SentryConfig {

    private static final Logger logger = LoggerFactory.getLogger(SentryConfig.class);

    @Value("${sentry.dsn}")
    private String dsn;

    @Value("${sentry.environment}")
    private String environment;

    @Value("${sentry.release}")
    private String release;

    @Value("${sentry.traces-sample-rate:1.0}")
    private double tracesSampleRate;

    @Value("${sentry.sample-rate:1.0}")
    private double sampleRate;

    @Value("${sentry.debug:false}")
    private boolean debug;

    /**
     * 初始化Sentry并应用配置
     */
    @PostConstruct
    public void initSentry() {
        try {
            logger.info("开始初始化Sentry...");
            logger.info("使用DSN: {}", maskDsn(dsn));
            
            // 直接初始化Sentry，确保配置正确应用
            Sentry.init(options -> {
                options.setDsn(dsn);
                options.setEnvironment(environment);
                options.setRelease(release);
                options.setTracesSampleRate(tracesSampleRate);
                options.setSampleRate(sampleRate);
                options.setDebug(debug);
                options.setAttachStacktrace(true);
                options.setSendDefaultPii(true);
                
                // 设置事件处理器
                options.setBeforeSend((event, hint) -> {
                    // 添加自定义标签
                    event.setTag("application_type", "spring_boot");
                    event.setTag("java_version", System.getProperty("java.version"));
                    
                    // 过滤敏感信息
                    if (event.getRequest() != null && event.getRequest().getData() != null) {
                        Object data = event.getRequest().getData();
                        if (data instanceof String dataStr && dataStr.contains("password")) {
                            event.getRequest().setData("[REDACTED]");
                        }
                    }
                    
                    return event;
                });
            });
            
            logger.info("Sentry初始化完成");
            
            // 设置Sentry日志级别为DEBUG，便于查看连接状态
            Sentry.setLevel(SentryLevel.DEBUG);
            
            // 发送一个测试事件以验证连接
            Sentry.captureMessage("Sentry连接测试消息", SentryLevel.INFO);
            logger.info("Sentry连接测试消息已发送");
            
        } catch (Exception e) {
            logger.error("Sentry初始化或连接失败", e);
        }
    }
    
    /**
     * 掩码DSN以保护敏感信息
     */
    private String maskDsn(String dsn) {
        if (dsn == null || !dsn.contains("@")) {
            return dsn;
        }
        int atIndex = dsn.indexOf("@");
        int protocolEndIndex = dsn.indexOf("://") + 3;
        
        // 掩码DSN密钥部分
        return dsn.substring(0, protocolEndIndex) + "***" + dsn.substring(atIndex);
    }
}