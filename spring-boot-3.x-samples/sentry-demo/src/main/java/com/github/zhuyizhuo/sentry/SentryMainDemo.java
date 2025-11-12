package com.github.zhuyizhuo.sentry;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Sentry 异常上报示例 - 独立运行的main方法
 * 从application.yml读取DSN配置并上报异常
 */
public class SentryMainDemo {

    public static void main(String[] args) {
        System.out.println("开始Sentry异常上报演示...");
        
        try {
            // 从配置文件读取DSN
            String dsn = readDsnFromConfig();
            
            // 如果配置文件读取失败，使用备用DSN
            if (dsn == null || dsn.trim().isEmpty()) {
                System.err.println("警告: 未从配置文件中找到有效的DSN，使用备用DSN");
                // dsn = "https://123456";
            }
            
            System.out.println("使用DSN: " + maskDsn(dsn));

            // 初始化Sentry
            initSentry(dsn);
            System.out.println("Sentry初始化完成");
            
            // 添加上下文信息
            addContextInfo();
            
            // 上报测试异常
            reportTestException();
            
            System.out.println("异常上报完成，请在Sentry控制台查看");

            // 确保事件被发送
            Sentry.flush(5000);

        } catch (Exception e) {
            System.err.println("Sentry演示过程中发生错误:");
            e.printStackTrace();
        } finally {
            // 清理资源
            Sentry.close();
        }
    }

    /**
     * 从配置文件读取DSN
     */
    private static String readDsnFromConfig() {
        // 尝试多个可能的配置文件路径
        String[] configPaths = {
            "application.yml",
            "src/main/resources/application.yml",
            "application.properties"
        };
        
        for (String configPath : configPaths) {
            try {
                System.out.println("尝试从配置文件读取DSN: " + configPath);
                
                // 先尝试通过文件系统读取
                java.io.File file = new java.io.File(configPath);
                if (file.exists() && file.isFile()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String dsn = extractDsnFromLine(line);
                            if (dsn != null) {
                                System.out.println("成功从文件读取DSN: " + configPath);
                                return dsn;
                            }
                        }
                    }
                }
                
                // 再尝试通过类路径读取
                try (InputStreamReader isr = new InputStreamReader(
                        SentryMainDemo.class.getClassLoader().getResourceAsStream(configPath), "UTF-8");
                     BufferedReader reader = new BufferedReader(isr)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String dsn = extractDsnFromLine(line);
                        if (dsn != null) {
                            System.out.println("成功从类路径读取DSN: " + configPath);
                            return dsn;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("读取配置文件失败: " + configPath + ", 错误: " + e.getMessage());
                // 继续尝试下一个配置文件
            }
        }
        
        return null;
    }
    
    /**
     * 从一行文本中提取DSN值
     */
    private static String extractDsnFromLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        line = line.trim();
        // 检查是否包含DSN配置
        if (line.startsWith("dsn:") || line.contains("dsn=")) {
            // YAML格式处理
            if (line.contains(":")) {
                int colonIndex = line.indexOf(":");
                String value = line.substring(colonIndex + 1).trim();
                return cleanDsnValue(value);
            }
            // Properties格式处理
            else if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length > 1) {
                    return cleanDsnValue(parts[1].trim());
                }
            }
        }
        return null;
    }
    
    /**
     * 清理DSN值，移除引号和多余空格
     */
    private static String cleanDsnValue(String value) {
        // 移除可能的引号
        if ((value.startsWith("\"") && value.endsWith("\"")) || 
            (value.startsWith("'") && value.endsWith("'"))) {
            value = value.substring(1, value.length() - 1);
        }
        // 移除前后空格
        return value.trim();
    }
    
    /**
     * 初始化Sentry
     */
    private static void initSentry(String dsn) {
        Sentry.init(options -> {
            options.setDsn(dsn);
            options.setEnvironment("development");
            options.setRelease("1.0.0");
            options.setTracesSampleRate(1.0);
            options.setSampleRate(1.0);
            options.setDebug(true);
            options.setAttachStacktrace(true);
            options.setSendDefaultPii(true);
        });
    }

    /**
     * 添加上下文信息
     */
    private static void addContextInfo() {
        // 添加面包屑
        Sentry.addBreadcrumb("应用启动");
        Sentry.addBreadcrumb("准备上报异常");

        // 设置额外信息
        Sentry.setExtra("application", "SentryMainDemo");
        Sentry.setExtra("java.version", System.getProperty("java.version"));
        Sentry.setExtra("os.name", System.getProperty("os.name"));

        // 设置标签
        Sentry.setTag("environment", "development");
        Sentry.setTag("source", "main-method");
    }

    /**
     * 上报测试异常
     */
    private static void reportTestException() {
        try {
            // 1. 上报消息
            System.out.println("上报测试消息...");
            Sentry.captureMessage("这是从main方法发送的测试消息", SentryLevel.INFO);

            // 2. 手动创建并上报异常
            System.out.println("上报除零异常...");
            try {
                int result = 1 / 0; // 故意触发除零异常
            } catch (Exception e) {
                // 上报异常并添加额外上下文
                Sentry.captureException(e, scope -> {
                    scope.setExtra("exception_type", "除零异常");
                    scope.setExtra("operation", "division_by_zero");
                    scope.setLevel(SentryLevel.ERROR);
                });
                System.out.println("除零异常已上报: " + e.getMessage());
            }

            // 3. 上报自定义异常
            System.out.println("上报自定义异常...");
            RuntimeException customException = new RuntimeException("这是一个自定义运行时异常");
            customException.initCause(new IllegalArgumentException("参数错误导致的异常"));
            Sentry.captureException(customException);

        } catch (Exception e) {
            System.err.println("上报异常时发生错误:");
            e.printStackTrace();
        }
    }

    /**
     * 掩码DSN以保护敏感信息
     */
    private static String maskDsn(String dsn) {
        if (dsn == null || !dsn.contains("@")) {
            return dsn;
        }
        int atIndex = dsn.indexOf("@");
        int protocolEndIndex = dsn.indexOf("://") + 3;
        
        // 掩码DSN密钥部分
        return dsn.substring(0, protocolEndIndex) + "***" + dsn.substring(atIndex);
    }
}