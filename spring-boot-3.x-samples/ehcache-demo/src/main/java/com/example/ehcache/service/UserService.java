package com.example.ehcache.service;

import com.example.ehcache.entity.User;

import java.util.List;

/**
 * 用户服务接口
 * 定义用户相关的业务逻辑接口
 */
public interface UserService {

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);

    /**
     * 获取所有用户信息
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 保存用户信息
     * @param user 用户信息
     * @return 保存后的用户信息
     */
    User saveUser(User user);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User updateUser(User user);

    /**
     * 删除用户信息
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);

    /**
     * 清除所有用户相关缓存
     */
    void clearUserCache();
}