package com.github.zhuyizhuo.activiti.samples.test;

import com.alibaba.fastjson.JSON;
import com.github.zhuyizhuo.activiti.samples.service.ActivitiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestActiviti {

    @Autowired
    ActivitiService activitiService;

    @Test
    public void applyForLeave(){
        HashMap<String, Object> askforleave = activitiService.startProcessByKey("askforleave");
        System.out.println(JSON.toJSONString(askforleave));

//        activitiService.completeTaskById()

    }
}
