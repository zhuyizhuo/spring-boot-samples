package com.github.zhuyizhuo.util;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 工具类
 * 使用最新的 RestHighLevelClient API 设计模式
 */
public class ElasticsearchUtil implements AutoCloseable {

    private final RestHighLevelClient client;

    /**
     * 构造函数，接受 ES 客户端实例
     * @param client RestHighLevelClient 实例
     */
    public ElasticsearchUtil(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * 判断索引是否存在
     * @param indexName 索引名称
     * @return 索引是否存在
     * @throws IOException 可能的IO异常
     */
    public boolean indexExists(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 创建索引
     * @param indexName 索引名称
     * @return 是否创建成功
     * @throws IOException 可能的IO异常
     */
    public boolean createIndex(String indexName) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        // 使用非静态方法设置索引配置
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    /**
     * 删除索引
     * @param indexName 索引名称
     * @return 是否删除成功
     * @throws IOException 可能的IO异常
     */
    public boolean deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    /**
     * 新增或更新文档 (ID不存在则新增，存在则更新)
     * @param indexName 索引名称
     * @param id 文档ID
     * @param jsonSource JSON格式的文档内容
     * @return 是否操作成功
     * @throws IOException 可能的IO异常
     */
    public boolean upsertDocument(String indexName, String id, String jsonSource) throws IOException {
        IndexRequest request = new IndexRequest(indexName)
                .id(id)
                .source(jsonSource, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response.status() == RestStatus.CREATED || response.status() == RestStatus.OK;
    }

    /**
     * 根据ID获取文档
     * @param indexName 索引名称
     * @param id 文档ID
     * @return 文档内容，不存在返回null
     * @throws IOException 可能的IO异常
     */
    public Map<String, Object> getDocumentById(String indexName, String id) throws IOException {
        GetRequest request = new GetRequest(indexName, id);
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        if (response.isExists()) {
            return response.getSourceAsMap();
        }
        return null;
    }

    /**
     * 根据ID更新文档 (部分字段)
     * @param indexName 索引名称
     * @param id 文档ID
     * @param updateFields 要更新的字段
     * @return 是否更新成功
     * @throws IOException 可能的IO异常
     */
    public boolean updateDocument(String indexName, String id, Map<String, Object> updateFields) throws IOException {
        UpdateRequest request = new UpdateRequest(indexName, id)
                .doc(updateFields);
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        return response.status() == RestStatus.OK;
    }

    /**
     * 根据ID删除文档
     * @param indexName 索引名称
     * @param id 文档ID
     * @return 是否删除成功
     * @throws IOException 可能的IO异常
     */
    public boolean deleteDocument(String indexName, String id) throws IOException {
        DeleteRequest request = new DeleteRequest(indexName, id);
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        return response.status() == RestStatus.OK;
    }

    /**
     * 批量操作 (新增、更新、删除)
     * @param operations 操作列表
     * @return 是否全部成功
     * @throws IOException 可能的IO异常
     */
    public boolean bulkOperation(List<?> operations) throws IOException {
        // 实际业务场景中需要实现具体逻辑
        return false;
    }

    /**
     * 搜索文档 (简单匹配查询)
     * @param indexName 索引名称
     * @param field 字段名
     * @param value 搜索值
     * @return 搜索结果列表
     * @throws IOException 可能的IO异常
     */
    public List<Map<String, Object>> search(String indexName, String field, String value) throws IOException {
        return search(indexName, field, value, 0, 10);
    }

    /**
     * 搜索文档 (带分页)
     * @param indexName 索引名称
     * @param field 字段名
     * @param value 搜索值
     * @param from 起始位置
     * @param size 返回数量
     * @return 搜索结果列表
     * @throws IOException 可能的IO异常
     */
    public List<Map<String, Object>> search(String indexName, String field, String value, int from, int size) throws IOException {
        SearchRequest request = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery(field, value))
                .from(from)
                .size(size)
                .sort("_score", SortOrder.DESC);
        request.source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        return getHitList(response);
    }

    /**
     * 通用搜索方法
     * @param request 搜索请求
     * @return 搜索结果列表
     * @throws IOException 可能的IO异常
     */
    public List<Map<String, Object>> search(SearchRequest request) throws IOException {
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        return getHitList(response);
    }

    /**
     * 解析搜索结果
     * @param response 搜索响应
     * @return 结果列表
     */
    private List<Map<String, Object>> getHitList(SearchResponse response) {
        List<Map<String, Object>> list = new ArrayList<>();
        SearchHits hits = response.getHits();
        if (hits != null) {
            for (SearchHit hit : hits.getHits()) {
                list.add(hit.getSourceAsMap());
            }
        }
        return list;
    }

    /**
     * 关闭客户端连接
     * @throws IOException 可能的IO异常
     */
    @Override
    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
    }
}