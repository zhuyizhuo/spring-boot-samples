package com.github.zhuyizhuo.activiti.samples.listener;

import org.activiti.engine.EngineServices;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * 全局监听、连线监听 需实现 ExecutionListener
 */
public class StartListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        System.out.println("流程开始.");
        String currentActivityId = delegateExecution.getCurrentActivityId();
        String processInstanceId = delegateExecution.getProcessInstanceId();
        EngineServices engineServices = delegateExecution.getEngineServices();
        TaskService taskService = engineServices.getTaskService();
        HistoryService historyService = engineServices.getHistoryService();
    }
}
