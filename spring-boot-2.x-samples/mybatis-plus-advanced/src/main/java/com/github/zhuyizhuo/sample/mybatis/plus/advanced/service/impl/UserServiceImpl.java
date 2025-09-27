package com.github.zhuyizhuo.sample.mybatis.plus.advanced.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.zhuyizhuo.sample.mybatis.plus.advanced.entity.User;
import com.github.zhuyizhuo.sample.mybatis.plus.advanced.mapper.UserMapper;
import com.github.zhuyizhuo.sample.mybatis.plus.advanced.service.UserService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户服务实现类
 * 继承ServiceImpl并实现UserService接口
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 根据年龄范围查询用户列表
     */
    @Override
    public List<User> getUsersByAgeRange(Integer minAge, Integer maxAge) {
        return baseMapper.selectUsersByAgeRange(minAge, maxAge);
    }

    /**
     * 分页查询用户列表
     */
    @Override
    public IPage<User> getUsersByPage(Integer page, Integer size) {
        Page<User> userPage = new Page<>(page, size);
        return baseMapper.selectPage(userPage, null);
    }

    /**
     * 根据姓名模糊查询用户并分页
     */
    @Override
    public IPage<User> getUsersByNameLike(String name, Integer page, Integer size) {
        Page<User> userPage = new Page<>(page, size);
        return baseMapper.selectUsersByNameLike(userPage, name);
    }

    /**
     * 批量插入用户
     */
    @Override
    public boolean saveBatchUsers(List<User> userList) {
        return saveBatch(userList);
    }
}