package com.github.zhuyizhuo.ai.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@SpringBootApplication
public class SpringAiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiDemoApplication.class, args);
    }

    // 配置RestTemplate用于HTTP请求，添加日志拦截器
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                System.out.println("=== [HTTP REQUEST] URL: " + request.getURI() + " ===");
                System.out.println("=== [HTTP REQUEST] Method: " + request.getMethod() + " ===");
                System.out.println("=== [HTTP REQUEST] Headers: " + request.getHeaders() + " ===");
                if (body != null && body.length > 0) {
                    System.out.println("=== [HTTP REQUEST] Body: " + new String(body, StandardCharsets.UTF_8) + " ===");
                }
                ClientHttpResponse response = execution.execute(request, body);
                System.out.println("=== [HTTP RESPONSE] Status: " + response.getStatusCode() + " ===");
                return response;
            }
        });
        return restTemplate;
    }

    // 配置OpenAiApi用于AI服务
    @Bean
    public OpenAiApi openAiApi(
            @Value("${spring.ai.openai.base-url:}") String baseUrl,
            @Value("${spring.ai.openai.api-key}") String apiKey) {
        // 如果baseUrl为空，使用硅基流动的默认URL
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = "https://api.siliconflow.cn";
        }
        
        // 确保base-url不以斜杠结尾（Spring AI会自动添加路径）
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        
        // 如果base-url包含/v1，移除它（Spring AI会自动添加/v1）
        if (baseUrl.endsWith("/v1")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 3);
            System.out.println("=== [CONFIG] Removed /v1 from base-url, new base-url: " + baseUrl + " ===");
        }
        
        System.out.println("=== [CONFIG] OpenAiApi Base URL: " + baseUrl + " ===");
        System.out.println("=== [CONFIG] OpenAiApi API Key: " + (apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) + "..." : "null") + " ===");
        System.out.println("=== [CONFIG] Expected API Endpoint: " + baseUrl + "/chat/completions ===");
        
        return new OpenAiApi(baseUrl, apiKey);
    }
    
    // 配置OpenAiChatModel用于AI聊天
    @Bean
    public OpenAiChatModel openAiChatModel(
            OpenAiApi openAiApi,
            @Value("${spring.ai.openai.chat.options.model:gpt-4o}") String model,
            @Value("${spring.ai.openai.chat.options.temperature:0.7}") Double temperature,
            @Value("${spring.ai.openai.chat.options.max-tokens:2048}") Integer maxTokens) {
        System.out.println("=== [CONFIG] OpenAiChatModel Model: " + model + " ===");
        System.out.println("=== [CONFIG] OpenAiChatModel Temperature: " + temperature + " ===");
        System.out.println("=== [CONFIG] OpenAiChatModel Max Tokens: " + maxTokens + " ===");
        
        // 创建ChatOptions并设置模型名称
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .withModel(model)
                .withTemperature(temperature)
                .withMaxTokens(maxTokens)
                .build();
        
        return new OpenAiChatModel(openAiApi, chatOptions);
    }

    // 配置ChatClient用于AI聊天
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    /**
     * 应用启动成功后显示访问信息
     */
    @Component
    public static class StartupInfoPrinter implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            Environment env = event.getApplicationContext().getEnvironment();
            String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
            String hostAddress = "localhost";
            try {
                hostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                // 如果无法获取主机地址，使用localhost
            }
            String serverPort = env.getProperty("server.port", "8080");
            String contextPath = env.getProperty("server.servlet.context-path", "");
            
            System.out.println("\n----------------------------------------");
            System.out.println("应用已成功启动！");
            System.out.println("应用名称: " + env.getProperty("app.name", "Spring AI Demo"));
            System.out.println("访问地址: " + protocol + "://" + hostAddress + ":" + serverPort + contextPath);
            System.out.println("健康检查: " + protocol + "://" + hostAddress + ":" + serverPort + contextPath + "actuator/health");
            System.out.println("H2数据库: " + protocol + "://" + hostAddress + ":" + serverPort + contextPath + "h2-console");
            System.out.println("当前Profile: " + String.join(",", env.getActiveProfiles()));
            System.out.println("AI Base URL: " + env.getProperty("spring.ai.openai.base-url", "未配置"));
            System.out.println("AI Model: " + env.getProperty("spring.ai.openai.chat.options.model", "未配置"));
            System.out.println("----------------------------------------\n");
        }
    }
}