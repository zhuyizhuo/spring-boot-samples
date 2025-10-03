package com.github.zhuyizhuo.rabbitmq.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 * 支持多种交换机类型：Direct、Topic、Fanout、Headers
 * 
 * @author zhuyizhuo
 * @version 1.0
 * @date 2025/10/02
 */
@Configuration
public class RabbitConfig {

    // ==================== Direct Exchange ====================
    public static final String DIRECT_EXCHANGE = "demo.direct.exchange";
    public static final String DIRECT_QUEUE = "demo.direct.queue";
    public static final String DIRECT_ROUTING_KEY = "demo.direct.routing.key";

    // ==================== Topic Exchange ====================
    public static final String TOPIC_EXCHANGE = "demo.topic.exchange";
    public static final String TOPIC_QUEUE_USER = "demo.topic.queue.user";
    public static final String TOPIC_QUEUE_ORDER = "demo.topic.queue.order";
    public static final String TOPIC_ROUTING_KEY_USER = "demo.topic.user.*";
    public static final String TOPIC_ROUTING_KEY_ORDER = "demo.topic.order.*";

    // ==================== Fanout Exchange ====================
    public static final String FANOUT_EXCHANGE = "demo.fanout.exchange";
    public static final String FANOUT_QUEUE_EMAIL = "demo.fanout.queue.email";
    public static final String FANOUT_QUEUE_SMS = "demo.fanout.queue.sms";
    public static final String FANOUT_QUEUE_PUSH = "demo.fanout.queue.push";

    // ==================== Headers Exchange ====================
    public static final String HEADERS_EXCHANGE = "demo.headers.exchange";
    public static final String HEADERS_QUEUE = "demo.headers.queue";

    // ==================== Dead Letter Exchange ====================
    public static final String DLX_EXCHANGE = "demo.dlx.exchange";
    public static final String DLX_QUEUE = "demo.dlx.queue";
    public static final String DLX_ROUTING_KEY = "demo.dlx.routing.key";

    /**
     * 配置消息转换器，使用JSON格式
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        
        // 开启发送确认
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息发送成功: " + correlationData);
            } else {
                System.err.println("消息发送失败: " + cause);
            }
        });
        
        // 开启返回确认
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.println("消息未能投递到队列: " + returned.getMessage());
        });
        
        return rabbitTemplate;
    }

    /**
     * 配置监听器容器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(5);
        return factory;
    }

    // ==================== Direct Exchange 配置 ====================

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public Queue directQueue() {
        return QueueBuilder.durable(DIRECT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .withArgument("x-message-ttl", 60000) // 消息TTL 60秒
                .build();
    }

    @Bean
    public Binding directBinding() {
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(DIRECT_ROUTING_KEY);
    }

    // ==================== Topic Exchange 配置 ====================

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public Queue topicQueueUser() {
        return QueueBuilder.durable(TOPIC_QUEUE_USER).build();
    }

    @Bean
    public Queue topicQueueOrder() {
        return QueueBuilder.durable(TOPIC_QUEUE_ORDER).build();
    }

    @Bean
    public Binding topicBindingUser() {
        return BindingBuilder.bind(topicQueueUser()).to(topicExchange()).with(TOPIC_ROUTING_KEY_USER);
    }

    @Bean
    public Binding topicBindingOrder() {
        return BindingBuilder.bind(topicQueueOrder()).to(topicExchange()).with(TOPIC_ROUTING_KEY_ORDER);
    }

    // ==================== Fanout Exchange 配置 ====================

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }

    @Bean
    public Queue fanoutQueueEmail() {
        return QueueBuilder.durable(FANOUT_QUEUE_EMAIL).build();
    }

    @Bean
    public Queue fanoutQueueSms() {
        return QueueBuilder.durable(FANOUT_QUEUE_SMS).build();
    }

    @Bean
    public Queue fanoutQueuePush() {
        return QueueBuilder.durable(FANOUT_QUEUE_PUSH).build();
    }

    @Bean
    public Binding fanoutBindingEmail() {
        return BindingBuilder.bind(fanoutQueueEmail()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBindingSms() {
        return BindingBuilder.bind(fanoutQueueSms()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBindingPush() {
        return BindingBuilder.bind(fanoutQueuePush()).to(fanoutExchange());
    }

    // ==================== Headers Exchange 配置 ====================

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE, true, false);
    }

    @Bean
    public Queue headersQueue() {
        return QueueBuilder.durable(HEADERS_QUEUE).build();
    }

    @Bean
    public Binding headersBinding() {
        return BindingBuilder.bind(headersQueue()).to(headersExchange())
                .whereAll("type", "priority").exist();
    }

    // ==================== Dead Letter Exchange 配置 ====================

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with(DLX_ROUTING_KEY);
    }
}

