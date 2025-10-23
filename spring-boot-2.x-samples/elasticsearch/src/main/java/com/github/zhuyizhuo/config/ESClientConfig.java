package com.github.zhuyizhuo.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Elasticsearch 客户端配置类
 * 提供从配置文件加载连接信息的功能，避免硬编码
 * 支持多节点配置、认证、连接池和超时设置
 * 
 * <h2>环境与依赖版本信息</h2>
 * <ul>
 *   <li><strong>Spring Boot</strong>: 2.x</li>
 *   <li><strong>Elasticsearch</strong>: 7.17.0</li>
 *   <li><strong>Elasticsearch客户端</strong>:
 *     <ul>
 *       <li>elasticsearch-rest-high-level-client: 7.17.0</li>
 *       <li>elasticsearch-rest-client: 7.17.0</li>
 *     </ul>
 *   </li>
 *   <li><strong>JSON处理</strong>: jackson-databind 2.14.2</li>
 *   <li><strong>日志</strong>: commons-logging 1.2</li>
 *   <li><strong>适用环境</strong>: Java 8+</li>
 * </ul>
 * 
 * <h2>配置说明</h2>
 * 配置文件: application.properties<br>
 * 主要配置项前缀: elasticsearch.<br>
 * 支持配置多节点URL、端口、认证信息、超时设置和连接池参数
 */
public final class ESClientConfig {
    
    private static final Logger logger = Logger.getLogger(ESClientConfig.class.getName());
    private static final Properties properties = new Properties();
    private static final String PROPERTIES_FILE = "application.properties";
    
    // 配置项常量
    private static final String PROP_URLS = "elasticsearch.urls";
    private static final String PROP_PORT = "elasticsearch.port";
    private static final String PROP_USERNAME = "elasticsearch.username";
    private static final String PROP_PASSWORD = "elasticsearch.password";
    private static final String PROP_SCHEME = "elasticsearch.scheme";
    private static final String PROP_CONNECTION_TIMEOUT = "elasticsearch.connection-timeout";
    private static final String PROP_SOCKET_TIMEOUT = "elasticsearch.socket-timeout";
    private static final String PROP_MAX_TOTAL_CONNECTIONS = "elasticsearch.max-total-connections";
    private static final String PROP_DEFAULT_MAX_PER_ROUTE = "elasticsearch.default-max-per-route";
    
    // 静态初始化块 - 加载配置
    static {
        try (InputStream input = ESClientConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                logger.warning("无法找到配置文件: " + PROPERTIES_FILE + ", 使用默认配置");
                // 设置默认值
                setDefaultProperties();
            } else {
                properties.load(input);
                logger.info("配置文件加载成功: " + PROPERTIES_FILE);
            }
        } catch (IOException ex) {
            logger.severe("加载配置文件失败，使用默认配置: " + ex.getMessage());
            ex.printStackTrace();
            setDefaultProperties();
        }
    }
    
    /**
     * 设置默认配置值
     * 当配置文件不存在或加载失败时使用
     */
    private static void setDefaultProperties() {
        properties.setProperty(PROP_URLS, "localhost");
        properties.setProperty(PROP_PORT, "9200");
        properties.setProperty(PROP_USERNAME, "");
        properties.setProperty(PROP_PASSWORD, "");
        properties.setProperty(PROP_SCHEME, "http");
        properties.setProperty(PROP_CONNECTION_TIMEOUT, "10000");
        properties.setProperty(PROP_SOCKET_TIMEOUT, "30000");
        properties.setProperty(PROP_MAX_TOTAL_CONNECTIONS, "30");
        properties.setProperty(PROP_DEFAULT_MAX_PER_ROUTE, "10");
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private ESClientConfig() {
        throw new AssertionError("不允许实例化此类");
    }
    
    /**
     * 创建 Elasticsearch 高级客户端
     * 
     * @return 配置好的 RestHighLevelClient 实例
     * @throws IllegalArgumentException 如果配置参数无效
     */
    public static RestHighLevelClient createClient() {
        try {
            // 1. 从配置文件读取值
            String elasticsearchUrls = properties.getProperty(PROP_URLS);
            int elasticsearchPort = parseIntSafely(PROP_PORT, 9200);
            String elasticsearchUsername = properties.getProperty(PROP_USERNAME, "");
            String elasticsearchPassword = properties.getProperty(PROP_PASSWORD, "");
            String elasticsearchScheme = properties.getProperty(PROP_SCHEME, "http");
            int connectionTimeout = parseIntSafely(PROP_CONNECTION_TIMEOUT, 10000);
            int socketTimeout = parseIntSafely(PROP_SOCKET_TIMEOUT, 30000);
            int maxTotalConnections = parseIntSafely(PROP_MAX_TOTAL_CONNECTIONS, 30);
            int defaultMaxPerRoute = parseIntSafely(PROP_DEFAULT_MAX_PER_ROUTE, 10);
            
            // 验证必要的配置
            if (elasticsearchUrls == null || elasticsearchUrls.trim().isEmpty()) {
                throw new IllegalArgumentException("Elasticsearch URLs 配置不能为空");
            }
            
            // 2. 解析配置的URI字符串，转换为HttpHost数组
            List<HttpHost> httpHosts = Arrays.stream(elasticsearchUrls.split(","))
                    .map(uri -> uri.trim())
                    .filter(uri -> !uri.isEmpty())
                    .map(uri -> new HttpHost(uri, elasticsearchPort, elasticsearchScheme))
                    .collect(Collectors.toList());
            
            if (httpHosts.isEmpty()) {
                throw new IllegalArgumentException("没有有效的Elasticsearch主机配置");
            }
            
            logger.info("将连接到Elasticsearch集群，节点数: " + httpHosts.size());
            
            // 3. 配置认证信息（如果提供了用户名密码）
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            boolean hasAuth = elasticsearchUsername != null && !elasticsearchUsername.isEmpty();
            
            if (hasAuth) {
                credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(elasticsearchUsername, elasticsearchPassword));
                logger.info("已配置基本认证");
            }

            // 4. 构建RestClientBuilder，配置多个节点、连接超时、套接字超时和认证
            RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[0]))
                    // 配置请求超时
                    .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                            .setConnectTimeout(connectionTimeout)
                            .setSocketTimeout(socketTimeout))
                    // 配置HTTP客户端
                    .setHttpClientConfigCallback((HttpAsyncClientBuilder httpClientBuilder) -> {
                        // 设置认证（如果需要）
                        if (hasAuth) {
                            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        }
                        // 配置连接池
                        return httpClientBuilder
                                .setMaxConnTotal(maxTotalConnections)
                                .setMaxConnPerRoute(defaultMaxPerRoute);
                    });
            
            // 5. 构建并返回客户端实例
            return new RestHighLevelClient(builder);
            
        } catch (Exception e) {
            logger.severe("创建Elasticsearch客户端失败: " + e.getMessage());
            throw new RuntimeException("无法创建Elasticsearch客户端", e);
        }
    }
    
    /**
     * 安全地解析整数配置
     * 
     * @param propertyName 配置属性名
     * @param defaultValue 默认值
     * @return 解析后的整数值或默认值
     */
    private static int parseIntSafely(String propertyName, int defaultValue) {
        try {
            String value = properties.getProperty(propertyName);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warning("配置项 " + propertyName + " 不是有效整数，使用默认值: " + defaultValue);
            return defaultValue;
        }
    }
}