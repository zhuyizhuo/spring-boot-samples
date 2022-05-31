package com.github.zhuyizhuo.swagger.sample.controller;

import com.github.zhuyizhuo.swagger.sample.vo.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Demo 文档")
@RestController
@RequestMapping("/demo")
public class DemoController {

    @ApiOperation(value = "打招呼",notes = "这是接口 notes!",httpMethod = "GET")
    @ApiImplicitParam(dataType = "string",name = "name",value = "姓名", required = false)
    @GetMapping("/hello")
    public String hello(String name){
        return "Hello " + name + "!";
    }

    @ApiOperation(value = "添加用户",httpMethod = "POST")
    @PostMapping("/add")
    public String addUser(UserInfo user){
        return "Add User Success!" + user;
    }

    @ApiOperation(value = "测试 tags",httpMethod = "POST",tags = "相同的 tags 会合并为一个列表")
    @PostMapping("/tagDemo")
    public String tagDemo(UserInfo user){
        return "Tag Demo!" + user;
    }
}