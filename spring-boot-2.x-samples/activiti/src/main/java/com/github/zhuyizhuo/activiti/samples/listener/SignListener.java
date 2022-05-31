package com.github.zhuyizhuo.activiti.samples.listener;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 节点监听 需实现 TaskListener
 */
public class SignListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("会签监听");
        //获取流程id
        String exId = delegateTask.getExecutionId();
        String id = delegateTask.getId();
        String assignee = delegateTask.getAssignee();
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = engine.getRuntimeService();
        TaskService taskService = engine.getTaskService();

    }
}
