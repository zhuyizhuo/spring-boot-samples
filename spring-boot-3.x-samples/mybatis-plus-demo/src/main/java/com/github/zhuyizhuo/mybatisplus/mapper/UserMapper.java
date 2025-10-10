package com.github.zhuyizhuo.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.zhuyizhuo.mybatisplus.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 自定义查询方法：通过角色ID查询用户
    List<User> selectByRoleId(Long roleId);

    // 自定义查询方法：查询用户及其角色信息
    List<User> selectUsersWithRoles();

}