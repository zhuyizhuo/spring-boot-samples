package com.github.zhuyizhuo.datasource.master;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface QueryMapper {

    List<Map> query();

}
