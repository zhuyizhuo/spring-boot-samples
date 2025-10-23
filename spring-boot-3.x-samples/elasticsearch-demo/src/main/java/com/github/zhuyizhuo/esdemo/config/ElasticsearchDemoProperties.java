package com.github.zhuyizhuo.esdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Elasticsearch示例配置属性类
 * 支持动态索引配置和查询参数配置
 */
@Component
@Data
@ConfigurationProperties(prefix = "elasticsearch-demo")
public class ElasticsearchDemoProperties {

    // 应用启动时间戳
    private final long startupTime;
    
    // ES连接地址
    private String uris;

    // 最大分页大小
    private int maxPageSize = 100;

    // 最大批量操作大小
    private int maxBatchSize = 1000;

    // 连接超时时间（毫秒）
    private int connectionTimeout = 10000;

    // Socket超时时间（毫秒）
    private int socketTimeout = 30000;

    // 初始化索引名称为null，需要通过前端传入
    public ElasticsearchDemoProperties() {
        this.startupTime = System.currentTimeMillis();
    }

    // 获取主机名（从uris中解析）
    public String getHost() {
        if (uris == null || uris.isEmpty()) {
            return "localhost"; // 默认值
        }
        try {
            // 从uris解析主机名
            String host = uris;
            if (host.startsWith("http://")) {
                host = host.substring(7);
            } else if (host.startsWith("https://")) {
                host = host.substring(8);
            }
            // 去掉端口部分
            int colonIndex = host.indexOf(':');
            if (colonIndex > 0) {
                return host.substring(0, colonIndex);
            }
            return host;
        } catch (Exception e) {
            return "localhost";
        }
    }
    
    // 获取端口（从uris中解析）
    public int getPort() {
        if (uris == null || uris.isEmpty()) {
            return 9200; // 默认端口
        }
        try {
            // 从uris解析端口
            int colonIndex = uris.lastIndexOf(':');
            int slashIndex = uris.lastIndexOf('/');
            if (colonIndex > 0 && (slashIndex == -1 || colonIndex > slashIndex)) {
                String portStr = uris.substring(colonIndex + 1);
                // 处理可能的路径部分
                if (portStr.contains("/")) {
                    portStr = portStr.substring(0, portStr.indexOf('/'));
                }
                return Integer.parseInt(portStr);
            }
            // 如果是https，默认443，否则9200
            return uris.startsWith("https://") ? 443 : 9200;
        } catch (Exception e) {
            return 9200;
        }
    }
}