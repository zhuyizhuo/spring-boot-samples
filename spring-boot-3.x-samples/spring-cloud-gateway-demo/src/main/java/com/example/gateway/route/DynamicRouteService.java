package com.example.gateway.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 动态路由服务类
 * 实现路由的增删改查和实时更新功能
 */
@Service
public class DynamicRouteService {

    @Autowired
    private RouteDefinitionRepository routeDefinitionRepository;
    
    @Autowired
    private ApplicationEventPublisher publisher;
    
    // 本地缓存路由定义，便于快速查询
    private final Map<String, RouteDefinition> routeCache = new LinkedHashMap<>();
    
    /**
     * 添加路由
     * @param definition 路由定义
     * @return 是否成功
     */
    public boolean addRoute(RouteDefinition definition) {
        try {
            routeDefinitionRepository.save(Mono.just(definition)).subscribe();
            routeCache.put(definition.getId(), definition);
            refreshRoutes();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 更新路由
     * @param definition 路由定义
     * @return 是否成功
     */
    public boolean updateRoute(RouteDefinition definition) {
        try {
            // 先删除旧路由
            deleteRoute(definition.getId());
            // 再添加新路由
            return addRoute(definition);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 删除路由
     * @param routeId 路由ID
     * @return 是否成功
     */
    public boolean deleteRoute(String routeId) {
        try {
            routeDefinitionRepository.delete(Mono.just(routeId)).subscribe();
            routeCache.remove(routeId);
            refreshRoutes();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取单个路由
     * @param routeId 路由ID
     * @return 路由定义
     */
    public Optional<RouteDefinition> getRoute(String routeId) {
        return Optional.ofNullable(routeCache.get(routeId));
    }
    
    /**
     * 刷新路由
     * 发布刷新事件，通知Gateway重新加载路由
     */
    public void refreshRoutes() {
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }
}