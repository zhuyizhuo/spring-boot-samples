package com.github.zhuyizhuo;

import com.github.zhuyizhuo.service.DatasourceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataSourceApplication.class)
public class MultipleDataSourceTest {

    @Autowired
    private DatasourceService datasourceService;

    @Test
    public void testSerivce(){
        datasourceService.query();
    }
}
