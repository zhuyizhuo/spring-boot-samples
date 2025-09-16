package com.github.zhuyizhuo.util;

import org.elasticsearch.action.DocWriteResponse;
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
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 工具类 (基于 RestHighLevelClient)
 * 注意: RestHighLevelClient 已被标记为弃用，但对于ES 7.x 仍是主流选择
 */
public class ElasticsearchUtil {

    private final RestHighLevelClient client;

    public ElasticsearchUtil(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * 判断索引是否存在
     */
    public boolean indexExists(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 创建索引
     */
    public boolean createIndex(String indexName) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    /**
     * 删除索引
     */
    public boolean deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    /**
     * 新增或更新文档 (ID不存在则新增，存在则更新)
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
     */
    public boolean updateDocument(String indexName, String id, Map<String, Object> updateFields) throws IOException {
        UpdateRequest request = new UpdateRequest(indexName, id)
                .doc(updateFields);
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        return response.status() == RestStatus.OK;
    }

    /**
     * 根据ID删除文档
     */
    public boolean deleteDocument(String indexName, String id) throws IOException {
        DeleteRequest request = new DeleteRequest(indexName, id);
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        return response.status() == RestStatus.OK;
    }

    /**
     * 批量操作 (新增、更新、删除)
     */
    public boolean bulkOperation(List<?> operations) throws IOException {
        // 这里需要根据业务逻辑构建BulkRequest，例如：
        // BulkRequest request = new BulkRequest();
        // for (SomeObject obj : operations) {
        //     request.add(new IndexRequest("index").id(obj.getId()).source(convertToMap(obj), XContentType.JSON));
        // }
        // BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        // return !response.hasFailures();
        // 具体实现取决于你的业务数据格式
        return false;
    }

    /**
     * 搜索文档 (简单匹配查询)
     */
    public List<Map<String, Object>> search(String indexName, String field, String value) throws IOException {
        return search(indexName, field, value, 0, 10);
    }

    /**
     * 搜索文档 (带分页)
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
     */
    public List<Map<String, Object>> search(SearchRequest request) throws IOException {
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        return getHitList(response);
    }

    /**
     * 解析搜索结果
     */
    private List<Map<String, Object>> getHitList(SearchResponse response) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            list.add(hit.getSourceAsMap());
        }
        return list;
    }

    /**
     * 关闭客户端连接 (重要！需要在应用关闭时调用)
     */
    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
    }
}