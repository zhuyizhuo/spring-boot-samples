package com.zhuo.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * ApplicationRunner扩展点增强实现
 * 在Spring Boot应用启动完成后执行，提供更高级的命令行参数访问和初始化任务管理
 * 当前增强实现：
 * - 细粒度参数验证和处理
 * - 多阶段初始化任务执行
 * - 健壮的异常处理和日志记录
 * - 参数优先级管理和配置覆盖机制
 * - 初始化任务的执行监控
 */
@Component
@Order(1) // 控制多个ApplicationRunner的执行顺序
public class TestApplicationRunner implements ApplicationRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(TestApplicationRunner.class);
    private static final Set<String> VALIDATED_PARAMS = new HashSet<>(Arrays.asList("app.mode", "app.profile", "feature.toggle"));
    private static final String DEFAULT_PROFILE = "development";
    
    @Autowired
    private TestFactoryBean.ConfigManager configManager;
    
    @Override
    public void run(ApplicationArguments args) {
        logger.info("开始执行ApplicationRunner扩展点");
        
        try {
            // 访问配置管理器
            logger.debug("配置管理器信息: {}", configManager.toString());
            
            // 获取并处理选项参数（--key=value形式）
            processOptionArgs(args);
            
            // 获取非选项参数（普通参数）
            processNonOptionArgs(args);
            
            // 执行初始化任务链
            executeInitializationTasks(args);
            
            // 记录执行结果
            logger.info("ApplicationRunner执行完成，当前配置项数量: {}", configManager.getConfigCount());
            logExecutionSummary();
        } catch (Exception e) {
            logger.error("ApplicationRunner执行过程中发生异常", e);
            // 根据异常类型决定是否需要中断应用启动
            handleCriticalException(e);
        }
    }
    
    /**
     * 处理选项参数并进行验证
     */
    private void processOptionArgs(ApplicationArguments args) {
        Set<String> optionNames = args.getOptionNames();
        if (!CollectionUtils.isEmpty(optionNames)) {
            logger.info("接收到的选项参数数量: {}", optionNames.size());
            
            // 按参数类型分组处理
            Map<Boolean, List<String>> paramGroups = optionNames.stream()
                .collect(Collectors.partitioningBy(VALIDATED_PARAMS::contains));
            
            // 处理需要验证的参数
            if (!CollectionUtils.isEmpty(paramGroups.get(true))) {
                logger.info("开始处理需要验证的参数");
                for (String optionName : paramGroups.get(true)) {
                    List<String> values = args.getOptionValues(optionName);
                    validateAndProcessParam(optionName, values);
                }
            }
            
            // 处理其他参数
            if (!CollectionUtils.isEmpty(paramGroups.get(false))) {
                logger.info("开始处理其他参数");
                for (String optionName : paramGroups.get(false)) {
                    List<String> values = args.getOptionValues(optionName);
                    String valueStr = String.join(",", values);
                    configManager.setConfig(optionName, valueStr);
                    logger.debug("设置参数: {} = {}", optionName, valueStr);
                }
            }
        }
    }
    
    /**
     * 验证并处理特定参数
     */
    private void validateAndProcessParam(String paramName, List<String> values) {
        try {
            String valueStr = String.join(",", values);
            
            // 根据参数名称执行特定验证
            if ("app.mode".equals(paramName)) {
                validateAppMode(valueStr);
            } else if ("app.profile".equals(paramName)) {
                // 应用配置文件参数，设置到配置管理器
                configManager.setConfig(paramName, valueStr);
                logger.info("已设置应用配置文件: {}", valueStr);
            } else if ("feature.toggle".equals(paramName)) {
                // 特性开关参数处理
                processFeatureToggle(valueStr);
            }
        } catch (Exception e) {
            logger.warn("参数验证失败: {}, 使用默认值", paramName, e);
            // 设置默认值
            setDefaultValueForParam(paramName);
        }
    }
    
    /**
     * 验证应用模式参数
     */
    private void validateAppMode(String mode) {
        Set<String> validModes = new HashSet<>(Arrays.asList("development", "test", "production"));
        if (validModes.contains(mode)) {
            configManager.setConfig("app.mode", mode);
            logger.info("已设置应用模式: {}", mode);
        } else {
            throw new IllegalArgumentException("无效的应用模式: " + mode + ", 有效值: " + validModes);
        }
    }
    
    /**
     * 处理特性开关参数
     */
    private void processFeatureToggle(String features) {
        String[] featureList = features.split(",");
        for (String feature : featureList) {
            String[] parts = feature.split(":");
            if (parts.length == 2) {
                String featureName = parts[0].trim();
                String featureValue = parts[1].trim();
                configManager.setConfig("feature." + featureName, featureValue);
                logger.info("已设置特性开关: {} = {}", featureName, featureValue);
            }
        }
    }
    
    /**
     * 为参数设置默认值
     */
    private void setDefaultValueForParam(String paramName) {
        switch (paramName) {
            case "app.mode":
                configManager.setConfig("app.mode", "development");
                break;
            case "app.profile":
                configManager.setConfig("app.profile", DEFAULT_PROFILE);
                break;
            default:
                logger.debug("没有为参数 {} 设置默认值", paramName);
        }
    }
    
    /**
     * 处理非选项参数
     */
    private void processNonOptionArgs(ApplicationArguments args) {
        List<String> nonOptionArgsList = args.getNonOptionArgs();
        if (nonOptionArgsList != null && !nonOptionArgsList.isEmpty()) {
            logger.info("接收到的非选项参数数量: {}", nonOptionArgsList.size());
            // 转换List<String>为String数组
            String[] nonOptionArgs = nonOptionArgsList.toArray(new String[0]);
            configManager.setConfig("commandLineArgs", String.join(",", nonOptionArgs));
            logger.debug("非选项参数: {}", String.join(", ", nonOptionArgs));
        }
    }
    
    /**
     * 执行初始化任务链
     */
    private void executeInitializationTasks(ApplicationArguments args) {
        logger.info("开始执行初始化任务链");
        
        // 检查是否需要执行数据初始化
        if (args.containsOption("init-data")) {
            logger.info("检测到init-data参数，执行数据初始化操作");
            initializeData();
        }
        
        // 检查是否需要执行缓存预热
        if (args.containsOption("warm-cache")) {
            logger.info("检测到warm-cache参数，执行缓存预热操作");
            warmupCache();
        }
        
        // 检查是否需要执行系统自检
        if (args.containsOption("self-check")) {
            logger.info("检测到self-check参数，执行系统自检操作");
            performSelfCheck();
        }
        
        // 执行默认初始化任务
        performDefaultInitialization();
    }
    
    /**
     * 初始化数据操作
     */
    private void initializeData() {
        logger.info("开始执行数据初始化...");
        long startTime = System.currentTimeMillis();
        
        try {
            // 模拟数据初始化过程
            TimeUnit.MILLISECONDS.sleep(800);
            
            // 记录初始化结果
            configManager.setConfig("data.initialized", "true");
            configManager.setConfig("data.initTime", String.valueOf(System.currentTimeMillis() - startTime));
            
            logger.info("数据初始化完成，耗时: {}ms", System.currentTimeMillis() - startTime);
        } catch (InterruptedException e) {
            logger.error("数据初始化过程被中断", e);
            Thread.currentThread().interrupt();
            // 设置初始化失败标记
            configManager.setConfig("data.initialized", "false");
        } catch (Exception e) {
            logger.error("数据初始化过程中发生异常", e);
            // 设置初始化失败标记
            configManager.setConfig("data.initialized", "false");
        }
    }
    
    /**
     * 缓存预热操作
     */
    private void warmupCache() {
        logger.info("开始执行缓存预热...");
        long startTime = System.currentTimeMillis();
        
        try {
            // 模拟缓存预热过程
            TimeUnit.MILLISECONDS.sleep(300);
            
            // 记录缓存预热结果
            configManager.setConfig("cache.warmed", "true");
            configManager.setConfig("cache.warmupTime", String.valueOf(System.currentTimeMillis() - startTime));
            
            logger.info("缓存预热完成，耗时: {}ms", System.currentTimeMillis() - startTime);
        } catch (InterruptedException e) {
            logger.error("缓存预热过程被中断", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 执行系统自检
     */
    private void performSelfCheck() {
        logger.info("开始执行系统自检...");
        
        try {
            // 模拟系统自检过程
            boolean systemHealthy = true;
            
            // 检查关键配置是否存在
            String appMode = configManager.getConfig("app.mode", "unknown");
            String appProfile = configManager.getConfig("app.profile", DEFAULT_PROFILE);
            
            // 记录自检结果
            configManager.setConfig("system.selfCheck.passed", String.valueOf(systemHealthy));
            configManager.setConfig("system.selfCheck.time", String.valueOf(System.currentTimeMillis()));
            
            logger.info("系统自检完成，状态: {}, 应用模式: {}, 配置文件: {}", 
                        systemHealthy ? "健康" : "异常", appMode, appProfile);
        } catch (Exception e) {
            logger.error("系统自检过程中发生异常", e);
            configManager.setConfig("system.selfCheck.passed", "false");
        }
    }
    
    /**
     * 执行默认初始化任务
     */
    private void performDefaultInitialization() {
        logger.info("执行默认初始化任务...");
        
        try {
            // 设置初始化完成时间
            configManager.setConfig("application.initializedAt", String.valueOf(System.currentTimeMillis()));
            configManager.setConfig("application.initializationComplete", "true");
            
            logger.info("默认初始化任务执行完成");
        } catch (Exception e) {
            logger.error("默认初始化任务执行异常", e);
        }
    }
    
    /**
     * 记录执行摘要信息
     */
    private void logExecutionSummary() {
        logger.info("ApplicationRunner执行摘要:");
        logger.info("- 配置项总数: {}", configManager.getConfigCount());
        logger.info("- 应用模式: {}", configManager.getConfig("app.mode", "development"));
        logger.info("- 数据初始化状态: {}", configManager.getConfig("data.initialized", "未执行"));
        logger.info("- 缓存预热状态: {}", configManager.getConfig("cache.warmed", "未执行"));
        logger.info("- 系统自检状态: {}", configManager.getConfig("system.selfCheck.passed", "未执行"));
    }
    
    /**
     * 处理关键异常
     */
    private void handleCriticalException(Exception e) {
        // 根据异常类型决定处理策略
        if (e instanceof IllegalStateException || e instanceof SecurityException) {
            logger.error("发生严重异常，可能需要中断应用启动", e);
            // 在实际生产环境中，可能需要决定是否终止应用启动
            // System.exit(1);
        } else {
            logger.warn("发生非致命异常，应用将继续启动", e);
        }
    }
}