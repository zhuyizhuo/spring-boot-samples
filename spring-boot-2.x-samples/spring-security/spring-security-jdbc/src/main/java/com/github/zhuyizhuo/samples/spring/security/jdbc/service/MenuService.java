package com.github.zhuyizhuo.samples.spring.security.jdbc.service;

import com.github.zhuyizhuo.samples.spring.security.jdbc.dto.MenuDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<MenuDto> queryMenu() {
        return jdbcTemplate.query("select id,name,url,type,perms from menu", new BeanPropertyRowMapper<>(MenuDto.class));
    }
}