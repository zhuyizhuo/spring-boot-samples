package com.github.zhuyizhuo.samples.spring.security.jdbc.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuo
 */
@RestController
@RequestMapping("/user")
public class UserController {

//    @Autowired
//    JdbcTemplate jdbcTemplate;

    @RequestMapping("/info")
    public String info(){
        //当前认证通过的用户身份
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

//        List<Map> query = jdbcTemplate.query("", new Object[]{}, new BeanPropertyRowMapper<>(Map.class));

        return "hello world";
    }
}
