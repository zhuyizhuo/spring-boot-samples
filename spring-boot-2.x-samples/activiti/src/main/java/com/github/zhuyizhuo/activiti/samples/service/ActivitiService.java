package com.github.zhuyizhuo.activiti.samples.service;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("userService")
public class ActivitiService {

    //  ProcessInstance/Execution 流程实例,每次申请按流程图走一遍,由RuntimeService管理.
    RuntimeService runtimeService;
    //  Task 任务,一次审批动作,由TaskService管理.
    TaskService taskService;
    //  ProcessDefinition 流程定义,流程图以xml格式保存在.bpmn文件,由RepositoryService管理.
    RepositoryService repositoryService;
    //  历史记录
    HistoryService historyService;
    ProcessEngine processEngine;

    public ActivitiService(RuntimeService runtimeService, TaskService taskService, RepositoryService repositoryService, HistoryService historyService, ProcessEngine processEngine) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.historyService = historyService;
        this.processEngine = processEngine;
    }

    public HashMap<String, Object> startProcessByKey(String processesKey) {
        // todo  真实业务需保证 key 存在
        HashMap<String, Object> resultMap = new HashMap<>(8);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processesKey);
        resultMap.put("id", processInstance.getId());
        resultMap.put("name", processInstance.getName());
        resultMap.put("deploymentId", processInstance.getDeploymentId());
        resultMap.put("processInstanceId", processInstance.getProcessInstanceId());
        resultMap.put("processDefinitionId", processInstance.getProcessDefinitionId());
        resultMap.put("processDefinitionName", processInstance.getProcessDefinitionName());
        return resultMap;
    }

    public HashMap<String, Object> startProcessByKey(String processesKey, String userName) {
        // todo  真实业务需保证 key 存在
        Map<String, Object> variables=new HashMap<>();
        variables.put("userName", userName);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processesKey, variables);

        HashMap<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("id", processInstance.getId());
        resultMap.put("name", processInstance.getName());
        resultMap.put("deploymentId", processInstance.getDeploymentId());
        resultMap.put("processInstanceId", processInstance.getProcessInstanceId());
        resultMap.put("processDefinitionId", processInstance.getProcessDefinitionId());
        resultMap.put("processDefinitionName", processInstance.getProcessDefinitionName());
        return resultMap;
    }

    public Map queryTask(String processInstanceId) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        Map m = new HashMap();
        m.put("processInstanceId", processInstanceId);
        m.put("processDefinitionName", pi.getProcessDefinitionName());
        m.put("deploymentId", pi.getDeploymentId());
        return m;
    }

    public ArrayList<HashMap<String, Object>> queryTaskList(String processDefinitionKey){
        return queryTaskList(processDefinitionKey, "");
    }

    public ArrayList<HashMap<String, Object>> queryTaskList(String processDefinitionKey, String userName) {
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey);
        if (StringUtils.hasText(userName)){
            //只查询该任务负责人的任务
            taskQuery.taskAssignee(userName);
        }
        List<Task> taskList = taskQuery.list();
        taskList.forEach(task -> {
            HashMap<String, Object> map = new HashMap<>(16);
            System.out.println(task);
            //任务ID
            map.put("id", task.getId());
            //任务名称
            map.put("name", task.getName());
            //任务委派人
            map.put("assignee", task.getAssignee());
            //任务创建时间
            map.put("createTime", task.getCreateTime());
            //任务描述
            map.put("description", task.getDescription());

            //任务对应得流程实例id
            map.put("processInstanceId", task.getProcessInstanceId());
            //任务对应得流程定义id
            map.put("processDefinitionId", task.getProcessDefinitionId());
            resultList.add(map);
        });
        return resultList;
    }

    public ArrayList<HashMap<String, Object>> queryRunTaskByProcessInstanceId(String processInstanceId) {
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();

        List<Task> taskList = taskService.createTaskQuery()
                .processInstanceId(processInstanceId).list();
        taskList.forEach(task -> {
            HashMap<String, Object> map = new HashMap<>(16);
            System.out.println(task);
            //任务ID
            map.put("id", task.getId());
            //任务名称
            map.put("name", task.getName());
            //任务委派人
            map.put("assignee", task.getAssignee());
            //任务创建时间
            map.put("createTime", task.getCreateTime());
            //任务描述
            map.put("description", task.getDescription());

            //任务对应得流程实例id
            map.put("processInstanceId", task.getProcessInstanceId());
            //任务对应得流程定义id
            map.put("processDefinitionId", task.getProcessDefinitionId());
            resultList.add(map);
        });
        return resultList;
    }

    public ArrayList<Object> getTaskHistoryList(String processDefinitionKey) {
        List<HistoricProcessInstance> taskList = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list();
        ArrayList<Object> resultList = new ArrayList<>();
        taskList.forEach(task -> {
            HashMap<String, Object> map = new HashMap<>(16);
            System.out.println(task);
            //任务ID
            map.put("id", task.getId());
            //任务对应得流程定义id
            map.put("processDefinitionId", task.getProcessDefinitionId());
            resultList.add(map);
        });
        return resultList;
    }

    public String completeTaskById(String taskId, String day) {
        //todo 将上一步的参数带到下一步
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.hasText(day)){
            map.put("day", day);
        } else {
            //不传默认为1天
            map.put("day", 1);
        }
        return completeTaskById(taskId, map);
    }

    public String completeTaskById(String taskId, Map<String, Object> map) {
        //应先校验任务是否存在
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null){
            return "task 不存在";
        }
        System.out.println("map:" + map);
        taskService.complete(taskId, map);
        return String.format("任务id为：%s 已经完成", taskId);
    }

    public String rejectTask(String taskId) {
        HistoricTaskInstance taskInstance = queryLastNodeByTaskId(taskId);
        if (taskInstance == null){
            return "task 不存在";
        }
        String backTaskDefinitionKey = taskInstance.getTaskDefinitionKey();
        //找到任务
        Task tasks = taskService.createTaskQuery().
                taskId(taskId).singleResult();
        String taskDefinitionKey = "";
        //实例 ID
        String processInstanceId = "";
        //当前流程未找到 则查找历史记录
        if (Objects.isNull(tasks)) {
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            taskDefinitionKey = historicTaskInstance.getTaskDefinitionKey();
            processInstanceId = historicTaskInstance.getProcessInstanceId();
        } else {
            taskDefinitionKey = tasks.getTaskDefinitionKey();
            processInstanceId = tasks.getProcessInstanceId();
        }
        System.out.println("processInstanceId: " + processInstanceId);
        //根据流程实例ID和任务key值查询所有同级任务集合
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(taskDefinitionKey).list();
        // 所有并行任务节点，同时驳回
        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            commitProcess(task, backTaskDefinitionKey, null);
        }
        //删除任务实例  流程图绘制以此为准
        deleteHistoryTask(taskId, taskInstance.getId(), processInstanceId);
        return String.format("任务id为：%s 审批拒绝", taskId);
    }

    /**
     * 生成流程图（高亮）
     * @param processInstanceId 实例 ID
     * @throws IOException
     */
    public byte[] getProcessImage(String processInstanceId) throws IOException {
        //  获取历史流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (historicProcessInstance == null) {
            throw new RuntimeException("实例不存在");
        } else {
            // 获取流程定义
            ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
                    .getProcessDefinition(historicProcessInstance.getProcessDefinitionId());

            // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstanceList = historyService
                    .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceId().desc().list();

            //历史任务 流程回退时删除任务实例 以任务实例为准
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
            List<String> collect = list.stream().map(HistoricTaskInstance::getTaskDefinitionKey).collect(Collectors.toList());
            //处理流程图节点
            collect.add("startevent1");
            collect.add("endevent1");
            collect.add("exclusivegateway1");

            // 已执行的节点ID集合
            List<String> executedActivityIdList = new ArrayList<>();
            System.out.println("获取已经执行的节点ID");
            for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                if (collect.contains(activityInstance.getActivityId())){
                    executedActivityIdList.add(activityInstance.getActivityId());
                }
            }
            // 获取流程图图像字符流
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

            //已执行flow的集合
            List<String> executedFlowIdList = getHighLightedFlows(bpmnModel, historicActivityInstanceList);

            ProcessDiagramGenerator processDiagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
            InputStream imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "png",
                    executedActivityIdList, executedFlowIdList, "黑体", "黑体", "黑体", null, 1.0);

            byte[] buffer = new byte[imageStream.available()];
            imageStream.read(buffer);
            imageStream.close();
            return buffer;
        }
    }

    /**
     * @param task     当前任务
     * @param variables  流程变量
     * @param activityId 流程转向执行任务节点ID<br> 此参数为空，默认为提交操作
     */
    private void commitProcess(Task task, String activityId, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }
        // 跳转节点为空，默认提交操作
        if (org.apache.commons.lang3.StringUtils.isBlank(activityId)) {
            taskService.complete(task.getId(), variables);
        } else {// 流程转向操作
            turnTransition(task, activityId, variables);
        }
    }

    /**
     * 流程转向操作
     *
     * @param task     当前任务
     * @param activityId 目标节点任务ID
     * @param variables  流程变量
     */
    private void turnTransition(Task task, String activityId,
                                Map<String, Object> variables) {
        try {
            String taskId = task.getId();
            // 当前节点
            ActivityImpl currActivity = findActivitiImpl(task, null);
            // 清空当前流向
            List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

            // 创建新流向
            TransitionImpl newTransition = currActivity.createOutgoingTransition();
            // 目标节点
            ActivityImpl pointActivity = findActivitiImpl(task, activityId);
            // 设置新流向的目标节点
            newTransition.setDestination(pointActivity);

            // 执行转向任务
            taskService.complete(taskId, variables);
            // 删除目标节点新流入
            pointActivity.getIncomingTransitions().remove(newTransition);

            // 还原以前流向
            restoreTransition(currActivity, oriPvmTransitionList);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("跳转至目标节点失败");
        }
    }

    /**
     * 清空指定活动节点流向
     *
     * @param activityImpl 活动节点
     * @return 节点流向集合
     */
    private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
        // 存储当前节点所有流向临时变量
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
        // 获取当前节点所有流向，存储到临时变量，然后清空
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            oriPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();

        return oriPvmTransitionList;
    }

    /**
     * 还原指定活动节点流向
     *
     * @param activityImpl         活动节点
     * @param oriPvmTransitionList 原有节点流向集合
     */
    private void restoreTransition(ActivityImpl activityImpl,
                                   List<PvmTransition> oriPvmTransitionList) {
        // 清空现有流向
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        pvmTransitionList.clear();
        // 还原以前流向
        for (PvmTransition pvmTransition : oriPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
    }
    /**
     * 根据任务ID和节点ID获取活动节点 <br>
     *
     * @param task     任务
     * @param activityId 活动节点ID <br> 如果为null或""，则默认查询当前活动节点 <br> 如果为"end"，则查询结束节点 <br>
     */
    private ActivityImpl findActivitiImpl(Task task, String activityId)
            throws Exception {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(task);
        // 获取当前活动节点ID
        if (org.apache.commons.lang3.StringUtils.isBlank(activityId)) {
            activityId = task.getTaskDefinitionKey();
        }

        // 根据节点ID，获取对应的活动节点
        return processDefinition.findActivity(activityId);
    }

    /**
     * 根据任务ID获取流程定义
     *
     * @param task 任务
     */
    private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
            Task task) throws Exception {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(task.getProcessDefinitionId());

        if (processDefinition == null) {
            throw new Exception("流程定义未找到!");
        }

        return processDefinition;
    }

    private void deleteHistoryTask(String currentTaskId, String targetTaskId, String procInstId) {
        try {
            // 查询
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(procInstId)
                    .orderByTaskId()
                    .asc()
                    .list();
            for (HistoricTaskInstance historicTaskInstance : list) {
                if (currentTaskId.equals(historicTaskInstance.getId())) {
                    historyService.deleteHistoricTaskInstance(historicTaskInstance.getId());
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("流程终止报错 taskId: " + currentTaskId + e.getMessage());
        }
    }


    /**
     * 根据任务id获取上一个节点的信息
     *
     * @param taskId
     */
    private HistoricTaskInstance queryLastNodeByTaskId(String taskId) {
        //上一个节点
        List<HistoricTaskInstance> list = historyService
                .createHistoricTaskInstanceQuery()
                .taskId(taskId).orderByHistoricTaskInstanceEndTime()
                .desc()
                .list();
        if(!list.isEmpty()){
            return queryLastNodeByProcessInstanceId(list.get(0).getProcessInstanceId());
        }
        return null;
    }

    /**
     * 根据流程实例id获取上一个节点的信息
     *
     * @param proInsId
     */
    private HistoricTaskInstance queryLastNodeByProcessInstanceId(String proInsId) {
        //上一个节点
        List<HistoricTaskInstance> list = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(proInsId)
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .list();
        for (int i = 0; i < list.size(); i++) {
            HistoricTaskInstance historicTaskInstance = list.get(i);
            if (historicTaskInstance.getEndTime() != null) {
                return historicTaskInstance;
            }
        }
        return null;
    }

    /**
     * 获取已经流转的线
     *
     * @param bpmnModel
     * @param historicActivityInstances
     * @return
     */
    private static List<String> getHighLightedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
        // 高亮流程已发生流转的线id集合
        List<String> highLightedFlowIds = new ArrayList<>();
        // 全部活动节点
        List<FlowNode> historicActivityNodes = new ArrayList<>();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstances = new ArrayList<>();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId());
//            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true);
            historicActivityNodes.add(flowNode);
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstances.add(historicActivityInstance);
            }
        }

        FlowNode currentFlowNode = null;
        FlowNode targetFlowNode = null;
        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId());
//            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlows = currentFlowNode.getOutgoingFlows();

            /**
             * 遍历outgoingFlows并找到已流转的 满足如下条件认为已流转：
             * 1.当前节点是并行网关或兼容网关， 则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
             * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
             */
            if ("parallelGateway".equals(currentActivityInstance.getActivityType()) || "inclusiveGateway".equals(currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef());
//                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(), true);
                    if (historicActivityNodes.contains(targetFlowNode)) {
                        highLightedFlowIds.add(targetFlowNode.getId());
                    }
                }
            } else {
                List<Map<String, Object>> tempMapList = new ArrayList<>();
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                        if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("highLightedFlowId", sequenceFlow.getId());
                            map.put("highLightedFlowStartTime", historicActivityInstance.getStartTime().getTime());
                            tempMapList.add(map);
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(tempMapList)) {
                    // 遍历匹配的集合，取得开始时间最早的一个
                    long earliestStamp = 0L;
                    String highLightedFlowId = null;
                    for (Map<String, Object> map : tempMapList) {
                        long highLightedFlowStartTime = Long.valueOf(map.get("highLightedFlowStartTime").toString());
                        if (earliestStamp == 0 || earliestStamp >= highLightedFlowStartTime) {
                            highLightedFlowId = map.get("highLightedFlowId").toString();
                            earliestStamp = highLightedFlowStartTime;
                        }
                    }
                    highLightedFlowIds.add(highLightedFlowId);
                }
            }
        }
        return highLightedFlowIds;
    }

    public void deleteTaskByProcessId(String processId) {
        runtimeService.deleteProcessInstance(processId, "结束");
    }

    public String suspendOrActivateProcessDefinition(String processDefinitionId) {
        // 获得流程定义
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        //是否暂停
        boolean suspend = processDefinition.isSuspended();
        if (suspend) {
            //如果暂停则激活，这里将流程定义下的所有流程实例全部激活
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
            System.out.println("流程定义：" + processDefinitionId + "激活");
            return String.format("流程定义：%s激活", processDefinitionId);

        } else {
            //如果激活则挂起，这里将流程定义下的所有流程实例全部挂起
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            System.out.println("流程定义：" + processDefinitionId + "挂起");
            return String.format("流程定义：%s挂起", processDefinitionId);
        }
    }
}
