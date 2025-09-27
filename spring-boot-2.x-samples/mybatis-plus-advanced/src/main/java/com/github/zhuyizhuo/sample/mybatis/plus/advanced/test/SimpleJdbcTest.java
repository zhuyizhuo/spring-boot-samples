package com.github.zhuyizhuo.sample.mybatis.plus.advanced.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 简单的JDBC连接测试类，放在main目录下
 */
public class SimpleJdbcTest {
    
    public static void main(String[] args) {
        System.out.println("===== 开始简单JDBC连接测试 =====");
        
        // 数据库连接信息
        String url = "jdbc:mysql://81.70.18.89:3306/springboot?useSSL=false&serverTimezone=UTC";
        String username = "management";
        String password = "Management3.2";
        
        Connection connection = null;
        try {
            // 显式加载MySQL驱动
            System.out.println("尝试加载MySQL驱动...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL驱动加载成功");
            
            // 尝试建立连接
            System.out.println("正在连接数据库: " + url);
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ 数据库连接成功！");
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL驱动未找到: " + e.getMessage());
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
        
        System.out.println("===== 简单JDBC连接测试结束 =====");
    }
}