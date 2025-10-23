package com.github.zhuyizhuo.esdemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch配置类
 * Spring Boot 3.x 与 Elasticsearch 兼容配置
 */
@Configuration
@EnableElasticsearchRepositories
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);

    @Value("${elasticsearch-demo.uris:localhost:9200}")
    private String uris;

    @Value("${spring.elasticsearch.rest.username:}")
    private String username;

    @Value("${spring.elasticsearch.rest.password:}")
    private String password;

    @Value("${elasticsearch-demo.connection-timeout:10000}")
    private int connectionTimeout;

    @Value("${elasticsearch-demo.socket-timeout:30000}")
    private int socketTimeout;

    @Override
    public ClientConfiguration clientConfiguration() {
        // 输出配置信息到日志
        logConfigurationInfo();
        
        // 使用ClientConfiguration.builder()的标准方式，不指定具体的构建器类型
        return ClientConfiguration.builder()
                .connectedTo(uris)
                .withConnectTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .withBasicAuth(username, password) // Spring Boot会自动处理空值情况
                .build();
    }
    
    /**
     * 记录Elasticsearch配置信息
     * 注意：密码信息使用安全方式记录，避免明文输出
     */
    private void logConfigurationInfo() {
        // 密码脱敏处理，不直接输出密码明文
        String passwordMasked = (password != null && !password.isEmpty()) ? "[PROTECTED]" : "";
        String usernameDisplay = (username != null && !username.isEmpty()) ? username : "<none>";
        
        logger.info("Elasticsearch配置信息：");
        logger.info("  - 连接地址(uris): {}", uris);
        logger.info("  - 认证信息: 用户名={}, 密码={}", usernameDisplay, passwordMasked);
        logger.info("  - 连接超时(connectionTimeout): {}ms", connectionTimeout);
        logger.info("  - 套接字超时(socketTimeout): {}ms", socketTimeout);
    }

    // 不再需要手动配置 ElasticsearchOperations，Spring Boot 3.x 会自动配置
    // ElasticsearchConfiguration 已经包含了必要的配置
}