package com.zhuo.test;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ApplicationContextInitializer扩展点优化实现
 * 在Spring容器刷新前执行，用于初始化ConfigurableApplicationContext
 * 当前增强实现：
 * - 添加执行优先级控制
 * - 多级初始化流程管理
 * - 条件化配置和环境检测
 * - 资源加载和应用配置
 * - 容器功能增强和扩展
 * - 异常处理和容错机制
 * - 详细的初始化状态报告
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)  // 设置较高优先级，确保尽早执行
public class TestApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    
    private static final String CUSTOM_PROPERTY_SOURCE_NAME = "customExtensionProperties";
    private static final String INITIALIZATION_CONTEXT_KEY = "extension.initialization.context";
    private static final String[] CRITICAL_BEAN_PACKAGES = {
            "com.zhuo.test",
            "com.zhuo.config"
    };
    
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        long startTime = System.currentTimeMillis();
        System.out.println("[TestApplicationContextInitializer] 开始初始化Spring应用上下文");
        
        try {
            // 阶段1: 初始化前准备和环境检测
            ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();
            String environmentType = determineEnvironmentType(environment);
            System.out.println("[TestApplicationContextInitializer] 检测到环境类型: " + environmentType);
            
            // 记录初始上下文信息
            logApplicationContextInfo(configurableApplicationContext);
            
            // 阶段2: 添加核心配置和属性
            addCoreProperties(environment);
            
            // 阶段3: 根据环境类型配置上下文
            configureContextByEnvironmentType(configurableApplicationContext, environmentType);
            
            // 阶段4: 预加载关键资源
            preloadCriticalResources(configurableApplicationContext);
            
            // 阶段5: 设置上下文增强功能
            enhanceApplicationContext(configurableApplicationContext);
            
            // 阶段6: 验证初始化结果
            validateInitializationResult(configurableApplicationContext);
            
            // 记录初始化完成信息
            long processingTime = System.currentTimeMillis() - startTime;
            System.out.println("[TestApplicationContextInitializer] 应用上下文初始化完成 - 耗时: " + processingTime + "ms");
            System.out.println("[TestApplicationContextInitializer] 已添加属性源: " + CUSTOM_PROPERTY_SOURCE_NAME);
            System.out.println("[TestApplicationContextInitializer] 当前环境激活的配置文件: " + String.join(", ", environment.getActiveProfiles()));
        } catch (Exception e) {
            System.err.println("[TestApplicationContextInitializer] 初始化应用上下文时发生异常: " + e.getMessage());
            e.printStackTrace();
            // 即使发生异常，也继续应用启动，确保容错性
        }
    }
    
    /**
     * 记录ApplicationContext基本信息
     */
    private void logApplicationContextInfo(ConfigurableApplicationContext context) {
        System.out.println("[TestApplicationContextInitializer] 应用上下文信息:");
        System.out.println("- 上下文类型: " + context.getClass().getSimpleName());
        System.out.println("- 是否为注解配置上下文: " + (context instanceof AnnotationConfigApplicationContext));
        System.out.println("- 环境类型: " + context.getEnvironment().getClass().getSimpleName());
        System.out.println("- 类加载器: " + context.getClassLoader().getClass().getSimpleName());
    }
    
    /**
     * 添加核心配置属性
     */
    private void addCoreProperties(ConfigurableEnvironment environment) {
        MutablePropertySources propertySources = environment.getPropertySources();
        
        // 检查是否已存在相同名称的属性源
        boolean propertySourceExists = false;
        for (PropertySource<?> source : propertySources) {
            if (CUSTOM_PROPERTY_SOURCE_NAME.equals(source.getName())) {
                propertySourceExists = true;
                break;
            }
        }
        
        if (!propertySourceExists) {
            // 创建扩展属性Map
            Map<String, Object> customProperties = new HashMap<>();
            
            // 1. 应用基本信息
            customProperties.put("extension.application.name", "spring-boot-extension-demo");
            customProperties.put("extension.application.version", "1.0.0");
            customProperties.put("extension.application.buildTimestamp", System.currentTimeMillis());
            customProperties.put("extension.application.startupTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // 2. 初始化器信息
            customProperties.put("extension.initializer.name", getClass().getSimpleName());
            customProperties.put("extension.initializer.order", Ordered.HIGHEST_PRECEDENCE + 10);
            
            // 3. 上下文配置
            customProperties.put("extension.context.initialized", "true");
            customProperties.put("extension.context.prepared", "true");
            
            // 4. 系统信息
            customProperties.put("extension.system.java.version", System.getProperty("java.version"));
            customProperties.put("extension.system.encoding", System.getProperty("file.encoding"));
            
            // 5. 创建并添加属性源
            MapPropertySource propertySource = new MapPropertySource(CUSTOM_PROPERTY_SOURCE_NAME, customProperties);
            propertySources.addFirst(propertySource);
            
            System.out.println("[TestApplicationContextInitializer] 添加了核心属性源: " + CUSTOM_PROPERTY_SOURCE_NAME);
            System.out.println("[TestApplicationContextInitializer] - 添加属性数量: " + customProperties.size());
            System.out.println("[TestApplicationContextInitializer] - 应用名称: " + customProperties.get("extension.application.name"));
        } else {
            System.out.println("[TestApplicationContextInitializer] 属性源已存在，跳过添加: " + CUSTOM_PROPERTY_SOURCE_NAME);
        }
    }
    
    /**
     * 确定环境类型
     */
    private String determineEnvironmentType(ConfigurableEnvironment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        
        if (activeProfiles != null && activeProfiles.length > 0) {
            for (String profile : activeProfiles) {
                if ("prod".equals(profile) || "production".equals(profile)) {
                    return "production";
                } else if ("test".equals(profile) || "testing".equals(profile)) {
                    return "testing";
                } else if ("dev".equals(profile) || "development".equals(profile)) {
                    return "development";
                }
            }
        }
        
        return "default";
    }
    
    /**
     * 根据环境类型配置上下文
     */
    private void configureContextByEnvironmentType(ConfigurableApplicationContext context, String environmentType) {
        System.out.println("[TestApplicationContextInitializer] 根据环境类型配置上下文: " + environmentType);
        
        // 根据不同环境设置不同的配置
        switch (environmentType) {
            case "production":
                configureProductionEnvironment(context);
                break;
            case "testing":
                configureTestingEnvironment(context);
                break;
            case "development":
            default:
                configureDevelopmentEnvironment(context);
                break;
        }
    }
    
    /**
     * 配置生产环境
     */
    private void configureProductionEnvironment(ConfigurableApplicationContext context) {
        System.out.println("[TestApplicationContextInitializer] 配置生产环境上下文");
        
        // 生产环境特定配置
        context.getEnvironment().getSystemProperties().put("spring.devtools.restart.enabled", "false");
        context.getEnvironment().getSystemProperties().put("spring.profiles.active", "prod");
        
        // 禁用调试日志
        System.setProperty("logging.level.com.zhuo.test", "INFO");
    }
    
    /**
     * 配置测试环境
     */
    private void configureTestingEnvironment(ConfigurableApplicationContext context) {
        System.out.println("[TestApplicationContextInitializer] 配置测试环境上下文");
        
        // 测试环境特定配置
        context.getEnvironment().getSystemProperties().put("spring.test.database.replace", "none");
    }
    
    /**
     * 配置开发环境
     */
    private void configureDevelopmentEnvironment(ConfigurableApplicationContext context) {
        System.out.println("[TestApplicationContextInitializer] 配置开发环境上下文");
        
        // 开发环境特定配置
        context.getEnvironment().getSystemProperties().put("spring.devtools.restart.enabled", "true");
        context.getEnvironment().getSystemProperties().put("spring.profiles.active", "dev");
        
        // 启用调试日志
        System.setProperty("logging.level.com.zhuo.test", "DEBUG");
    }
    
    /**
     * 预加载关键资源
     */
    private void preloadCriticalResources(ConfigurableApplicationContext context) {
        System.out.println("[TestApplicationContextInitializer] 开始预加载关键资源");
        
        try {
            ResourceLoader resourceLoader = context;
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
            
            // 预加载配置文件和资源
            for (String packageName : CRITICAL_BEAN_PACKAGES) {
                String packagePath = "classpath*:" + packageName.replace('.', '/') + "/**/*.class";
                try {
                    Resource[] resources = resolver.getResources(packagePath);
                    System.out.println("[TestApplicationContextInitializer] 预加载包资源: " + packageName + " - 找到 " + resources.length + " 个类文件");
                } catch (IOException e) {
                    System.err.println("[TestApplicationContextInitializer] 预加载资源失败: " + packagePath + " - 错误: " + e.getMessage());
                    // 继续预加载其他资源
                }
            }
        } catch (Exception e) {
            System.err.println("[TestApplicationContextInitializer] 预加载关键资源时发生异常: " + e.getMessage());
            // 即使发生异常也继续执行
        }
        
        System.out.println("[TestApplicationContextInitializer] 关键资源预加载完成");
    }
    
    /**
     * 增强ApplicationContext功能
     */
    private void enhanceApplicationContext(ConfigurableApplicationContext context) {
        System.out.println("[TestApplicationContextInitializer] 增强ApplicationContext功能");
        
        // 设置初始化上下文对象到应用上下文中
        context.getBeanFactory().registerSingleton(INITIALIZATION_CONTEXT_KEY, new InitializationContext());
        
        // 为特定类型的Bean设置自定义行为
        // 在实际应用中，这里可以添加更多的上下文增强功能
        
        System.out.println("[TestApplicationContextInitializer] ApplicationContext功能增强完成");
    }
    
    /**
     * 验证初始化结果
     */
    private void validateInitializationResult(ConfigurableApplicationContext context) {
        System.out.println("[TestApplicationContextInitializer] 验证初始化结果");
        
        ConfigurableEnvironment environment = context.getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        
        // 验证属性源是否已正确添加
        boolean propertySourceExists = false;
        for (PropertySource<?> source : propertySources) {
            if (CUSTOM_PROPERTY_SOURCE_NAME.equals(source.getName())) {
                propertySourceExists = true;
                break;
            }
        }
        
        if (propertySourceExists) {
            System.out.println("[TestApplicationContextInitializer] 验证通过: 属性源已成功添加");
        } else {
            System.out.println("[TestApplicationContextInitializer] 验证失败: 属性源未添加成功");
        }
        
        // 验证关键属性是否存在
        boolean criticalPropertyExists = environment.containsProperty("extension.application.name");
        if (criticalPropertyExists) {
            System.out.println("[TestApplicationContextInitializer] 验证通过: 关键属性存在");
        } else {
            System.out.println("[TestApplicationContextInitializer] 验证失败: 关键属性不存在");
        }
    }
    
    /**
     * 初始化上下文内部类，用于存储初始化过程中的状态和信息
     */
    public static class InitializationContext {
        private final Map<String, Object> attributes = new HashMap<>();
        private final List<String> initializationSteps = new ArrayList<>();
        private final LocalDateTime initializationTime = LocalDateTime.now();
        
        public void addAttribute(String key, Object value) {
            attributes.put(key, value);
        }
        
        public Object getAttribute(String key) {
            return attributes.get(key);
        }
        
        public void addInitializationStep(String step) {
            initializationSteps.add(step);
        }
        
        public List<String> getInitializationSteps() {
            return Collections.unmodifiableList(initializationSteps);
        }
        
        public LocalDateTime getInitializationTime() {
            return initializationTime;
        }
        
        public int getAttributeCount() {
            return attributes.size();
        }
        
        public int getStepCount() {
            return initializationSteps.size();
        }
    }
}
