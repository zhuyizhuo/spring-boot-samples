package com.github.zhuyizhuo.datasource.slave;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QueryMapper2 {

    List query();

}
