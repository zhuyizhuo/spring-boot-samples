package com.github.zhuyizhuo.jackson.sample.controller;

import com.github.zhuyizhuo.jackson.sample.constants.BaseConstant;
import com.github.zhuyizhuo.jackson.sample.model.BaseResponse;
import com.github.zhuyizhuo.jackson.sample.model.Car;
import com.github.zhuyizhuo.jackson.sample.model.UserInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回数据 id 自动 hash 加密,
 * 传入加密 id 自动解密
 * @author zhuo
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/list")
    public BaseResponse userList(){
        UserInfo zhangSan = new UserInfo(1L,"张三");
        zhangSan.setHeight(178L);
        UserInfo jack = new UserInfo(2L,"jack");
        UserInfo james = new UserInfo(3L,"james");
        jack.addCar(new Car(999L,"保时捷"));
        jack.addCar(new Car(888L,"奥迪"));
        List<UserInfo> users = new ArrayList<>();
        users.add(zhangSan);
        users.add(jack);
        users.add(james);
        return BaseResponse.success(users);
    }

    /**
     * 前端传入加密 id 字段名统一为 hashId
     * 后端接收参数名为 hashId 则接收未解码的 hashId.后端参数为 id 则接收解码的 id;
     * @param hashId UserController#userList() 接口返回的 id
     */
    @RequestMapping("/info")
    public BaseResponse queryUserInfo(@RequestParam(value = "hashId",required = true) String hashId,
                                      @RequestParam(value = "id") Long id){
        System.out.println("queryUserInfo hashId = " + hashId);
        System.out.println("queryUserInfo id = " + id);
        UserInfo info = new UserInfo();
        info.setId(id);
        info.setName("张三");
        info.setNickName("法外狂徒"+id);
        info.setEmail("zhangsan@gmail.com");
        return BaseResponse.success(info);
    }

    @RequestMapping("/query")
    public BaseResponse queryUser(@RequestBody UserInfo user){
        System.out.println("queryUser id = " + user.getId());
        if (user.getId() == null){
            return new BaseResponse(BaseConstant.ILLEGAL_ARGUMENT,"用户 id 为空!");
        }
        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setHeight(178L);
        info.setName("张三");
        info.setNickName("法外狂徒");
        info.setEmail("zhangsan@gmail.com");
        info.addCar(new Car(333L,"大众"));
        return BaseResponse.success(info);
    }
}
