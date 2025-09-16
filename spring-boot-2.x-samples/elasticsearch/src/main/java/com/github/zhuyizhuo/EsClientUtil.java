package com.github.zhuyizhuo;

import com.github.zhuyizhuo.config.ESClientConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EsClientUtil {

    private static RestHighLevelClient client;

    // 初始化客户端
    static {
        client = ESClientConfig.createClient();
    }

    public static RestHighLevelClient getClient() {
        return client;
    }

    /**
     * 新增/覆盖文档
     */
    public static String addDocument(String index, String id, Map<String, Object> data) throws IOException {
        IndexRequest request = new IndexRequest(index).id(id).source(data);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response.getResult().toString();
    }

    /**
     * 获取文档
     */
    public static String getDocument(String index, String id) throws IOException {
        GetRequest request = new GetRequest(index, id);
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        if (response.isExists()) {
            return response.getSourceAsString();
        }
        return null;
    }

    /**
     * 更新文档
     */
    public static String updateDocument(String index, String id, Map<String, Object> data) throws IOException {
        UpdateRequest request = new UpdateRequest(index, id).doc(data);
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        return response.getResult().toString();
    }

    /**
     * 删除文档
     */
    public static String deleteDocument(String index, String id) throws IOException {
        DeleteRequest request = new DeleteRequest(index, id);
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        return response.getResult().toString();
    }

    /**
     * 条件查询 + 指定字段返回 + 多字段排序
     * @param index 索引名
     * @param queryBuilder 查询条件（termQuery、rangeQuery 等）
     * @param includes 返回字段（null 表示返回全部）
     * @param excludes 排除字段（可为 null）
     * @param sortBuilders 排序规则集合
     * @param size 返回条数
     */
    public static List<Map<String, Object>> searchWithFilterAndSort(
            String index,
            QueryBuilder queryBuilder,
            String[] includes,
            String[] excludes,
            List<SortBuilder<?>> sortBuilders,
            int size
    ) throws IOException {

        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 查询条件
        if (queryBuilder != null) {
            sourceBuilder.query(queryBuilder);
        }

        // 指定返回字段
        if (includes != null || excludes != null) {
            sourceBuilder.fetchSource(new FetchSourceContext(true, includes, excludes));
        }

        // 排序规则
        if (sortBuilders != null) {
            for (SortBuilder<?> sortBuilder : sortBuilders) {
                sourceBuilder.sort(sortBuilder);
            }
        }

        sourceBuilder.size(size);
        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            results.add(hit.getSourceAsMap());
        }
        return results;
    }

    /**
     * 构造脚本排序 (示例：sourceList 包含 2 的排最前, 再是包含 1 的, 最后是其他)
     */
    public static ScriptSortBuilder buildSourceListPrioritySort() {
        String scriptCode =
//                "def list = doc['sourceList'];" +
//                        "double priority = 3.0;" +
//                        "for (item in list) {" +
//                        "  if (item == 1.0) { priority = Math.min(priority, 0.0); }" +
//                        "  else if (item == 2.0) { priority = Math.min(priority, 1.0); }" +
//                        "  else if (item == 3.0) { priority = Math.min(priority, 2.0); }" +
//                        "}" +
//                        "return priority;";
                "def list = doc['sourceList'];" +
                        "if (list.contains(1.0)) { return 0; } " +
                        "else if (list.contains(2.0)) { return 1; } " +
                        "else if (list.contains(3.0)) { return 2; } " +
                        "else if (list.contains(4.0)) { return 3; } " +
                        "else { return 9; }";
        Script script = new Script(scriptCode);
        return new ScriptSortBuilder(script, ScriptSortBuilder.ScriptSortType.NUMBER)
                .order(SortOrder.ASC);
    }

    /**
     * 关闭客户端
     */
    public static void close() throws IOException {
        if (client != null) {
            client.close();
        }
    }
}
