package com.github.zhuyizhuo.ai.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@SpringBootApplication
public class SpringAiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiDemoApplication.class, args);
    }

    // 配置RestTemplate用于HTTP请求
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // 配置ChatClient用于AI聊天
    @Bean
    public ChatClient chatClient() {
        // 使用动态代理实现ChatClient接口
        return (ChatClient) Proxy.newProxyInstance(
                ChatClient.class.getClassLoader(),
                new Class<?>[] { ChatClient.class },
                (proxy, method, args) -> {
                    // 处理Object类方法
                    if (method.getDeclaringClass() == Object.class) {
                        if (method.getName().equals("equals")) {
                            return proxy == args[0];
                        } else if (method.getName().equals("hashCode")) {
                            return System.identityHashCode(proxy);
                        } else if (method.getName().equals("toString")) {
                            return "ChatClient Proxy Implementation";
                        }
                        return method.invoke(this, args);
                    }
                    
                    // 处理call方法，返回ChatResponse对象
                    if (method.getName().equals("call")) {
                        System.out.println("接收到聊天请求，返回模拟响应");
                        
                        // 处理Prompt参数
                        Object arg = args[0];
                        String content = "未知输入";
                        try {
                            // 通过反射获取Prompt中的指令内容
                            Method getInstructionsMethod = arg.getClass().getMethod("getInstructions");
                            Object instructions = getInstructionsMethod.invoke(arg);
                            Method getContentMethod = instructions.getClass().getMethod("getContent");
                            content = (String) getContentMethod.invoke(instructions);
                        } catch (Exception e) {
                            System.out.println("无法获取提示内容: " + e.getMessage());
                        }
                        
                        // 创建一个简单的模拟ChatResponse对象
                        // 由于ChatResponse是类不是接口，我们需要创建一个简单的实现类
                        try {
                            // 创建响应实现类
                            return new ChatResponseImpl(content);
                        } catch (Exception e) {
                            throw new RuntimeException("创建响应对象失败", e);
                        }
                    }
                    
                    // 处理mutate方法
                    if (method.getName().equals("mutate")) {
                        return proxy;
                    }
                    
                    // 对于其他方法，返回proxy
                    return proxy;
                }
        );
    }
    
    // 简单的ChatResponse实现类
    private static class ChatResponseImpl {
        private final String content;
        
        public ChatResponseImpl(String content) {
            this.content = "这是一个模拟的AI响应，您的输入是: " + content;
        }
        
        public Object getResult() {
            return new ResultImpl(content);
        }
    }
    
    private static class ResultImpl {
        private final String content;
        
        public ResultImpl(String content) {
            this.content = content;
        }
        
        public Object getOutput() {
            return new OutputImpl(content);
        }
    }
    
    private static class OutputImpl {
        private final String content;
        
        public OutputImpl(String content) {
            this.content = content;
        }
        
        public String getContent() {
            return content;
        }
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
            System.out.println("----------------------------------------\n");
        }
    }
}