package com.github.zhuyizhuo.activiti.samples.test;

import com.alibaba.fastjson.JSON;
import com.github.zhuyizhuo.activiti.samples.dto.Operator;
import com.github.zhuyizhuo.activiti.samples.service.ActivitiService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestActiviti {

    @Autowired
    ActivitiService activitiService;
    //  ProcessDefinition 流程定义,流程图以xml格式保存在.bpmn文件,由RepositoryService管理.
    @Resource
    RepositoryService repositoryService;

    /**
     * 请假流程单测
     */
    @Test
    public void normalApplyForLeave(){
        String processesKey = "askforleave";
        String userName = "lisi";
        //查询进行中的任务
        ArrayList<HashMap<String, Object>> queryTaskList = activitiService.queryTaskList(processesKey);
        System.out.println(queryTaskList);
        //新建流程
        HashMap<String, Object> askforleave = activitiService.startProcessByKey(processesKey, userName);
        System.out.println(JSON.toJSONString(askforleave));
        //根据流程实例ID查询 TASK
        ArrayList<HashMap<String, Object>> runTask = activitiService.queryRunTaskByProcessInstanceId((String) askforleave.get("processInstanceId"));
        String day = "2";
        for (int i = 0; i < runTask.size(); i++) {
            //提交申请
            activitiService.completeTaskById((String) runTask.get(i).get("id"), day);
        }

        //查询 leader 待审批任务
        ArrayList<HashMap<String, Object>> taskList = activitiService.queryTaskList(processesKey, "leader");
        System.out.println(taskList);

        for (int i = 0; i < taskList.size(); i++) {
            HashMap<String, Object> stringObjectHashMap = taskList.get(i);
            //审批通过
            activitiService.completeTaskById((String) stringObjectHashMap.get("id"), day);
        }

    }

    /**
     * 会签单测
     */
    @Test
    public void normalOrder() throws InterruptedException {
        String processesKey = "affair_order";
        String userName = "wangwu";
        //查询进行中的任务
        ArrayList<HashMap<String, Object>> queryTaskList = activitiService.queryTaskList(processesKey);
        System.out.println(queryTaskList);
        //新建流程
        HashMap<String, Object> askforleave = activitiService.startProcessByKey(processesKey, userName);
        System.out.println(JSON.toJSONString(askforleave));
        //根据流程实例ID查询 TASK
        ArrayList<HashMap<String, Object>> runTask = activitiService.queryRunTaskByProcessInstanceId((String) askforleave.get("processInstanceId"));
        for (int i = 0; i < runTask.size(); i++) {
            //提交申请
            Map<String, Object> params = new HashMap<>();
            params.put("userName", "chushen");
            activitiService.completeTaskById((String) runTask.get(i).get("id"), params);
        }

        //查询 shenhe 待审批任务
        ArrayList<HashMap<String, Object>> taskList = activitiService.queryTaskList(processesKey, "chushen");
        System.out.println(taskList);

        for (int i = 0; i < taskList.size(); i++) {
            HashMap<String, Object> stringObjectHashMap = taskList.get(i);
            //审批通过
            Map<String, Object> variables = new HashMap<>();
            List<Operator> list = new ArrayList();
            for (int j = 0; j < 3; j++) {
                list.add(new Operator(j+"",null,null));
            }
            variables.put("operator", list);
            activitiService.completeTaskById((String) stringObjectHashMap.get("id"), variables);
        }

        //根据流程实例ID查询 TASK
        ArrayList<HashMap<String, Object>> shenheTask = activitiService.queryRunTaskByProcessInstanceId((String) askforleave.get("processInstanceId"));
        //提交多单位确认
        Map<String, Object> variables = new HashMap<>();
        List<Operator> list = new ArrayList();
        variables.put("deptList", list);
        for (int i = 0; i < shenheTask.size(); i++) {
            for (int j = 0; j < 3; j++) {
                list.add(new Operator(i+""+j+"",null,null));
            }
            activitiService.completeTaskById((String) shenheTask.get(i).get("id"), variables);
        }

        //根据流程实例ID查询 TASK
        ArrayList<HashMap<String, Object>> querenTask = activitiService.queryRunTaskByProcessInstanceId((String) askforleave.get("processInstanceId"));
        for (int i = 0; i < querenTask.size(); i++) {
            //提交申请
            activitiService.completeTaskById((String) querenTask.get(i).get("id"), "");
        }
    }

    /**
     * 查询流程定义单测
     */
    @Test
    public void query(){
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("affair_order")
                .orderByProcessDefinitionVersion().asc().list();
        System.out.println(list);
    }

}
