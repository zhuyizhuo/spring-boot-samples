package com.github.zhuyizhuo.flyway.demo.migration;

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.migration.JavaMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 自定义类名 需实现 JavaMigration
 */
public class UpdateUserName implements JavaMigration {
    private Logger logger = LoggerFactory.getLogger(getClass());

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
    /** 版本号为 1.1 */
    @Override
    public MigrationVersion getVersion() {
        return MigrationVersion.fromVersion("1.1");
    }
    /** 描述 */
    @Override
    public String getDescription() {
        return "update user name";
    }

    @Override
    public boolean isUndo() {
        return false;
    }
    /** 执行是否应在事务内部进行 */
    @Override
    public boolean canExecuteInTransaction() {
        return true;
    }

    /**
     * 默认校验和为 0  需重写 返回自己自定义计算的校验和 此处 demo 返回固定值11
     */
    @Override
    public Integer getChecksum() {
        return 11;
    }
}