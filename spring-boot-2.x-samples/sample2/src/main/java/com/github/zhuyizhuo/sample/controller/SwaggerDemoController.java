package com.github.zhuyizhuo.sample.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 如果 controller 注解 @Api 没有标识 tags 属性,
 * 则该 controller 中方法注解 @ApiOperation 包含 tags 属性的方法不展示在该 controller 下
 * 如果 controller 注解 @Api 标识了 tags 属性,
 * 则该 controller 中方法注解 @ApiOperation 包含 tags 属性的方法展示在该 controller 下，
 * 并且相同 tags 的方法会在名为 tags 的分组下再次展示。
 */
@Api(value = " Swagger demo ")
@RestController
@RequestMapping("/swaggerDemo")
public class SwaggerDemoController {

    @ApiOperation(value = "index 演示接口",httpMethod = "GET",tags = "相同的 tags 会合并为一个列表")
    @GetMapping("/index")
    public Object index(){
        return "this is swagger simple demo.";
    }

}
