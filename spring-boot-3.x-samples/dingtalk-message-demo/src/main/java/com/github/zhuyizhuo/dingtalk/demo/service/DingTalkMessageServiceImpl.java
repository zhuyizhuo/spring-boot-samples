package com.github.zhuyizhuo.dingtalk.demo.service;

import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.github.zhuyizhuo.dingtalk.demo.config.DingTalkProperties;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 钉钉消息服务实现类
 */
@Service
public class DingTalkMessageServiceImpl implements DingTalkMessageService {
    private static final Logger log = LoggerFactory.getLogger(DingTalkMessageServiceImpl.class);

    private final DingTalkProperties dingTalkProperties;
    private final com.aliyun.dingtalkoauth2_1_0.Client dingTalkOAuthClient;
    private final com.aliyun.dingtalkrobot_1_0.Client dingTalkRobotClient;
    private final RuntimeOptions runtimeOptions;
    private final Gson gson;
    private final RestTemplate restTemplate;

    // 手动实现构造函数
    public DingTalkMessageServiceImpl(DingTalkProperties dingTalkProperties,
                                     com.aliyun.dingtalkoauth2_1_0.Client dingTalkOAuthClient,
                                     com.aliyun.dingtalkrobot_1_0.Client dingTalkRobotClient,
                                     RuntimeOptions runtimeOptions,
                                     Gson gson,
                                     RestTemplate restTemplate) {
        this.dingTalkProperties = dingTalkProperties;
        this.dingTalkOAuthClient = dingTalkOAuthClient;
        this.dingTalkRobotClient = dingTalkRobotClient;
        this.runtimeOptions = runtimeOptions;
        this.gson = gson;
        this.restTemplate = restTemplate;
    }

    /**
     * 获取钉钉访问令牌
     */
    private String getAccessToken() throws Exception {
        GetAccessTokenRequest request = new GetAccessTokenRequest();
        request.setAppKey(dingTalkProperties.getAppKey());
        request.setAppSecret(dingTalkProperties.getAppSecret());
        GetAccessTokenResponse response = dingTalkOAuthClient.getAccessToken(request);
        return response.getBody().getAccessToken();
    }

    /**
     * 生成签名
     */
    private String generateSign() throws Exception {
        Long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + dingTalkProperties.getSecret();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(dingTalkProperties.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
    }

    @Override
    public boolean sendTextMessage(String content, String[] atMobiles, boolean isAtAll) {
        try {
            // 构建文本消息
            Map<String, Object> text = new HashMap<>();
            text.put("content", content);

            Map<String, Object> at = new HashMap<>();
            if (atMobiles != null && atMobiles.length > 0) {
                at.put("atMobiles", atMobiles);
            }
            at.put("isAtAll", isAtAll);

            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "text");
            message.put("text", text);
            message.put("at", at);

            // 发送消息
            String sign = generateSign();
            Long timestamp = System.currentTimeMillis();

            // 使用webhook和签名发送实际请求
            String webhook = dingTalkProperties.getWebhook() + "&timestamp=" + timestamp + "&sign=" + sign;
            
            log.info("准备发送钉钉文本消息到: {}", webhook);
            log.info("消息内容: {}", gson.toJson(message));
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 创建请求实体
            HttpEntity<String> requestEntity = new HttpEntity<>(gson.toJson(message), headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(webhook, requestEntity, String.class);
            
            log.info("钉钉文本消息发送结果: {}", response.getBody());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("发送钉钉文本消息失败", e);
            return false;
        }
    }

    @Override
    public boolean sendMarkdownMessage(String title, String text, String[] atMobiles, boolean isAtAll) {
        try {
            // 构建Markdown消息
            Map<String, Object> markdown = new HashMap<>();
            markdown.put("title", title);
            markdown.put("text", text);

            Map<String, Object> at = new HashMap<>();
            if (atMobiles != null && atMobiles.length > 0) {
                at.put("atMobiles", atMobiles);
            }
            at.put("isAtAll", isAtAll);

            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "markdown");
            message.put("markdown", markdown);
            message.put("at", at);

            // 发送消息
            String sign = generateSign();
            Long timestamp = System.currentTimeMillis();

            // 使用webhook方式发送实际请求
            String webhook = dingTalkProperties.getWebhook() + "&timestamp=" + timestamp + "&sign=" + sign;
            
            log.info("准备发送钉钉Markdown消息到: {}", webhook);
            log.info("消息内容: {}", gson.toJson(message));
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 创建请求实体
            HttpEntity<String> requestEntity = new HttpEntity<>(gson.toJson(message), headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(webhook, requestEntity, String.class);
            
            log.info("钉钉Markdown消息发送结果: {}", response.getBody());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("发送钉钉Markdown消息失败", e);
            return false;
        }
    }

    @Override
    public boolean sendWorkNoticeMessage(String userId, String title, String content) {
        try {
            // 获取访问令牌
            String accessToken = getAccessToken();
            
            // 构建工作通知消息
            Map<String, Object> msgBody = new HashMap<>();
            msgBody.put("msgtype", "text");
            Map<String, String> text = new HashMap<>();
            text.put("content", content);
            msgBody.put("text", text);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("agent_id", dingTalkProperties.getAppKey());
            requestBody.put("userid_list", userId);
            requestBody.put("msg", msgBody);
            requestBody.put("title", title);

            // 钉钉工作通知API地址
            String apiUrl = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=" + accessToken;
            
            log.info("准备发送钉钉工作通知消息到: {}", apiUrl);
            log.info("消息内容: {}", gson.toJson(requestBody));
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 创建请求实体
            HttpEntity<String> requestEntity = new HttpEntity<>(gson.toJson(requestBody), headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
            
            log.info("钉钉工作通知发送结果: {}", response.getBody());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("发送钉钉工作通知消息失败", e);
            return false;
        }
    }
}