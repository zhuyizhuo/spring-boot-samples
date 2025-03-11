package com.github.zhuyizhuo.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages ="com.github.zhuyizhuo.datasource.slave" ,sqlSessionFactoryRef = "slaveSqlSessionFactory")
public class SlaveDataSource {

    // mapper.xml所在地址
    private static final String MAPPER_LOCATION = "classpath*:mapper2/*.xml";


    @Bean(name = "slaveSqlSessionFactory")
    public SqlSessionFactory originSqlSessionFactory(@Qualifier("originDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        return sessionFactory.getObject();
    }

    @Bean
    public DataSourceTransactionManager originTransactionManager(@Qualifier("originDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
