package com.github.zhuyizhuo.flyway.demo.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * 起名遵循 Flyway 的起名规则
 */
public class V1_2__Another_user extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true))
                .execute("INSERT INTO users (username, password, create_time) VALUES ('James', 'E10ADC3949BA59ABBE56E057F20F883E', now())");
    }

    /**
     * 默认校验和为 0  需重写 返回自己自定义计算的校验和 此处 demo 返回固定值12
     */
    @Override
    public Integer getChecksum() {
        return 12;
    }
}