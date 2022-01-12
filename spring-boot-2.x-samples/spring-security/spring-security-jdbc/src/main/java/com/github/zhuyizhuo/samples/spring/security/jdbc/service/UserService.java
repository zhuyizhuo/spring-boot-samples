package com.github.zhuyizhuo.samples.spring.security.jdbc.service;

import com.github.zhuyizhuo.samples.spring.security.jdbc.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhuo
 */
@Component
public class UserService implements UserDetailsService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<UserDto> selectUserRole(String username){
        return jdbcTemplate.query("select id,username,password,role_id as roleId from user where username = ?",new Object[]{username}, new BeanPropertyRowMapper<>(UserDto.class));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDto> userDtos = selectUserRole(username);
        if (userDtos.size() == 0){
            return null;
        }
        UserDto userDto = userDtos.get(0);
        return User.withUsername(userDto.getUsername()).password(userDto.getPassword()).authorities(userDto.getRoleId()).build();
    }
}