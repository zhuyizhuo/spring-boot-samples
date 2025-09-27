package com.github.zhuyizhuo.sample.mybatis.plus.advanced.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.zhuyizhuo.sample.mybatis.plus.advanced.MybatisPlusAdvancedApplication;
import com.github.zhuyizhuo.sample.mybatis.plus.advanced.entity.User;
import com.github.zhuyizhuo.sample.mybatis.plus.advanced.mapper.UserMapper;
import com.github.zhuyizhuo.sample.mybatis.plus.advanced.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.List;

/**
 * MyBatis-Plus高级特性测试类
 */
@SpringBootTest(classes = MybatisPlusAdvancedApplication.class)
// @Disabled("测试需要可用的MySQL数据库环境，可在配置好数据库后手动启用")
public class SampleTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    /**
     * 测试基础查询功能
     */
    @Test
    public void testSelect() {
        System.out.println("----- 基础查询测试 -----");
        List<User> userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
    }

    /**
     * 测试条件构造器查询
     */
    @Test
    public void testConditionQuery() {
        System.out.println("----- 条件构造器查询测试 -----");
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
            .like(User::getName, "J")
            .ge(User::getAge, 18)
            .orderByDesc(User::getCreateTime);
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }

    /**
     * 测试分页查询功能
     */
    @Test
    public void testPageQuery() {
        System.out.println("----- 分页查询测试 -----");
        Page<User> page = new Page<>(1, 2);
        IPage<User> userPage = userMapper.selectPage(page, null);
        System.out.println("总记录数: " + userPage.getTotal());
        System.out.println("总页数: " + userPage.getPages());
        System.out.println("当前页数据:");
        userPage.getRecords().forEach(System.out::println);
    }

    /**
     * 测试自定义SQL查询
     */
    @Test
    public void testCustomSql() {
        System.out.println("----- 自定义SQL查询测试 -----");
        List<User> userList = userMapper.selectUsersByAgeRange(20, 30);
        userList.forEach(System.out::println);
    }

    /**
     * 测试自定义分页查询
     */
    @Test
    public void testCustomPageSql() {
        System.out.println("----- 自定义分页SQL查询测试 -----");
        Page<User> page = new Page<>(1, 2);
        IPage<User> userPage = userMapper.selectUsersByNameLike(page, "J");
        System.out.println("总记录数: " + userPage.getTotal());
        System.out.println("总页数: " + userPage.getPages());
        System.out.println("当前页数据:");
        userPage.getRecords().forEach(System.out::println);
    }

    /**
     * 测试Service层的批量操作
     */
    @Test
    public void testServiceBatchOperation() {
        System.out.println("----- Service层批量操作测试 -----");
        // 创建测试用户
        User user1 = new User();
        user1.setName("TestUser1");
        user1.setAge(25);
        user1.setEmail("test1@example.com");

        User user2 = new User();
        user2.setName("TestUser2");
        user2.setAge(26);
        user2.setEmail("test2@example.com");

        // 批量插入
        boolean result = userService.saveBatch(Arrays.asList(user1, user2));
        System.out.println("批量插入结果: " + result);
    }
}