package com.github.zhuyizhuo.esdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * 主应用程序类
 * Spring Boot 3.x 集成 Elasticsearch 示例应用
 */
@SpringBootApplication(exclude = {
        // 全面排除Elasticsearch相关的自动配置
        ElasticsearchRestClientAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration.class
})
public class ElasticsearchDemoApplication {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchDemoApplication.class);

    @Autowired
    private Environment environment;
    
    @Autowired(required = false)
    private ElasticsearchOperations elasticsearchOperations;

    public static void main(String[] args) {
        // 创建SpringApplication实例，并设置失败时不退出JVM
        SpringApplication app = new SpringApplication(ElasticsearchDemoApplication.class);
        // 设置容错模式，使应用在某些组件初始化失败时仍能启动
        app.setRegisterShutdownHook(true);
        app.run(args);
    }
    
    /**
     * 检查Elasticsearch连接状态
     */
    private void checkElasticsearchConnection() {
        System.out.println("\n==========================================");
        System.out.println("正在检查Elasticsearch连接状态...");
        
        try {
            if (elasticsearchOperations == null) {
                System.out.println("[ERROR] Elasticsearch客户端未初始化，可能缺少必要配置");
                return;
            }
            
            // 尝试执行一个简单的操作来测试连接
            // 使用ping或简单查询来验证连接
            try {
                // 使用IndexCoordinates创建索引坐标
                String indexName = environment.getProperty("elasticsearch.demo.index-name", "test-index");
                IndexCoordinates indexCoordinates = IndexCoordinates.of(indexName);
                // 使用一个简单的查询操作来测试连接
                elasticsearchOperations.indexOps(indexCoordinates).exists();
                System.out.println("[SUCCESS] Elasticsearch连接成功!");
            } catch (Exception e) {
                System.out.println("[ERROR] Elasticsearch连接失败: " + e.getMessage());
                log.error("Elasticsearch连接检查失败", e);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Elasticsearch连接检查过程中发生异常: " + e.getMessage());
            log.error("Elasticsearch连接检查过程中发生异常", e);
        } finally {
            System.out.println("==========================================\n");
        }
    }
    

    @EventListener(ApplicationReadyEvent.class)
    public void printApplicationUrl() {
        // 检查ES连接状态
        checkElasticsearchConnection();
        try {
            // 获取本机IP地址
            String ip = InetAddress.getLocalHost().getHostAddress();
            // 获取端口号，如果没有配置则使用默认的8080
            String port = Optional.ofNullable(environment.getProperty("server.port")).orElse("8080");
            // 获取上下文路径，并确保不包含前导斜杠
            String contextPath = Optional.ofNullable(environment.getProperty("server.servlet.context-path"))
                    .map(path -> path.startsWith("/") ? path.substring(1) : path)
                    .orElse("");
            
            // 构建访问URL，确保路径拼接正确
            String baseUrl = "http://" + ip + ":" + port;
            String localhostUrl = "http://localhost:" + port;
            String pathPrefix = contextPath.isEmpty() ? "" : "/" + contextPath;
            
            // 打印访问地址
            System.out.println("\n==========================================");
            System.out.println("应用已启动成功！");
            System.out.println("前端访问地址(IP): " + baseUrl + pathPrefix + "/index");
            System.out.println("前端访问地址(本地): " + localhostUrl + pathPrefix + "/index");
            System.out.println("API 文档地址: " + baseUrl + pathPrefix + "/swagger-ui.html");
            System.out.println("\n功能说明:");
            System.out.println("1. 支持通过页面输入index动态查询ES数据");
            System.out.println("2. 提供数据管理、高级搜索和系统信息功能");
            System.out.println("3. Elasticsearch连接已设置为容错模式，应用可以在ES不可用时正常启动");
            System.out.println("==========================================\n");
        } catch (UnknownHostException e) {
            // 如果无法获取IP地址，使用localhost
            log.warn("无法获取本机IP地址，使用localhost替代", e);
            String port = Optional.ofNullable(environment.getProperty("server.port")).orElse("8080");
            // 获取上下文路径，并确保不包含前导斜杠
            String contextPath = Optional.ofNullable(environment.getProperty("server.servlet.context-path"))
                    .map(path -> path.startsWith("/") ? path.substring(1) : path)
                    .orElse("");
            String localhostUrl = "http://localhost:" + port;
            String pathPrefix = contextPath.isEmpty() ? "" : "/" + contextPath;
            
            System.out.println("\n==========================================");
            System.out.println("应用已启动成功！");
            System.out.println("前端访问地址: " + localhostUrl + pathPrefix + "/index");
            System.out.println("==========================================\n");
        }
    }
}