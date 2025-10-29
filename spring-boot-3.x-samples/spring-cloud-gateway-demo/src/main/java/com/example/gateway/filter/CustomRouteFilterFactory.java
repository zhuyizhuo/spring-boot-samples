package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义路由过滤器工厂示例
 * 
 * @author Example
 */
@Component
public class CustomRouteFilterFactory extends AbstractGatewayFilterFactory<CustomRouteFilterFactory.Config> {

    /**
     * 配置类，用于接收过滤器参数
     */
    public static class Config {
        private String headerName;
        private String headerValue;

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public String getHeaderValue() {
            return headerValue;
        }

        public void setHeaderValue(String headerValue) {
            this.headerValue = headerValue;
        }
    }

    public CustomRouteFilterFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        // 定义配置参数的顺序
        return Arrays.asList("headerName", "headerValue");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 前置处理
            System.out.println("应用自定义路由过滤器: 添加头信息 - " + 
                    config.getHeaderName() + ":" + config.getHeaderValue());
            
            // 添加自定义头信息
            exchange.getRequest().mutate()
                    .header(config.getHeaderName(), config.getHeaderValue())
                    .build();
            
            // 调用链上的下一个过滤器
            return chain.filter(exchange);
        };
    }
}