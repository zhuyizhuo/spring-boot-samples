package com.github.zhuyizhuo.flyway.demo.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 起名遵循 Flyway 的起名规则
 */
@Component
public class V1_1__UpdateUserName extends BaseJavaMigration {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Integer getChecksum() {
        return 11;
    }

    @Override
    public void migrate(Context context) throws Exception {
        // 创建 JdbcTemplate ，方便 JDBC 操作
        JdbcTemplate template = new JdbcTemplate(context.getConfiguration().getDataSource());
        // 查询所有用户，如果用户名为 张三 ，则变更成 李四
        template.query("SELECT id, username, password, create_time FROM users", new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                // 遍历返回的结果
                do {
                    String username = rs.getString("username");
                    if ("张三".equals(username)) {
                        Integer id = rs.getInt("id");
                        template.update("UPDATE users SET username = ? WHERE id = ?",
                                "李四", id);
                        logger.info("[migrate][更新 user({}) 的用户名({} => {})", id, username, "李四");
                    }
                } while (rs.next());
            }
        });
    }
}