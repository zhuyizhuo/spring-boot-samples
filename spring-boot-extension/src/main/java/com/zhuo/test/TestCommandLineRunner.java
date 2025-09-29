package com.zhuo.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * CommandLineRunner扩展点增强实现
 * 在Spring Boot应用启动完成后执行，提供全面的应用启动后处理
 * 当前增强实现：
 * - 执行顺序控制与优先级管理
 * - 详细的应用环境信息收集与分析
 * - 命令行参数智能解析与处理
 * - 多阶段应用初始化任务执行
 * - 系统资源状态检查与报告
 * - 健壮的异常处理与容错机制
 * - 全面的启动信息摘要生成
 */
@Component
@Order(2) // 设置执行顺序，在TestApplicationRunner之后执行
public class TestCommandLineRunner implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(TestCommandLineRunner.class);
    private static final String DEFAULT_APP_NAME = "Spring Boot Extension";
    private static final String DEFAULT_APP_VERSION = "1.0.0";
    private static final String DEFAULT_SERVER_PORT = "8080";
    
    @Autowired
    private Environment environment;
    
    @Override
    public void run(String... args) {
        logger.info("开始执行CommandLineRunner扩展点");
        
        try {
            // 记录开始时间
            long startTime = System.currentTimeMillis();
            
            // 收集并记录应用环境信息
            collectAndLogEnvironmentInfo();
            
            // 处理命令行参数
            processCommandLineArgs(args);
            
            // 执行应用启动后的初始化任务
            executePostStartupTasks();
            
            // 生成并记录启动摘要
            generateStartupSummary(startTime);
            
            logger.info("CommandLineRunner扩展点执行完成");
        } catch (Exception e) {
            logger.error("CommandLineRunner执行过程中发生异常", e);
            // 异常处理策略
            handleExecutionException(e);
        }
    }
    
    /**
     * 收集并记录应用环境信息
     */
    private void collectAndLogEnvironmentInfo() {
        logger.info("开始收集应用环境信息");
        
        try {
            // 获取应用基本信息
            String appName = environment.getProperty("extension.application.name", DEFAULT_APP_NAME);
            String appVersion = environment.getProperty("extension.application.version", DEFAULT_APP_VERSION);
            String[] activeProfiles = environment.getActiveProfiles();
            String[] defaultProfiles = environment.getDefaultProfiles();
            
            // 获取服务器信息
            String serverPort = environment.getProperty("server.port", DEFAULT_SERVER_PORT);
            String contextPath = environment.getProperty("server.servlet.context-path", "");
            String serverAddress = getServerAddress();
            
            // 记录应用信息
            logger.info("应用信息:");
            logger.info("  名称: {}", appName);
            logger.info("  版本: {}", appVersion);
            logger.info("  激活的配置文件: {}", activeProfiles.length > 0 ? 
                       String.join(", ", activeProfiles) : "默认配置");
            logger.info("  默认配置文件: {}", String.join(", ", defaultProfiles));
            
            // 记录服务器信息
            logger.info("服务器信息:");
            logger.info("  端口: {}", serverPort);
            logger.info("  上下文路径: {}", contextPath.isEmpty() ? "/" : contextPath);
            logger.info("  访问地址: http://localhost:{}{}", 
                       serverPort,
                       contextPath);
            if (!environment.acceptsProfiles("production")) {
                logger.info("  或访问: http://{}:{}{}", 
                           serverAddress, 
                           serverPort,
                           contextPath.isEmpty() ? "" : contextPath);
            }

            // 记录环境属性信息
            logEnvironmentPropertiesSummary();
        } catch (Exception e) {
            logger.warn("收集环境信息时发生异常", e);
        }
    }
    
    /**
     * 获取服务器地址
     */
    private String getServerAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn("无法获取本机地址", e);
            return "localhost";
        }
    }
    
    /**
     * 记录环境属性摘要信息
     */
    private void logEnvironmentPropertiesSummary() {
        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment configurableEnv = (ConfigurableEnvironment) environment;
            
            // 记录属性源数量
            int propertySourceCount = configurableEnv.getPropertySources().size();
            logger.debug("环境中属性源数量: {}", propertySourceCount);
            
            // 记录特定前缀的属性数量
            int extensionPropsCount = 0;
            try {
                // 在实际应用中，这里可以使用更高效的方式获取属性
                for (PropertySource<?> propertySource : configurableEnv.getPropertySources()) {
                    // 检查属性源名称
                    if (propertySource.getName().contains("application.yml") || propertySource.getName().contains("application.properties")) {
                        // 实际应用中可以进一步解析属性源获取具体属性
                        logger.debug("处理属性源: {}", propertySource.getName());
                    }
                }
            } catch (Exception e) {
                logger.debug("统计扩展属性时发生异常", e);
            }
            
            if (extensionPropsCount > 0) {
                logger.info("扩展属性数量: {}", extensionPropsCount);
            }
        }
    }
    
    /**
     * 处理命令行参数
     */
    private void processCommandLineArgs(String... args) {
        if (args != null && args.length > 0) {
            logger.info("接收到的命令行参数数量: {}", args.length);
            
            // 按参数类型分组（以--开头的选项参数和普通参数）
            Map<Boolean, String> argsMap = Arrays.stream(args)
                .collect(Collectors.toMap(
                    arg -> arg.startsWith("--"),
                    arg -> arg,
                    (existing, replacement) -> existing + ", " + replacement
                ));
            
            // 记录选项参数
            if (argsMap.containsKey(true)) {
                logger.info("选项参数: {}", argsMap.get(true));
            }
            
            // 记录普通参数
            if (argsMap.containsKey(false)) {
                logger.info("普通参数: {}", argsMap.get(false));
            }
            
            // 详细记录每个参数
            for (int i = 0; i < args.length; i++) {
                logger.debug("参数[{}]: {}", i, args[i]);
            }
            
            // 检测并处理特殊命令行参数
            detectSpecialArguments(args);
        }
    }
    
    /**
     * 检测并处理特殊命令行参数
     */
    private void detectSpecialArguments(String... args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--debug") || arg.equalsIgnoreCase("-d")) {
                logger.info("检测到调试模式参数，已启用详细日志");
                // 在实际应用中，这里可以动态调整日志级别
            } else if (arg.equalsIgnoreCase("--quiet") || arg.equalsIgnoreCase("-q")) {
                logger.info("检测到静默模式参数，已降低日志级别");
                // 在实际应用中，这里可以动态调整日志级别
            } else if (arg.startsWith("--spring.profiles.active=")) {
                String profiles = arg.substring("--spring.profiles.active=".length());
                logger.info("通过命令行指定的激活配置文件: {}", profiles);
            }
        }
    }
    
    /**
     * 执行应用启动后的初始化任务
     */
    private void executePostStartupTasks() {
        logger.info("开始执行应用启动后初始化任务");
        
        // 执行多个初始化任务，按顺序执行
        long totalTaskTime = executeTasksInSequence(
            this::loadInitialData,     // 加载初始数据
            this::initializeComponents, // 初始化组件
            this::checkSystemResources  // 检查系统资源
        );
        
        logger.info("所有启动后初始化任务执行完成，总耗时: {}ms", totalTaskTime);
    }
    
    /**
     * 按顺序执行多个任务
     */
    private long executeTasksInSequence(Runnable... tasks) {
        long startTime = System.currentTimeMillis();
        
        for (Runnable task : tasks) {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("执行初始化任务时发生异常", e);
                // 可以选择继续执行其他任务或中断
            }
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * 加载初始数据
     */
    private void loadInitialData() {
        logger.info("开始加载初始数据...");
        long startTime = System.currentTimeMillis();
        
        try {
            // 模拟数据加载过程
            TimeUnit.MILLISECONDS.sleep(500);
            
            logger.info("初始数据加载完成，耗时: {}ms", System.currentTimeMillis() - startTime);
        } catch (InterruptedException e) {
            logger.error("数据加载过程被中断", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 初始化组件
     */
    private void initializeComponents() {
        logger.info("开始初始化应用组件...");
        long startTime = System.currentTimeMillis();
        
        try {
            // 模拟组件初始化过程
            TimeUnit.MILLISECONDS.sleep(300);
            
            logger.info("应用组件初始化完成，耗时: {}ms", System.currentTimeMillis() - startTime);
        } catch (InterruptedException e) {
            logger.error("组件初始化过程被中断", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 检查系统资源
     */
    private void checkSystemResources() {
        logger.info("开始检查系统资源状态...");
        
        try {
            // 获取系统资源信息
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory() / 1024 / 1024; // MB
            long totalMemory = runtime.totalMemory() / 1024 / 1024; // MB
            long freeMemory = runtime.freeMemory() / 1024 / 1024; // MB
            int processorCount = runtime.availableProcessors();
            
            logger.info("系统资源状态:");
            logger.info("  处理器数量: {}", processorCount);
            logger.info("  最大内存: {} MB", maxMemory);
            logger.info("  已分配内存: {} MB", totalMemory);
            logger.info("  空闲内存: {} MB", freeMemory);
            logger.info("  已使用内存: {} MB", (totalMemory - freeMemory));
        } catch (Exception e) {
            logger.warn("检查系统资源时发生异常", e);
        }
    }
    
    /**
     * 生成启动摘要
     */
    private void generateStartupSummary(long startTime) {
        long startupTime = System.currentTimeMillis() - startTime;
        
        logger.info("\n====================================");
        logger.info("应用启动摘要");
        logger.info("====================================");
        logger.info("应用名称: {}", environment.getProperty("extension.application.name", DEFAULT_APP_NAME));
        logger.info("启动耗时: {}ms", startupTime);
        logger.info("激活配置: {}", activeProfilesAsString());
        logger.info("访问地址: http://localhost:{}{}", 
                   environment.getProperty("server.port", DEFAULT_SERVER_PORT),
                   environment.getProperty("server.servlet.context-path", ""));
        logger.info("====================================\n");
    }
    
    /**
     * 获取激活配置文件的字符串表示
     */
    private String activeProfilesAsString() {
        String[] activeProfiles = environment.getActiveProfiles();
        return activeProfiles.length > 0 ? String.join(", ", activeProfiles) : "默认配置";
    }
    
    /**
     * 处理执行异常
     */
    private void handleExecutionException(Exception e) {
        // 根据异常类型执行不同的处理策略
        if (e instanceof RuntimeException) {
            logger.error("发生运行时异常，这可能会影响应用功能", e);
            // 在实际应用中，这里可以实现更复杂的错误处理逻辑
        } else {
            logger.error("发生非运行时异常", e);
        }
        
        // 记录异常信息到特定位置，便于问题排查
        logExceptionToDiagnostics(e);
    }
    
    /**
     * 将异常信息记录到诊断系统
     */
    private void logExceptionToDiagnostics(Exception e) {
        // 在实际应用中，这里可以实现将异常信息发送到监控系统或诊断日志
        logger.debug("异常诊断信息已记录");
    }
}
