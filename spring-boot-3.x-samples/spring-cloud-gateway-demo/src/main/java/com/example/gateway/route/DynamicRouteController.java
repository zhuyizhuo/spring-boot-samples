package com.example.gateway.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 动态路由控制器
 * 提供REST API接口来管理路由规则
 */
@RestController
@RequestMapping("/api/routes")
public class DynamicRouteController {

    @Autowired
    private DynamicRouteService dynamicRouteService;
    
    /**
     * 添加路由
     * @param routeDefinition 路由定义
     * @return 响应结果
     */
    @PostMapping
    public ResponseEntity<String> addRoute(@RequestBody RouteDefinition routeDefinition) {
        boolean result = dynamicRouteService.addRoute(routeDefinition);
        if (result) {
            return ResponseEntity.ok("Route added successfully: " + routeDefinition.getId());
        } else {
            return ResponseEntity.badRequest().body("Failed to add route: " + routeDefinition.getId());
        }
    }
    
    /**
     * 更新路由
     * @param routeId 路由ID
     * @param routeDefinition 路由定义
     * @return 响应结果
     */
    @PutMapping("/{routeId}")
    public ResponseEntity<String> updateRoute(@PathVariable String routeId, 
                                            @RequestBody RouteDefinition routeDefinition) {
        // 确保路径中的ID与请求体中的ID一致
        if (!routeId.equals(routeDefinition.getId())) {
            routeDefinition.setId(routeId);
        }
        boolean result = dynamicRouteService.updateRoute(routeDefinition);
        if (result) {
            return ResponseEntity.ok("Route updated successfully: " + routeId);
        } else {
            return ResponseEntity.badRequest().body("Failed to update route: " + routeId);
        }
    }
    
    /**
     * 删除路由
     * @param routeId 路由ID
     * @return 响应结果
     */
    @DeleteMapping("/{routeId}")
    public ResponseEntity<String> deleteRoute(@PathVariable String routeId) {
        boolean result = dynamicRouteService.deleteRoute(routeId);
        if (result) {
            return ResponseEntity.ok("Route deleted successfully: " + routeId);
        } else {
            return ResponseEntity.badRequest().body("Failed to delete route: " + routeId);
        }
    }
    
    /**
     * 获取单个路由
     * @param routeId 路由ID
     * @return 路由定义
     */
    @GetMapping("/{routeId}")
    public ResponseEntity<?> getRoute(@PathVariable String routeId) {
        return dynamicRouteService.getRoute(routeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 刷新所有路由
     * @return 响应结果
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshRoutes() {
        dynamicRouteService.refreshRoutes();
        return ResponseEntity.ok("Routes refreshed successfully");
    }
}