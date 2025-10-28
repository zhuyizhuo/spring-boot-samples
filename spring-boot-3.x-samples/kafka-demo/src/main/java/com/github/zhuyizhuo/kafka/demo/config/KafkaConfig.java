package com.github.zhuyizhuo.kafka.demo.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka配置类
 */
@Configuration
public class KafkaConfig {

    @Value("${kafka-demo.topics.demo-topic}")
    private String demoTopic;
    
    @Value("${kafka-demo.topics.json-topic}")
    private String jsonTopic;
    
    @Value("${kafka-demo.topics.batch-topic}")
    private String batchTopic;
    
    // 从配置文件中读取Kafka服务器地址
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;
    
    /**
     * 配置Kafka AdminClient
     */
    @Bean
    public AdminClient adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        return AdminClient.create(configs);
    }
    
    /**
     * 配置Kafka Producer Factory
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    /**
     * 配置Kafka Template
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    /**
     * 配置Kafka Consumer Factory
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-demo-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.github.zhuyizhuo.kafka.demo.model");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    /**
     * 配置Kafka Listener Container Factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        // 设置手动确认模式
        factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL);
        return factory;
    }
    
    /**
     * 配置批量消息Kafka Listener Container Factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> batchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        // 设置手动确认模式
        factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL);
        // 启用批量监听模式
        factory.setBatchListener(true);
        return factory;
    }
    
    /**
     * 创建普通消息主题
     */
    @Bean
    public NewTopic demoTopic() {
        return TopicBuilder.name(demoTopic)
                .partitions(3)
                .replicas(1)
                .configs(java.util.Map.of("retention.ms", "86400000")) // 1天
                .build();
    }
    
    /**
     * 创建JSON消息主题
     */
    @Bean
    public NewTopic jsonTopic() {
        return TopicBuilder.name(jsonTopic)
                .partitions(3)
                .replicas(1)
                .configs(java.util.Map.of("retention.ms", "604800000")) // 7天
                .build();
    }
    
    /**
     * 创建批量消息主题
     */
    @Bean
    public NewTopic batchTopic() {
        return TopicBuilder.name(batchTopic)
                .partitions(5)
                .replicas(1)
                .configs(java.util.Map.of(
                        "retention.ms", "3600000", // 1小时
                        "max.message.bytes", "10485760" // 10MB
                ))
                .build();
    }
}