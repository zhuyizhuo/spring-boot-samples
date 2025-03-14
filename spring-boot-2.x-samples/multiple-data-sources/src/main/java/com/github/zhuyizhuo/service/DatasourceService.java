package com.github.zhuyizhuo.service;

import com.github.zhuyizhuo.datasource.master.QueryMapper;
import com.github.zhuyizhuo.datasource.slave.QueryMapper2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatasourceService {

    private QueryMapper queryMapper;

    private QueryMapper2 queryMapper2;

    public DatasourceService(QueryMapper queryMapper, QueryMapper2 queryMapper2) {
        this.queryMapper = queryMapper;
        this.queryMapper2 = queryMapper2;
    }

    public void query() {
        System.out.println("query");
        try {
            List l = queryMapper.query();
            System.out.println("数据库1查询总数: " + l.size());
            List l1 = queryMapper2.query();
            System.out.println("数据库2查询总数: " + l1.size());

            System.out.println("suc");
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
