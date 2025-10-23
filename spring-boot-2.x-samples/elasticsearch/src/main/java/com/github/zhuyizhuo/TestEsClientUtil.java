package com.github.zhuyizhuo;

import com.github.zhuyizhuo.util.EsClientUtil;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestEsClientUtil {
    public static void main(String[] args) throws Exception {
        String index = "seller_crm_customer_list_info";

        // 条件：查询 registrationCapital >= 500
        QueryBuilder query = QueryBuilders.rangeQuery("registrationCapital").gte(500);

        // 只返回 enterpriseName 和 registrationCapital
        String[] includes = {"enterpriseName", "registrationCapital", "sourceList"};
        String[] excludes = null;

        // 排序规则：1. registrationCapital 升序, 2. sourceList 脚本排序
        List<SortBuilder<?>> sortBuilders = Arrays.asList(
                new FieldSortBuilder("registrationCapital").order(SortOrder.DESC),
                EsClientUtil.buildSourceListPrioritySort()
        );

        List<Map<String, Object>> results = EsClientUtil.searchWithFilterAndSort(
                index, query, includes, excludes, sortBuilders, 20
        );

        System.out.println("查询+排序结果:");
        for (Map<String, Object> r : results) {
            System.out.println(r);
        }

        EsClientUtil.close();
    }
}

