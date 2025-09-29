package com.zhuo.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Boot扩展点使用示例配置类
 * 展示如何使用优化后的扩展点实现高级功能
 */
@Component
@ConfigurationProperties(prefix = "extension.example")
public class ExtensionExampleConfig implements InitializingBean {
    
    private static final Logger logger = LoggerFactory.getLogger(ExtensionExampleConfig.class);
    
    // 示例配置属性
    private String applicationName = "Spring Boot Extension Example";
    private String applicationVersion = "1.0.0";
    private boolean featureEnabled = true;
    private Map<String, String> customProperties = new HashMap<>();
    
    @PostConstruct
    public void init() {
        logger.info("ExtensionExampleConfig初始化开始");
        logger.info("应用名称: {}", applicationName);
        logger.info("应用版本: {}", applicationVersion);
        logger.info("功能启用状态: {}", featureEnabled);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("ExtensionExampleConfig属性设置完成");
        
        // 这里可以进行配置验证和初始化操作
        validateConfiguration();
        
        // 记录自定义属性数量
        if (!customProperties.isEmpty()) {
            logger.info("自定义属性数量: {}", customProperties.size());
            customProperties.forEach((key, value) -> {
                logger.debug("自定义属性 {}: {}", key, value);
            });
        }
    }
    
    /**
     * 验证配置的有效性
     */
    private void validateConfiguration() {
        if (applicationName == null || applicationName.trim().isEmpty()) {
            logger.warn("应用名称未设置，使用默认值");
            applicationName = "Spring Boot Extension Example";
        }
        
        if (applicationVersion == null || applicationVersion.trim().isEmpty()) {
            logger.warn("应用版本未设置，使用默认值");
            applicationVersion = "1.0.0";
        }
    }
    
    @PreDestroy
    public void destroy() {
        logger.info("ExtensionExampleConfig销毁中");
        // 清理资源
        customProperties.clear();
    }
    
    // Getters and Setters
    public String getApplicationName() {
        return applicationName;
    }
    
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    
    public String getApplicationVersion() {
        return applicationVersion;
    }
    
    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }
    
    public boolean isFeatureEnabled() {
        return featureEnabled;
    }
    
    public void setFeatureEnabled(boolean featureEnabled) {
        this.featureEnabled = featureEnabled;
    }
    
    public Map<String, String> getCustomProperties() {
        return customProperties;
    }
    
    public void setCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
    }
    
    @Override
    public String toString() {
        return String.format("ExtensionExampleConfig{applicationName='%s', applicationVersion='%s', featureEnabled=%s, customPropertiesSize=%d}",
                applicationName, applicationVersion, featureEnabled, customProperties.size());
    }
}