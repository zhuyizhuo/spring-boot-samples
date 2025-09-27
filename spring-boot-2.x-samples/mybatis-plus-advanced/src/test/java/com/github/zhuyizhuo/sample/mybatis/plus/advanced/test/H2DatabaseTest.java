package com.github.zhuyizhuo.sample.mybatis.plus.advanced.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 测试使用H2内存数据库的Spring上下文初始化
 */
@SpringBootTest(classes = com.github.zhuyizhuo.sample.mybatis.plus.advanced.MybatisPlusAdvancedApplication.class)
public class H2DatabaseTest {

    @Autowired
    private DataSource dataSource;

    /**
     * 测试DataSource是否能够成功注入
     */
    @Test
    public void testDataSourceInjection() {
        System.out.println("===== 测试DataSource注入 ====");
        if (dataSource != null) {
            System.out.println("✅ DataSource注入成功！");
            try {
                Connection connection = dataSource.getConnection();
                if (connection != null && !connection.isClosed()) {
                    System.out.println("✅ 成功获取数据库连接！");
                    System.out.println("数据库URL: " + connection.getMetaData().getURL());
                    System.out.println("数据库产品名称: " + connection.getMetaData().getDatabaseProductName());
                    System.out.println("数据库版本: " + connection.getMetaData().getDatabaseProductVersion());
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("❌ 获取数据库连接失败: " + e.getMessage());
            }
        } else {
            System.out.println("❌ DataSource未成功注入！");
        }
    }
}