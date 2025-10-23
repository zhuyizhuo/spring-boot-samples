package com.github.zhuyizhuo.esdemo.controller;

import com.github.zhuyizhuo.esdemo.service.EsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Elasticsearch通用REST API控制器
 * 提供Elasticsearch数据操作接口
 */
@RestController
@RequestMapping("/api/es")
public class EsController {
    private static final Logger log = LoggerFactory.getLogger(EsController.class);

    @Autowired
    private EsService esService;

    /**
     * 动态查询接口 - 支持通过查询参数查询，同时支持GET和POST方法
     */
    @RequestMapping(value = "/dynamic-search", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> dynamicSearch(
            @RequestParam String indexName,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) Map<String, Object> queryParams) {
        try {
            // 如果查询参数为空，创建一个空的HashMap
            if (queryParams == null) {
                queryParams = new HashMap<>();
            } else {
                // 移除系统参数，避免它们作为查询条件被传入
                queryParams.remove("indexName");
                queryParams.remove("page");
                queryParams.remove("size");
            }

            // 执行动态查询
            SearchHits<Map> searchHits = esService.dynamicSearch(indexName, queryParams, page, size);
            
            // 转换结果
            List datas = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", datas);
            result.put("total", searchHits.getTotalHits());
            result.put("page", page);
            result.put("size", size);
            result.put("index", indexName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("动态查询失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    

    
    /**
     * 原始数据查询接口
     */
    @GetMapping("/raw-search")
    public ResponseEntity<Map<String, Object>> rawSearch(
            @RequestParam String indexName,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        try {
            // 参数验证和调整
            if (page == null || page < 0) {
                page = 0;
            }
            if (size == null || size <= 0) {
                size = 10;
            } else if (size > 100) {
                // 限制最大每页数量，防止查询过多数据
                size = 100;
            }

            // 执行查询，使用传入的分页参数
            SearchHits<Map> searchHits = esService.dynamicSearch(indexName, new HashMap<>(), page, size);
            
            // 转换结果 - 添加空值检查
            List<Map> datas = new ArrayList<>();
            if (searchHits != null && searchHits.getSearchHits() != null) {
                datas = searchHits.getSearchHits().stream()
                        .map(SearchHit::getContent)
                        .collect(Collectors.toList());
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", !datas.isEmpty());
            result.put("data", datas);
            // 添加空值检查
            result.put("total", searchHits != null ? searchHits.getTotalHits() : 0);
            result.put("message", datas.isEmpty() ? "查询结果为空" : "查询成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("原始数据查询失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("data", new ArrayList<>());
            result.put("total", 0);
            result.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    /**
     * 获取索引字段信息接口
     */
    @GetMapping("/fields")
    public ResponseEntity<Map<String, Object>> getIndexFields(@RequestParam String indexName) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, String>> fields = esService.getIndexFields(indexName);
            response.put("success", true);
            response.put("data", fields);
            response.put("message", "获取字段信息成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取字段信息失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    // 仅保留前端实际使用的接口
}