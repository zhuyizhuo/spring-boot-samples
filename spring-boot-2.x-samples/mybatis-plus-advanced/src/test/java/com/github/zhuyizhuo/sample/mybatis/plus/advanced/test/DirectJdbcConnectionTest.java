package com.github.zhuyizhuo.sample.mybatis.plus.advanced.test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 纯JDBC数据库连接测试，不依赖Spring框架
 * 直接验证数据库连接配置是否正确
 */
public class DirectJdbcConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("===== 开始纯JDBC数据库连接测试 =====");
        
        // 数据库连接信息
        String url = "jdbc:mysql://81.70.18.89:3306/spring-boot?useSSL=false&serverTimezone=UTC";
        String username = "management";
        String password = "Management3.2";
        String driverClass = "com.mysql.cj.jdbc.Driver";
        
        Connection connection = null;
        try {
            // 加载驱动
            Class.forName(driverClass);
            System.out.println("✅ 驱动加载成功: " + driverClass);
            
            // 建立连接
            System.out.println("正在连接数据库: " + url);
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ 数据库连接成功！");
            
            // 获取数据库元信息
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("\n数据库信息:");
            System.out.println("- 数据库产品名称: " + metaData.getDatabaseProductName());
            System.out.println("- 数据库版本: " + metaData.getDatabaseProductVersion());
            System.out.println("- JDBC驱动版本: " + metaData.getDriverVersion());
            System.out.println("- 连接URL: " + metaData.getURL());
            System.out.println("- 当前用户名: " + metaData.getUserName());
            
            // 测试查询
            testQuery(connection);
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ 驱动加载失败: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ 数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭连接
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("✅ 数据库连接已关闭");
                } catch (SQLException e) {
                    System.out.println("❌ 关闭连接失败: " + e.getMessage());
                }
            }
        }
        
        System.out.println("===== 纯JDBC数据库连接测试结束 =====");
    }
    
    /**
     * 测试简单查询
     */
    private static void testQuery(Connection connection) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            
            // 查询user表记录数
            String query = "SELECT COUNT(*) FROM user";
            System.out.println("\n执行查询: " + query);
            resultSet = statement.executeQuery(query);
            
            if (resultSet.next()) {
                long count = resultSet.getLong(1);
                System.out.println("✅ 查询成功! user表记录数: " + count);
            }
            
            // 查询数据库中的所有表
            System.out.println("\n数据库中的表:");
            ResultSet tables = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            int tableCount = 0;
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("- " + tableName);
                tableCount++;
            }
            System.out.println("✅ 共发现 " + tableCount + " 个表");
            
        } catch (SQLException e) {
            System.out.println("❌ 查询失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}