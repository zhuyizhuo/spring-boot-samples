package com.github.zhuyizhuo.kafka.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Spring Boot应用程序主类
 */
@SpringBootApplication
public class KafkaDemoApplication {

    private final Environment environment;
    
    public KafkaDemoApplication(Environment environment) {
        this.environment = environment;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
    }
    
    /**
     * 应用程序启动后打印访问地址
     */
    @EventListener(ApplicationReadyEvent.class)
    public void printAccessInfo() throws UnknownHostException {
        String protocol = "http";
        String serverPort = environment.getProperty("server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        
        String baseUrl = protocol + "://" + hostAddress + ":" + serverPort + contextPath;
        String homeUrl = baseUrl + "/";
        String swaggerUrl = baseUrl + "/swagger-ui.html";
        
        System.out.println();
        System.out.println("==============================================");
        System.out.println("Kafka Demo Application 启动成功!");
        System.out.println("==============================================");
        System.out.println("访问地址:");
        System.out.println("- 首页: " + homeUrl);
        System.out.println("- API文档: " + swaggerUrl);
        System.out.println("==============================================");
        System.out.println();
    }
}