package com.example.memcache.service;

import com.example.memcache.model.User;
import java.util.List;

public interface UserService {
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户对象
     */
    User getUserById(Long id);

    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 添加用户
     * @param user 用户对象
     * @return 添加后的用户对象
     */
    User addUser(User user);

    /**
     * 更新用户
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    User updateUser(User user);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);

    /**
     * 清除缓存
     * @param id 用户ID，为null时清除所有缓存
     */
    void clearCache(Long id);
}