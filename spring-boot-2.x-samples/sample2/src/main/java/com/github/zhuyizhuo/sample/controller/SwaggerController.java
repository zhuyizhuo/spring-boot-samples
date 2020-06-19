package com.github.zhuyizhuo.sample.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = " Swagger ",tags = "Swagger Controller")
@RestController
@RequestMapping("/swagger")
public class SwaggerController {

    @ApiOperation(value = "演示接口", notes = "这是一个演示接口",httpMethod = "GET",tags = "相同的 tags 会合并为一个列表")
    @GetMapping("/demo")
    public Object demo(){
        return "this is swagger simple demo.";
    }

    @ApiOperation(value = "演示接口1",httpMethod = "GET")
    @GetMapping("/demo1")
    public Object demo1(){
        return "this is swagger simple demo1.";
    }
}
