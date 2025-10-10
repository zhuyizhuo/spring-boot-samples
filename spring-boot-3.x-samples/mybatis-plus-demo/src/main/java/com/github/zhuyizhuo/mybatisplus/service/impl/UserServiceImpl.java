package com.github.zhuyizhuo.mybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.zhuyizhuo.mybatisplus.mapper.RoleMapper;
import com.github.zhuyizhuo.mybatisplus.mapper.UserMapper;
import com.github.zhuyizhuo.mybatisplus.mapper.UserRoleMapper;
import com.github.zhuyizhuo.mybatisplus.model.User;
import com.github.zhuyizhuo.mybatisplus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<User> getUsersByRoleId(Long roleId) {
        return userMapper.selectByRoleId(roleId);
    }

    @Override
    public List<User> getAllUsersWithRoles() {
        return userMapper.selectUsersWithRoles();
    }

    @Override
    public List<User> getUsersByPage(int page, int pageSize) {
        // 计算分页偏移量
        int offset = (page - 1) * pageSize;
        
        // 使用MyBatis Plus的分页查询
        return userMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                        .last("LIMIT " + offset + ", " + pageSize)
        );
    }

    @Transactional
    @Override
    public boolean saveUserWithRoles(User user, List<Long> roleIds) {
        // 保存用户信息
        boolean saved = save(user);
        if (saved && roleIds != null && !roleIds.isEmpty()) {
            // 使用专门的UserRoleMapper保存用户角色关系
            userRoleMapper.batchInsertUserRoles(user.getId(), roleIds);
        }
        return saved;
    }

    @Transactional
    @Override
    public boolean updateUserRoles(Long userId, List<Long> roleIds) {
        // 先删除用户原有角色
        userRoleMapper.deleteByUserId(userId);
        
        // 然后添加新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            userRoleMapper.batchInsertUserRoles(userId, roleIds);
        }
        return true;
    }

}