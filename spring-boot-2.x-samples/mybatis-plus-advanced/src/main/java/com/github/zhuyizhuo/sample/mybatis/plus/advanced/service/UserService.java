package com.github.zhuyizhuo.sample.mybatis.plus.advanced.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.zhuyizhuo.sample.mybatis.plus.advanced.entity.User;
import java.util.List;

/**
 * 用户服务接口
 * 继承IService获取高级CRUD功能
 */
public interface UserService extends IService<User> {

    /**
     * 根据年龄范围查询用户列表
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @return 用户列表
     */
    List<User> getUsersByAgeRange(Integer minAge, Integer maxAge);

    /**
     * 分页查询用户列表
     * @param page 页码
     * @param size 每页数量
     * @return 分页用户列表
     */
    IPage<User> getUsersByPage(Integer page, Integer size);

    /**
     * 根据姓名模糊查询用户并分页
     * @param name 姓名关键词
     * @param page 页码
     * @param size 每页数量
     * @return 分页用户列表
     */
    IPage<User> getUsersByNameLike(String name, Integer page, Integer size);

    /**
     * 批量插入用户
     * @param userList 用户列表
     * @return 插入成功的数量
     */
    boolean saveBatchUsers(List<User> userList);
}