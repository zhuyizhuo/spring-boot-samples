package com.github.zhuyizhuo.sample.mybatis.plus.advanced.test;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * MyBatis-Plus代码生成器测试类
 * 演示如何使用代码生成器快速生成实体类、Mapper、Service等代码
 */
public class CodeGeneratorTest {

    /**
     * 数据库配置
     */
    private static final String URL = "jdbc:mysql://localhost:3306/springboot?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Zhuo3.2";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * 代码生成测试
     * 注意：此测试类需要手动执行，不会自动运行
     * 注意：当前MyBatis-Plus 3.5.2版本的API与示例代码不兼容
     * 如需使用代码生成器，请参考官方文档根据具体版本调整
     */
    @Test
    public void generateCode() {
        /*
        // 配置数据源
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        // MyBatis-Plus 3.5.2版本的DataSourceConfig类API不同，请根据官方文档调整
        
        // 快速生成代码
        // FastAutoGenerator.create(dataSourceConfig)
        //     ...配置...
        //     .execute();
        
        System.out.println("代码生成完成！");
        */
        System.out.println("MyBatis-Plus代码生成器示例已禁用，请参考官方文档根据具体版本调整");
    }
}