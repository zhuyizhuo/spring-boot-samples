package com.github.zhuyizhuo.jackson.sample.controller;

import com.github.zhuyizhuo.jackson.sample.model.BaseResponse;
import com.github.zhuyizhuo.jackson.sample.model.Car;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局配置过滤 null 值及空串
 * Car 上的注解优先级高于全局配置，只过滤 null 不过滤空串
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    /**
     * 全局配置过滤 null 值及空串
     */
    @RequestMapping("/dataNull")
    public BaseResponse dataNull(){
        return BaseResponse.success();
    }

    /**
     * 全局配置过滤 null 值及空串
     */
    @RequestMapping("/dataEmpty")
    public BaseResponse dataEmpty(){
        return BaseResponse.success("");
    }

    /**
     * 全局配置过滤 null 值及空串
     * Car 上的注解优先级高于全局配置，只过滤 null 不过滤空串
     */
    @RequestMapping("/carNameNull")
    public BaseResponse<Car> carNameNull(){
        Car car = new Car();
        car.setId(3L);
        car.setName(null);
        return BaseResponse.success(car);
    }

    /**
     * 全局配置过滤 null 值及空串
     * Car 上的注解优先级高于全局配置，只过滤 null 不过滤空串
     */
    @RequestMapping("/carNameEmpty")
    public BaseResponse<Car> carNameEmpty(){
        Car car = new Car();
        car.setId(2L);
        car.setName("");
        return BaseResponse.success(car);
    }
}
