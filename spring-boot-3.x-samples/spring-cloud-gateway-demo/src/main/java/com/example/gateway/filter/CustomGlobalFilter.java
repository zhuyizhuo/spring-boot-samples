package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 自定义全局过滤器示例
 * 
 * @author Example
 */
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 前置处理
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        System.out.println("请求路径: " + path);
        
        // 添加自定义请求头
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Custom-Filter", "applied")
                .build();
        
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();
        
        // 调用链上的下一个过滤器
        return chain.filter(modifiedExchange).then(Mono.fromRunnable(() -> {
            // 后置处理 - 响应返回后执行
            System.out.println("请求处理完成: " + path);
        }));
    }

    @Override
    public int getOrder() {
        // 过滤器执行顺序，数值越小优先级越高
        return -1;
    }
}