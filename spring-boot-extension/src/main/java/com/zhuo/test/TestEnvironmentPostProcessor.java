package com.zhuo.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EnvironmentPostProcessor扩展点优化实现
 * 在Spring Boot应用的环境准备阶段修改环境配置
 * 当前实现：
 * - 添加多级自定义环境属性
 * - 实现完整的配置文件加载功能
 * - 增强环境检测能力
 * - 添加配置验证和容错机制
 * - 支持配置文件优先级管理
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 100)  // 设置较高优先级，但低于Spring Boot内部处理器
public class TestEnvironmentPostProcessor implements EnvironmentPostProcessor {
    
    private static final String CUSTOM_PROPERTY_SOURCE_NAME = "customEnvironmentProperties";
    private static final String EXTENSION_PROFILE_PREFIX = "extension-";
    private static final String[] CONFIG_LOCATIONS = {
            "classpath*:extension-*.properties",
            "classpath*:extension-*.yml",
            "classpath*:extension-*.yaml"
    };
    
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        long startTime = System.currentTimeMillis();
        System.out.println("[TestEnvironmentPostProcessor] 开始处理环境配置");
        
        try {
            // 1. 记录环境初始状态
            logEnvironmentInitialState(environment);
            
            // 2. 添加核心自定义环境属性
            addCustomProperties(environment);
            
            // 3. 加载额外的配置文件
            int loadedFilesCount = loadAdditionalConfigurationFiles(environment);
            
            // 4. 根据环境调整配置
            adjustConfigurationForEnvironment(environment);
            
            // 5. 验证关键配置
            validateCriticalConfigurations(environment);
            
            // 6. 日志记录处理结果
            long processingTime = System.currentTimeMillis() - startTime;
            System.out.println("[TestEnvironmentPostProcessor] 环境配置处理完成 - 耗时: " + processingTime + "ms, 加载配置文件数: " + loadedFilesCount);
            System.out.println("[TestEnvironmentPostProcessor] 当前激活的配置文件: " + String.join(", ", environment.getActiveProfiles()));
            System.out.println("[TestEnvironmentPostProcessor] 检测到的环境类型: " + determineEnvironment(environment));
        } catch (Exception e) {
            System.err.println("[TestEnvironmentPostProcessor] 处理环境配置时发生异常: " + e.getMessage());
            e.printStackTrace();
            // 即使发生异常，也继续应用启动，确保容错性
        }
    }
    
    /**
     * 记录环境初始状态
     */
    private void logEnvironmentInitialState(ConfigurableEnvironment environment) {
        System.out.println("[TestEnvironmentPostProcessor] 环境初始状态: ");
        System.out.println("- 活动配置文件数: " + environment.getActiveProfiles().length);
        System.out.println("- 默认配置文件数: " + environment.getDefaultProfiles().length);
        System.out.println("- 属性源数量: " + environment.getPropertySources().size());
        
        // 列出主要的属性源
        List<String> mainPropertySources = environment.getPropertySources().stream()
                .map(PropertySource::getName)
                .filter(name -> !name.contains("systemProperties") && !name.contains("systemEnvironment"))
                .limit(5)
                .collect(Collectors.toList());
        System.out.println("- 主要属性源: " + String.join(", ", mainPropertySources));
    }
    
    /**
     * 添加自定义环境属性
     */
    private void addCustomProperties(ConfigurableEnvironment environment) {
        Map<String, Object> customProperties = new HashMap<>();
        
        // 1. 系统基础信息
        customProperties.put("extension.system.version", "1.0.0");
        customProperties.put("extension.system.buildTime", System.currentTimeMillis());
        customProperties.put("extension.system.startupTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        customProperties.put("extension.system.environment", determineEnvironment(environment));
        customProperties.put("extension.system.java.version", System.getProperty("java.version"));
        customProperties.put("extension.system.operatingSystem", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        
        // 2. 特性开关配置
        Map<String, Object> features = new HashMap<>();
        features.put("cache.enabled", "true");
        features.put("logging.enhanced", "true");
        features.put("monitoring.enabled", "true");
        features.put("swagger.enabled", !isProductionEnvironment(environment));
        features.put("debugMode", !isProductionEnvironment(environment));
        
        // 3. 根据环境调整特性配置
        if (isProductionEnvironment(environment)) {
            features.put("trace.logs.enabled", "false");
            features.put("development.tools.enabled", "false");
        } else {
            features.put("trace.logs.enabled", "true");
            features.put("development.tools.enabled", "true");
        }
        
        // 将特性配置添加到主属性Map
        for (Map.Entry<String, Object> entry : features.entrySet()) {
            customProperties.put("extension.features." + entry.getKey(), entry.getValue());
        }
        
        // 4. 创建并添加自定义属性源
        MapPropertySource propertySource = new MapPropertySource(CUSTOM_PROPERTY_SOURCE_NAME, customProperties);
        environment.getPropertySources().addFirst(propertySource);
        
        System.out.println("[TestEnvironmentPostProcessor] 添加了自定义属性源: " + CUSTOM_PROPERTY_SOURCE_NAME + ", 包含属性数: " + customProperties.size());
        
        // 打印部分关键属性
        System.out.println("[TestEnvironmentPostProcessor] - 系统版本: " + customProperties.get("extension.system.version"));
        System.out.println("[TestEnvironmentPostProcessor] - 环境类型: " + customProperties.get("extension.system.environment"));
    }
    
    /**
     * 加载额外的配置文件
     */
    private int loadAdditionalConfigurationFiles(ConfigurableEnvironment environment) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        int totalLoadedFiles = 0;
        
        try {
            // 按优先级顺序加载不同位置和类型的配置文件
            for (String locationPattern : CONFIG_LOCATIONS) {
                Resource[] resources = resolver.getResources(locationPattern);
                
                if (resources != null && resources.length > 0) {
                    System.out.println("[TestEnvironmentPostProcessor] 发现 " + resources.length + " 个配置文件匹配模式: " + locationPattern);
                    
                    // 排序以确保一致的加载顺序
                    Arrays.sort(resources, Comparator.comparing(resource -> {
                        try {
                            return resource.getFilename();
                        } catch (Exception e) {
                            return "";
                        }
                    }));
                    
                    // 加载每个配置文件
                    for (Resource resource : resources) {
                        if (resource.exists() && resource.isReadable()) {
                            try {
                                loadPropertiesFile(resource, environment);
                                totalLoadedFiles++;
                            } catch (IOException e) {
                                System.err.println("[TestEnvironmentPostProcessor] 加载配置文件失败: " + resource.getFilename() + ", 错误: " + e.getMessage());
                                // 继续加载其他文件，确保容错性
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[TestEnvironmentPostProcessor] 查找配置文件时出错: " + e.getMessage());
        }
        
        // 加载特定于当前环境的配置
        String env = determineEnvironment(environment);
        try {
            Resource envSpecificResource = resolver.getResource("classpath:extension-config-" + env + ".properties");
            if (envSpecificResource.exists() && envSpecificResource.isReadable()) {
                loadPropertiesFile(envSpecificResource, environment);
                totalLoadedFiles++;
            }
        } catch (Exception e) {
            // 忽略特定环境配置文件不存在的情况
        }
        
        return totalLoadedFiles;
    }
    
    /**
     * 加载属性文件
     */
    private void loadPropertiesFile(Resource resource, ConfigurableEnvironment environment) throws IOException {
        try {
            // 根据文件类型选择不同的加载方式
            String filename = resource.getFilename();
            if (filename != null) {
                if (filename.endsWith(".properties")) {
                    loadPropertiesFileImpl(resource, environment);
                } else if (filename.endsWith(".yml") || filename.endsWith(".yaml")) {
                    // 在实际项目中，这里可以实现YAML文件的加载
                    System.out.println("[TestEnvironmentPostProcessor] 发现YAML配置文件: " + filename + ", 支持需要添加SnakeYAML依赖");
                }
            }
        } catch (IOException e) {
            System.err.println("[TestEnvironmentPostProcessor] 加载配置文件时出错: " + resource.getFilename());
            throw e;
        }
    }
    
    /**
     * 实际加载Properties文件的实现
     */
    private void loadPropertiesFileImpl(Resource resource, ConfigurableEnvironment environment) throws IOException {
        Properties properties = new Properties();
        properties.load(resource.getInputStream());
        
        String filename = resource.getFilename();
        String propertySourceName = EXTENSION_PROFILE_PREFIX + filename;
        
        // 转换Properties到Map
        Map<String, Object> propertiesMap = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            propertiesMap.put(key, properties.getProperty(key));
        }
        
        // 创建属性源并添加到环境中
        MapPropertySource propertySource = new MapPropertySource(propertySourceName, propertiesMap);
        
        // 根据配置文件类型决定添加位置
        if (filename.contains("override") || filename.contains("custom")) {
            // 自定义覆盖配置放在前面
            environment.getPropertySources().addAfter(CUSTOM_PROPERTY_SOURCE_NAME, propertySource);
        } else {
            // 普通配置放在后面
            environment.getPropertySources().addLast(propertySource);
        }
        
        System.out.println("[TestEnvironmentPostProcessor] 已加载配置文件: " + filename + ", 包含属性数: " + propertiesMap.size());
    }
    
    /**
     * 根据环境调整配置
     */
    private void adjustConfigurationForEnvironment(ConfigurableEnvironment environment) {
        String env = determineEnvironment(environment);
        
        // 根据不同环境调整日志级别
        if ("production".equals(env)) {
            System.setProperty("logging.level.root", "INFO");
            System.setProperty("logging.level.com.zhuo", "WARN");
        } else if ("testing".equals(env)) {
            System.setProperty("logging.level.root", "INFO");
            System.setProperty("logging.level.com.zhuo", "INFO");
        } else {
            // 开发环境
            System.setProperty("logging.level.root", "INFO");
            System.setProperty("logging.level.com.zhuo", "DEBUG");
        }
        
        System.out.println("[TestEnvironmentPostProcessor] 已根据环境类型 '" + env + "' 调整日志配置");
    }
    
    /**
     * 验证关键配置
     */
    private void validateCriticalConfigurations(ConfigurableEnvironment environment) {
        List<String> missingCriticalProps = new ArrayList<>();
        
        // 检查关键配置是否存在
        String[] criticalProperties = {
                "extension.system.version",
                "extension.features.cache.enabled"
        };
        
        for (String prop : criticalProperties) {
            if (!environment.containsProperty(prop)) {
                missingCriticalProps.add(prop);
            }
        }
        
        if (!missingCriticalProps.isEmpty()) {
            System.out.println("[TestEnvironmentPostProcessor] 警告: 缺少以下关键配置属性: " + String.join(", ", missingCriticalProps));
        } else {
            System.out.println("[TestEnvironmentPostProcessor] 关键配置验证通过");
        }
    }
    
    /**
     * 确定当前环境
     */
    private String determineEnvironment(ConfigurableEnvironment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        
        if (activeProfiles != null && activeProfiles.length > 0) {
            for (String profile : activeProfiles) {
                if ("prod".equals(profile) || "production".equals(profile)) {
                    return "production";
                } else if ("test".equals(profile) || "testing".equals(profile)) {
                    return "testing";
                } else if ("dev".equals(profile) || "development".equals(profile)) {
                    return "development";
                } else if ("staging".equals(profile)) {
                    return "staging";
                }
            }
        }
        
        // 检查环境变量或系统属性来确定环境
        String envFromSystem = System.getProperty("app.env", System.getenv("APP_ENV"));
        if (envFromSystem != null) {
            return envFromSystem.toLowerCase();
        }
        
        return "default";
    }
    
    /**
     * 判断是否为生产环境
     */
    private boolean isProductionEnvironment(ConfigurableEnvironment environment) {
        String env = determineEnvironment(environment);
        return "production".equals(env) || "prod".equals(env);
    }
}