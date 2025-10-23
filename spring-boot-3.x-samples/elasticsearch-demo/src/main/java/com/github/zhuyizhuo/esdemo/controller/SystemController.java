package com.github.zhuyizhuo.esdemo.controller;

import com.github.zhuyizhuo.esdemo.config.ElasticsearchDemoProperties;
import com.github.zhuyizhuo.esdemo.service.EsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统管理相关API控制器
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {
    private static final Logger log = LoggerFactory.getLogger(SystemController.class);

    @Autowired(required = false)
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ElasticsearchDemoProperties properties;

    @Autowired
    private EsService esService;

    /**
     * 获取Elasticsearch连接状态
     */
    @GetMapping("/es-status")
    public ResponseEntity<Map<String, Object>> getEsStatus() {
        Map<String, Object> response = new HashMap<>();
        boolean connected = false;
        String version = "";

        try {
            // 检查ES操作对象是否可用
            if (elasticsearchOperations != null) {
                // 尝试执行简单操作来验证连接（使用count操作代替ping）
                try {
                    // 创建一个简单的查询来测试连接
                    CriteriaQuery query = new CriteriaQuery(new Criteria());
                    elasticsearchOperations.count(query, Map.class, IndexCoordinates.of("*"));
                    connected = true;
                    log.info("Elasticsearch连接状态: 正常");
                    
                    // 获取Elasticsearch版本信息 - 通过EsService获取
                    try {
                        version = esService.getElasticsearchVersion();
                        if (version != null && !version.isEmpty()) {
                            log.info("获取到Elasticsearch版本: {}", version);
                        }
                    } catch (Exception e) {
                        log.warn("获取Elasticsearch版本信息失败: {}", e.getMessage());
                    }
                } catch (Exception e) {
                    // 连接测试失败，但不抛出异常，只记录日志
                    log.warn("Elasticsearch连接测试失败: {}", e.getMessage());
                }
            } else {
                log.warn("Elasticsearch操作对象不可用");
            }
        } catch (Exception e) {
            log.error("Elasticsearch连接检查异常: {}", e.getMessage());
        }

        // 填充响应数据
        response.put("connected", connected);
        response.put("version", version);
        response.put("host", properties.getHost());
        response.put("port", properties.getPort());
        response.put("connectionTimeout", properties.getConnectionTimeout());
        response.put("socketTimeout", properties.getSocketTimeout());
        response.put("connectionAddress", properties.getUris());
        
        // 获取当前系统时间
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        response.put("currentTime", currentTime);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取系统状态信息
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> response = new HashMap<>();
        
        // 检查应用状态
        boolean systemRunning = true;
        
        // 检查ES连接状态
        boolean esConnected = false;
        try {
            if (elasticsearchOperations != null) {
                // 使用同样的方法检查ES连接
                try {
                    CriteriaQuery query = new CriteriaQuery(new Criteria());
                    elasticsearchOperations.count(query, Map.class, IndexCoordinates.of("*"));
                    esConnected = true;
                } catch (Exception e) {
                    log.warn("Elasticsearch连接测试失败: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Elasticsearch连接检查异常: {}", e.getMessage());
        }
        
        // 计算系统运行时长
        long uptimeSeconds = (System.currentTimeMillis() - properties.getStartupTime()) / 1000;
        
        // 填充响应数据
        response.put("systemRunning", systemRunning);
        response.put("esConnected", esConnected);
        response.put("uptimeSeconds", uptimeSeconds);
        response.put("currentIndex", esService.getCurrentIndex());
        
        return ResponseEntity.ok(response);
    }
}