package com.github.zhuyizhuo.dingtalk.demo.config;

import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 钉钉配置类
 */
@Configuration
@AllArgsConstructor
public class DingTalkConfig {

    private final DingTalkProperties dingTalkProperties;

    /**
     * 创建钉钉配置
     */
    @Bean
    public Config aliDingTalkConfig() {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return config;
    }

    /**
     * 创建钉钉OAuth客户端
     */
    @Bean
    public com.aliyun.dingtalkoauth2_1_0.Client dingTalkOAuthClient(Config config) throws Exception {
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }

    /**
     * 创建钉钉机器人客户端
     */
    @Bean
    public com.aliyun.dingtalkrobot_1_0.Client dingTalkRobotClient(Config config) throws Exception {
        return new com.aliyun.dingtalkrobot_1_0.Client(config);
    }

    /**
     * 创建运行时选项
     */
    @Bean
    public RuntimeOptions runtimeOptions() {
        return new RuntimeOptions();
    }
    
    /**
     * 创建RestTemplate用于发送HTTP请求
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }
}