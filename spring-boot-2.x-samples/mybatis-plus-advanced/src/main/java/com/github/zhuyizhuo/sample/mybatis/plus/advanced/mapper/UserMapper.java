package com.github.zhuyizhuo.sample.mybatis.plus.advanced.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.zhuyizhuo.sample.mybatis.plus.advanced.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 * 继承BaseMapper获取基本CRUD功能，并添加自定义查询方法
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 自定义查询：根据年龄范围查询用户列表
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @return 用户列表
     */
    List<User> selectUsersByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    /**
     * 自定义分页查询：根据姓名模糊查询用户
     * @param page 分页对象
     * @param name 姓名关键词
     * @return 分页用户列表
     */
    IPage<User> selectUsersByNameLike(IPage<User> page, @Param("name") String name);
}