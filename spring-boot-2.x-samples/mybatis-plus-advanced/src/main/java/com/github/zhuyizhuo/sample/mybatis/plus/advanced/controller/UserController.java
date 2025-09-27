package com.github.zhuyizhuo.sample.mybatis.plus.advanced.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.zhuyizhuo.sample.mybatis.plus.advanced.entity.User;
import com.github.zhuyizhuo.sample.mybatis.plus.advanced.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 用户控制器
 * 提供RESTful API接口
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户列表
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.list();
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * 创建新用户
     */
    @PostMapping
    public boolean createUser(@RequestBody User user) {
        return userService.save(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public boolean updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.updateById(user);
    }

    /**
     * 删除用户（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        return userService.removeById(id);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public IPage<User> getUsersByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return userService.getUsersByPage(page, size);
    }

    /**
     * 根据年龄范围查询用户
     */
    @GetMapping("/age-range")
    public List<User> getUsersByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        return userService.getUsersByAgeRange(minAge, maxAge);
    }

    /**
     * 根据姓名模糊查询用户
     */
    @GetMapping("/search")
    public IPage<User> searchUsersByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return userService.getUsersByNameLike(name, page, size);
    }

    /**
     * 批量创建用户
     */
    @PostMapping("/batch")
    public boolean batchCreateUsers(@RequestBody List<User> users) {
        return userService.saveBatchUsers(users);
    }
}