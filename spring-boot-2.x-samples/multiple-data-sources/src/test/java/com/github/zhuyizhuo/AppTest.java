package com.github.zhuyizhuo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataSourceApplication.class)
public class AppTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("originJdbcTemplate")
    JdbcTemplate originJdbcTemplate;

    @Test
    public void a(){
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from biz_botpy");
        System.out.println(maps);

        List<Map<String, Object>> maps1 = originJdbcTemplate.queryForList("select * from biz_botpy");
        System.out.println(maps1);
    }
}
