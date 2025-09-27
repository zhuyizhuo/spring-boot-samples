package com.github.zhuyizhuo.sample.mybatis.plus.advanced.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 数据库连接测试类
 * 用于直接验证数据库连接是否正常
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = com.github.zhuyizhuo.sample.mybatis.plus.advanced.MybatisPlusAdvancedApplication.class)
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试数据源连接是否正常
     */
    @Test
    public void testDataSourceConnection() throws Exception {
        System.out.println("----- 测试数据源连接 -----\n");
        
        // 检查依赖注入是否成功
        if (dataSource == null) {
            System.out.println("错误: DataSource未成功注入");
            return;
        }
        
        // 打印数据源信息
        System.out.println("数据源类型: " + dataSource.getClass().getName());
        
        // 尝试获取数据库连接
        try (Connection connection = dataSource.getConnection()) {
            boolean isConnected = connection.isValid(5);
            System.out.println("数据库连接状态: " + (isConnected ? "已连接" : "未连接"));
            System.out.println("数据库URL: " + connection.getMetaData().getURL());
            System.out.println("数据库用户名: " + connection.getMetaData().getUserName());
            System.out.println("数据库产品名称: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("数据库产品版本: " + connection.getMetaData().getDatabaseProductVersion());
        } catch (Exception e) {
            System.out.println("获取数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n----- 数据源连接测试完成 -----\n");
    }

    /**
     * 测试JdbcTemplate是否可以执行SQL查询
     */
    @Test
    public void testJdbcTemplateQuery() {
        System.out.println("----- 测试JdbcTemplate查询 -----\n");
        
        // 检查依赖注入是否成功
        if (jdbcTemplate == null) {
            System.out.println("错误: JdbcTemplate未成功注入");
            return;
        }
        
        try {
            // 执行简单的查询，检查user表是否存在
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'user'",
                Integer.class
            );
            
            if (count != null && count > 0) {
                System.out.println("user表已存在");
                
                // 如果表存在，尝试查询记录数
                Long userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user", Long.class);
                System.out.println("user表中的记录数: " + userCount);
            } else {
                System.out.println("user表不存在，请先执行建表SQL");
            }
        } catch (Exception e) {
            System.out.println("查询执行失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n----- JdbcTemplate查询测试完成 -----\n");
    }
}