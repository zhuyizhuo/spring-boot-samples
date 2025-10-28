package com.github.zhuyizhuo.kafka.demo.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: zhuo
 * @date: 2025-10-17 10:52
 */
@Component
@Slf4j
public class KafkaConnectionTester {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void testConnectionOnStartup() {
        log.info("🔍 开始Kafka连接测试...");
        
        // 仅使用AdminClient测试连接
        testWithAdminClient();
    }

    private void testWithAdminClient() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        props.put(AdminClientConfig.RETRIES_CONFIG, 3);
        props.put(AdminClientConfig.RETRY_BACKOFF_MS_CONFIG, 1000);

        try (AdminClient adminClient = KafkaAdminClient.create(props)) {
            // 测试列出主题
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> topicNames = topics.names().get(30, TimeUnit.SECONDS);
            log.info("✅ AdminClient连接成功，发现 {} 个主题: {}", topicNames.size(), topicNames);
        } catch (Exception e) {
            log.error("❌ AdminClient连接失败: {}", e.getMessage());
            log.debug("详细错误:", e);
        }
    }
}
