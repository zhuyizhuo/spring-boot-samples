package com.github.zhuyizhuo.kafka.demo.service;

import org.apache.kafka.clients.admin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Kafka主题管理服务
 */
@Service
public class KafkaTopicService {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    private static final int DEFAULT_PARTITIONS = 1;
    private static final short DEFAULT_REPLICATION_FACTOR = 1;
    private static final int TIMEOUT_MS = 30000;

    /**
     * 获取AdminClient实例
     */
    private AdminClient getAdminClient() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, TIMEOUT_MS);
        props.put(AdminClientConfig.RETRIES_CONFIG, 3);
        props.put(AdminClientConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        return KafkaAdminClient.create(props);
    }

    /**
     * 获取所有主题列表
     */
    public List<String> listTopics() {
        try (AdminClient adminClient = getAdminClient()) {
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> topicNames = topics.names().get(30, TimeUnit.SECONDS);
            return new ArrayList<>(topicNames);
        } catch (Exception e) {
            throw new RuntimeException("获取主题列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建主题
     */
    public boolean createTopic(String topicName) {
        try (AdminClient adminClient = getAdminClient()) {
            // 检查主题是否已存在
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> topicNames = topics.names().get(30, TimeUnit.SECONDS);
            
            if (topicNames.contains(topicName)) {
                return false; // 主题已存在
            }

            // 创建主题配置
            NewTopic newTopic = new NewTopic(topicName, DEFAULT_PARTITIONS, DEFAULT_REPLICATION_FACTOR);
            CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(newTopic));
            
            // 等待创建完成
            result.all().get(30, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("创建主题失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除主题
     */
    public boolean deleteTopic(String topicName) {
        try (AdminClient adminClient = getAdminClient()) {
            // 检查主题是否存在
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> topicNames = topics.names().get(30, TimeUnit.SECONDS);
            
            if (!topicNames.contains(topicName)) {
                return false; // 主题不存在
            }

            // 删除主题
            DeleteTopicsResult result = adminClient.deleteTopics(Collections.singletonList(topicName));
            
            // 等待删除完成
            result.all().get(30, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("删除主题失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查主题是否存在
     */
    public boolean topicExists(String topicName) {
        try (AdminClient adminClient = getAdminClient()) {
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> topicNames = topics.names().get(30, TimeUnit.SECONDS);
            return topicNames.contains(topicName);
        } catch (Exception e) {
            throw new RuntimeException("检查主题失败: " + e.getMessage(), e);
        }
    }
}