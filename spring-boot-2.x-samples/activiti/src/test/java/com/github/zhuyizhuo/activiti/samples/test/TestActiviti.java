package com.github.zhuyizhuo.activiti.samples.test;

import com.github.zhuyizhuo.activiti.samples.controller.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestActiviti {

    @Autowired
    UserController userController;

    @Test
    public void getLearn(){
        System.out.println(userController);
    }
}
