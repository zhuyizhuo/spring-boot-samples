package com.github.zhuyizhuo.esdemo.service;

import com.github.zhuyizhuo.esdemo.config.ElasticsearchDemoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Elasticsearch通用服务层实现
 * 提供Elasticsearch数据操作和业务逻辑
 */
@Service
public class EsService {
    private static final Logger log = LoggerFactory.getLogger(EsService.class);

    @Autowired(required = false)
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ElasticsearchDemoProperties properties;

    /**
     * 动态查询 - 支持多条件组合查询和模糊查询
     */
    public SearchHits<Map> dynamicSearch(String indexName, Map<String, Object> queryParams, int page, int size) {
        size = Math.min(size, properties.getMaxPageSize());
        Pageable pageable = PageRequest.of(page, size);

        if (elasticsearchOperations == null) {
            log.warn("Elasticsearch operations not available");
            return null;
        }

        try {
            Criteria criteria = new Criteria();

            // 动态构建查询条件
            if (queryParams != null && !queryParams.isEmpty()) {
                queryParams.forEach((field, value) -> {
                    if (value != null && !value.toString().trim().isEmpty()) {
                        String valueStr = value.toString().trim();
                        // 检查是否是模糊查询条件（格式：field=~value 或 value本身包含*或?通配符）
                        if (field.endsWith("~") || valueStr.contains("*") || valueStr.contains("?")) {
                            // 移除字段名末尾的波浪号
                            String actualField = field.endsWith("~") ? field.substring(0, field.length() - 1) : field;
                            // 添加模糊查询条件
                            criteria.and(new Criteria(actualField).matches(valueStr));
                            log.debug("添加模糊查询条件: {} matches {}", actualField, valueStr);
                        } else {
                            // 添加精确查询条件
                            criteria.and(new Criteria(field).is(value));
                            log.debug("添加精确查询条件: {} = {}", field, value);
                        }
                    }
                });
            }

            CriteriaQuery criteriaQuery = new CriteriaQuery(criteria, pageable);

            log.info("动态查询ES数据，索引: {}, 查询条件: {}, 页码: {}, 每页大小: {}",
                    indexName, queryParams, page, size);

            // 指定索引名称
            return elasticsearchOperations.search(criteriaQuery, Map.class, IndexCoordinates.of(indexName));

        } catch (Exception e) {
            log.error("Error performing dynamic search on index: {}", indexName, e);
            return null;
        }
    }
    
    /**
     * 获取索引字段信息
     * @param indexName 索引名称
     * @return 字段信息列表
     */
    public List<Map<String, String>> getIndexFields(String indexName) {
        List<Map<String, String>> fields = new ArrayList<>();
        
        if (elasticsearchOperations == null) {
            log.warn("Elasticsearch operations not available");
            return fields;
        }
        
        if (indexName == null || indexName.trim().isEmpty()) {
            log.warn("索引名称不能为空");
            return fields;
        }
        
        try {
            // 使用Spring Data Elasticsearch 5.x API获取索引映射
            IndexOperations indexOperations = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));
            Map<String, Object> mapping = indexOperations.getMapping();
            
            if (mapping != null && mapping.containsKey("properties")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> properties = (Map<String, Object>) mapping.get("properties");
                // 提取字段信息
                extractFieldsFromMap(properties, "", fields);
            } else {
                log.warn("未找到索引 {} 的映射信息", indexName);
                // 尝试通过查询获取样本数据并分析字段
                try {
                    // 使用match_all查询获取一条文档用于分析字段
                    Query query = Query.findAll();
                    SearchHits<Object> hits = elasticsearchOperations.search(query, Object.class, IndexCoordinates.of(indexName));
                    if (hits.hasSearchHits()) {
                        Object firstHit = hits.getSearchHits().get(0).getContent();
                        if (firstHit instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> sampleDoc = (Map<String, Object>) firstHit;
                            // 从样本文档中提取字段信息
                            extractFieldsFromSampleMap(sampleDoc, "", fields);
                        }
                    }
                } catch (Exception e) {
                    log.warn("无法通过查询样本获取字段信息: {}", e.getMessage());
                }
            }
            
            log.info("成功获取索引 {} 的字段信息，共 {} 个字段", indexName, fields.size());
        } catch (Exception e) {
            log.error("获取索引字段信息失败: {}", indexName, e);
        }
        
        return fields;
    }
    
    /**
     * 递归提取字段信息
     */
    private void extractFieldsFromMap(Map<String, Object> properties, String prefix, List<Map<String, String>> fields) {
        if (properties == null || properties.isEmpty()) {
            return;
        }
        
        properties.forEach((fieldName, fieldProps) -> {
            if (fieldProps instanceof Map) {
                Map<String, Object> fieldProperties = (Map<String, Object>) fieldProps;
                String fullFieldName = prefix.isEmpty() ? fieldName : prefix + "." + fieldName;
                
                if (fieldProperties.containsKey("properties")) {
                    // 如果是嵌套对象，递归处理
                    @SuppressWarnings("unchecked")
                    Map<String, Object> nestedProperties = (Map<String, Object>) fieldProperties.get("properties");
                    extractFieldsFromMap(nestedProperties, fullFieldName, fields);
                } else if (fieldProperties.containsKey("type")) {
                    // 添加字段信息
                    Map<String, String> fieldInfo = new HashMap<>();
                    fieldInfo.put("name", fullFieldName);
                    fieldInfo.put("type", fieldProperties.getOrDefault("type", "unknown").toString());
                    fields.add(fieldInfo);
                }
            }
        });
    }
    
    /**
     * 从样本文档中提取字段信息
     */
    private void extractFieldsFromSampleMap(Map<String, Object> doc, String prefix, List<Map<String, String>> fields) {
        if (doc == null || doc.isEmpty()) {
            return;
        }
        
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            String fullFieldName = prefix.isEmpty() ? fieldName : prefix + "." + fieldName;
            
            // 跳过内部元数据字段
            if (fieldName.startsWith("_") && !fieldName.equals("_source")) {
                continue;
            }
            
            // 根据值类型判断字段类型
            String fieldType;
            if (value instanceof String) {
                fieldType = "text";
            } else if (value instanceof Boolean) {
                fieldType = "boolean";
            } else if (value instanceof Number) {
                fieldType = "number";
            } else if (value instanceof Map) {
                // 嵌套对象
                fieldType = "object";
                // 添加当前字段
                Map<String, String> fieldInfo = new HashMap<>();
                fieldInfo.put("name", fullFieldName);
                fieldInfo.put("type", fieldType);
                fields.add(fieldInfo);
                // 递归处理嵌套对象
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedDoc = (Map<String, Object>) value;
                extractFieldsFromSampleMap(nestedDoc, fullFieldName, fields);
                continue;
            } else if (value instanceof List) {
                fieldType = "array";
            } else {
                fieldType = "object";
            }
            
            // 添加字段信息
            Map<String, String> fieldInfo = new HashMap<>();
            fieldInfo.put("name", fullFieldName);
            fieldInfo.put("type", fieldType);
            fields.add(fieldInfo);
        }
    }

    /**
     * 获取当前索引名称
     */
    public String getCurrentIndex() {
        return "";
    }
    
    /**
     * 获取Elasticsearch版本信息
     * @return Elasticsearch版本号，如无法获取则返回空字符串
     */
    public String getElasticsearchVersion() {
        if (elasticsearchOperations == null) {
            log.warn("Elasticsearch operations not available, cannot get version");
            return "";
        }
        
        try {
            log.info("尝试获取Elasticsearch版本信息");
            
            // 方法1：尝试通过反射调用elasticsearchOperations的方法获取客户端
            try {
                // 尝试获取客户端对象
                Object client = null;
                
                // 尝试访问elasticsearchOperations的client或elasticsearchClient属性
                try {
                    java.lang.reflect.Field clientField = null;
                    // 尝试不同的字段名
                    String[] possibleFieldNames = {"client", "elasticsearchClient", "restHighLevelClient", "lowLevelClient"};
                    
                    for (String fieldName : possibleFieldNames) {
                        try {
                            clientField = elasticsearchOperations.getClass().getDeclaredField(fieldName);
                            break;
                        } catch (NoSuchFieldException ignored) {
                        }
                    }
                    
                    if (clientField != null) {
                        clientField.setAccessible(true);
                        client = clientField.get(elasticsearchOperations);
                    }
                } catch (Exception e) {
                    log.debug("通过反射获取客户端字段失败: {}", e.getMessage());
                }
                
                // 如果获取到了客户端对象，尝试调用info方法
                if (client != null) {
                    try {
                        // 尝试调用info方法
                        java.lang.reflect.Method infoMethod = client.getClass().getMethod("info");
                        Object infoResponse = infoMethod.invoke(client);
                        
                        // 解析版本信息
                        return parseVersionFromResponse(infoResponse);
                    } catch (NoSuchMethodException e) {
                        log.debug("客户端对象没有info方法: {}", e.getMessage());
                        
                        // 尝试其他可能的方法名
                        String[] possibleMethodNames = {"getVersion", "version", "clusterInfo", "info"};
                        for (String methodName : possibleMethodNames) {
                            try {
                                java.lang.reflect.Method method = client.getClass().getMethod(methodName);
                                Object result = method.invoke(client);
                                String version = parseVersionFromResponse(result);
                                if (!version.isEmpty()) {
                                    return version;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("使用反射获取ES版本失败: {}", e.getMessage());
            }
            
            // 方法2：尝试通过发送特殊查询获取版本信息
            // 使用一个简单的查询，通过响应头或元数据获取版本
            try {
                // 创建一个简单的查询
                org.springframework.data.elasticsearch.core.query.Query query = org.springframework.data.elasticsearch.core.query.Query.findAll();
                query.setPageable(org.springframework.data.domain.PageRequest.of(0, 1));
                
                // 执行查询并尝试从结果中获取版本信息
                org.springframework.data.elasticsearch.core.SearchHits<?> hits = elasticsearchOperations.search(query, Object.class);
                
                // 检查响应元数据
                if (hits.hasSearchHits()) {
                    // 从响应头或元数据中尝试获取版本信息
                    // 不同版本的Spring Data Elasticsearch可能有不同的API
                    try {
                        // 尝试访问hits的元数据
                        java.lang.reflect.Field metadataField = hits.getClass().getDeclaredField("metadata");
                        metadataField.setAccessible(true);
                        Object metadata = metadataField.get(hits);
                        
                        if (metadata != null) {
                            String version = parseVersionFromResponse(metadata);
                            if (!version.isEmpty()) {
                                return version;
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception e) {
                log.debug("通过查询获取版本信息失败: {}", e.getMessage());
            }
            
            log.warn("无法获取Elasticsearch版本信息，使用默认版本标识");
            return "unknown";
        } catch (Exception e) {
            log.warn("获取Elasticsearch版本信息时出错: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * 解析响应对象中的版本信息
     * @param response 响应对象
     * @return 版本号字符串，如无法解析则返回空字符串
     */
    private String parseVersionFromResponse(Object response) {
        if (response == null) {
            return "";
        }
        
        try {
            // 方法1：使用反射解析
            try {
                // 尝试获取version字段
                java.lang.reflect.Field versionField = null;
                
                // 检查对象本身是否有version字段
                try {
                    versionField = response.getClass().getDeclaredField("version");
                } catch (NoSuchFieldException e) {
                    // 尝试获取其他可能的版本相关字段
                    String[] possibleVersionFields = {"version", "Version", "esVersion", "elasticsearchVersion"};
                    for (String fieldName : possibleVersionFields) {
                        try {
                            versionField = response.getClass().getDeclaredField(fieldName);
                            break;
                        } catch (NoSuchFieldException ignored) {
                        }
                    }
                }
                
                if (versionField != null) {
                    versionField.setAccessible(true);
                    Object versionObj = versionField.get(response);
                    
                    if (versionObj != null) {
                        // 如果version对象是字符串，直接返回
                        if (versionObj instanceof String) {
                            String version = versionObj.toString();
                            log.info("成功获取Elasticsearch版本: {}", version);
                            return version;
                        }
                        
                        // 尝试从version对象中获取number字段
                        try {
                            java.lang.reflect.Field numberField = versionObj.getClass().getDeclaredField("number");
                            numberField.setAccessible(true);
                            Object numberObj = numberField.get(versionObj);
                            
                            if (numberObj != null) {
                                String version = numberObj.toString();
                                log.info("成功获取Elasticsearch版本: {}", version);
                                return version;
                            }
                        } catch (Exception e) {
                            log.debug("解析version对象的number字段失败: {}", e.getMessage());
                            // 尝试将version对象直接转为字符串
                            String versionStr = versionObj.toString();
                            String version = extractVersionFromString(versionStr);
                            if (!version.isEmpty()) {
                                return version;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("使用反射解析版本信息失败: {}", e.getMessage());
            }
            
            // 方法2：使用字符串解析
            String responseStr = response.toString();
            return extractVersionFromString(responseStr);
        } catch (Exception e) {
            log.warn("解析响应对象中的版本信息失败: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * 从字符串中提取版本号
     * @param input 输入字符串
     * @return 提取的版本号，如无法提取则返回空字符串
     */
    private String extractVersionFromString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        // 尝试多种版本号格式的提取
        // 格式1: number=7.17.0
        if (input.contains("number=") && input.contains(",")) {
            int start = input.indexOf("number=") + 7;
            int end = input.indexOf(",", start);
            if (start > 0 && end > start) {
                String version = input.substring(start, end).trim();
                log.info("通过字符串解析获取Elasticsearch版本: {}", version);
                return version;
            }
        }
        
        // 格式2: version=7.17.0
        if (input.contains("version=") && input.contains(",")) {
            int start = input.indexOf("version=") + 8;
            int end = input.indexOf(",", start);
            if (start > 0 && end > start) {
                String version = input.substring(start, end).trim();
                log.info("通过字符串解析获取Elasticsearch版本: {}", version);
                return version;
            }
        }
        
        // 格式3: 直接包含版本号格式 x.x.x
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+\\.\\d+\\.\\d+");
        java.util.regex.Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String version = matcher.group();
            log.info("通过正则表达式匹配获取Elasticsearch版本: {}", version);
            return version;
        }
        
        return "";
    }

}