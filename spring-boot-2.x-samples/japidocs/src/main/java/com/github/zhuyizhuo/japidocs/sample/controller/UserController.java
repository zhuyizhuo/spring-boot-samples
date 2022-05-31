package com.github.zhuyizhuo.japidocs.sample.controller;

import com.github.zhuyizhuo.japidocs.sample.base.BaseResponse;
import com.github.zhuyizhuo.japidocs.sample.vo.UserVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户 Controller
 * @author zhuo
 */
@RequestMapping("/api/user/")
@RestController
public class UserController {

    /**
     * 用户列表
     * @param user
     */
    @RequestMapping(path = "list", method = {RequestMethod.GET,  RequestMethod.POST}  )
    public BaseResponse<List<UserVO>> list(UserVO user){
        return null;
    }

    /**
     * 保存用户
     * @param user
     */
    @PostMapping(path = "save")
    public BaseResponse<UserVO> saveUser(@RequestBody UserVO user){
        return null;
    }

    /**
     * 删除用户
     * @param userId 用户ID
     */
    @PostMapping("delete")
    public BaseResponse deleteUser(@RequestParam Long userId){
        return null;
    }

}
