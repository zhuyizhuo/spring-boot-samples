package com.example.gateway.route;

import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的路由定义仓库实现
 * 用于存储和管理动态路由定义
 */
@Component
public class InMemoryRouteDefinitionRepository implements RouteDefinitionRepository {

    // 使用ConcurrentHashMap保证线程安全
    private final Map<String, RouteDefinition> routes = new ConcurrentHashMap<>();
    
    /**
     * 获取所有路由定义
     * @return 路由定义的Flux流
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(routes.values());
    }
    
    /**
     * 保存路由定义
     * @param route 路由定义的Mono
     * @return 完成信号
     */
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(r -> {
            routes.put(r.getId(), r);
            return Mono.empty();
        });
    }
    
    /**
     * 删除路由定义
     * @param routeId 路由ID的Mono
     * @return 完成信号
     */
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            if (routes.containsKey(id)) {
                routes.remove(id);
            }
            return Mono.empty();
        });
    }
}