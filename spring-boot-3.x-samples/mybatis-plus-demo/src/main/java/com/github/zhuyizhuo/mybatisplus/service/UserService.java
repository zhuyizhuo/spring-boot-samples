package com.github.zhuyizhuo.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.zhuyizhuo.mybatisplus.model.User;

import java.util.List;

public interface UserService extends IService<User> {

    // 根据角色ID查询用户
    List<User> getUsersByRoleId(Long roleId);

    // 查询所有用户及其角色信息
    List<User> getAllUsersWithRoles();

    // 自定义分页查询用户
    List<User> getUsersByPage(int page, int pageSize);

    // 添加用户并分配角色
    boolean saveUserWithRoles(User user, List<Long> roleIds);

    // 更新用户角色
    boolean updateUserRoles(Long userId, List<Long> roleIds);

}