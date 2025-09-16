package com.github.zhuyizhuo.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ESClientConfig {

    public static RestHighLevelClient createClient() {
       /* return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http") // 根据你的ES集群配置添加多个节点
                        //, new HttpHost("localhost", 9201, "http")
                )
        );*/

        String urls = "test-3-40000-jiesi-jdos-seller-crm.jd.local,test-2-40000-jiesi-jdos-seller-crm.jd.local,test-1-40000-jiesi-jdos-seller-crm.jd.local";
        String[] elasticsearchUris = urls.split(",");
        String username = "jiesi-jdos-seller-crm";
        String password = "9C081EC73B88AE21";
        Integer port = 40000;

        String scheme = "http";

        // 1. 解析配置的URI字符串，转换为HttpHost数组
        List<HttpHost> httpHosts = Arrays.stream(elasticsearchUris)
                .map(uri -> {
                    // 简单的URI解析，例如将 "http://node1:9200" 转换为 HttpHost 对象
//                    String[] parts = uri.split("://")[1].split(":");
//                    String host = parts[0];
//                    int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9200;
//                    String scheme = uri.startsWith("https") ? "https" : "http";
                    return new HttpHost(uri, port, scheme);
                })
                .collect(Collectors.toList());

        // 2. 配置认证信息
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        // 3. 构建RestClientBuilder，配置多个节点和嗅探
        RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[0])) // [!code ++] // [!code focus] // 传入节点数组
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider));
//                .setSniff(true); // [!code ++] // [!code focus] // 启用嗅探

        return new RestHighLevelClient(builder);
/*
        // 1. 创建凭证提供者，并设置用户名和密码
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, // 认证范围，ANY 表示任何主机和端口
                new UsernamePasswordCredentials("your_username", "your_password")); // 替换为你的用户名和密码

        // 2. 构建 RestClientBuilder，并配置 HttpClient 的回调
        RestClientBuilder builder = RestClient.builder(
                        new HttpHost("localhost", 9200, "http")) // 配置ES服务器地址和端口
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider) // 设置凭证提供者
                        // .disableAuthCaching(); // 如果需要禁用认证缓存，可取消注释此行:cite[3]
                );

        // 3. 创建 RestHighLevelClient
        return new RestHighLevelClient(builder);*/
    }
}