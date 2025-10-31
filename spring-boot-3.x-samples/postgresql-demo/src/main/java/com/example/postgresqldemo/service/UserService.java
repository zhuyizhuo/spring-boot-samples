package com.example.postgresqldemo.service;

import com.example.postgresqldemo.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    // 创建用户
    User createUser(User user);

    // 根据ID获取用户
    Optional<User> getUserById(Long id);

    // 根据用户名获取用户
    Optional<User> getUserByUsername(String username);

    // 获取所有用户
    List<User> getAllUsers();

    // 更新用户
    User updateUser(User user);

    // 删除用户
    void deleteUser(Long id);

    // 检查用户名是否存在
    boolean existsByUsername(String username);

    // 检查邮箱是否存在
    boolean existsByEmail(String email);
}