package com.github.zhuyizhuo;


import com.github.zhuyizhuo.config.ESClientConfig;
import com.github.zhuyizhuo.util.ElasticsearchUtil;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestES {
    public static void main(String[] args) {
        RestHighLevelClient client = ESClientConfig.createClient();
        ElasticsearchUtil esUtil = new ElasticsearchUtil(client);

        try {
            // 1. 创建索引
//            boolean created = esUtil.createIndex("user_index");
//            System.out.println("索引创建: " + created);

            // 2. 插入文档
//            String json = "{\"name\":\"王五\",\"age\":22,\"city\":\"北京\"}";
            String json = "{\"sourceList\":[3,4],\"registrationCapital\":16065995,\"enterpriseName\":\"商行天司术北京司1\"}";
            String number = "223";
            boolean inserted = esUtil.upsertDocument("seller_crm_customer_list_info", number, json);
            System.out.println("文档插入: " + inserted);

//            // 3. 查询文档
//            Map<String, Object> doc = esUtil.getDocumentById("seller_crm_customer_list_info", "2");
//            System.out.println("查询结果: " + doc);
//
//            // 4. 搜索文档
//            List<Map<String, Object>> results = esUtil.search("seller_crm_customer_list_info", "enterpriseName", "中化方胜能源管理服务有限公司萍乡分公司");
//            System.out.println("搜索结果: " + results);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                esUtil.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
