package com.github.zhuyizhuo.activiti.samples.controller;

import com.github.zhuyizhuo.activiti.samples.service.ActivitiService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

@RestController
@RequestMapping
public class ActivitiController {

    ActivitiService activitiService;

    public ActivitiController(ActivitiService activitiService) {
        this.activitiService = activitiService;
    }

    /**
     * 启动请假流程--传入请假流程的key
     */
    @GetMapping("start")
    public ResponseEntity startProcessByKey(@RequestParam String processesKey) {
        return ResponseEntity.ok(activitiService.startProcessByKey(processesKey));
    }

    /**
     * 根据流程实例ID 查询流程
     */
    @GetMapping("queryByTaskId")
    public ResponseEntity queryTask(@RequestParam String processInstanceId){
        return ResponseEntity.ok(activitiService.queryTask(processInstanceId));
    }

    /**
     * 根据流程key和用户名获取待办流程
     * @param processDefinitionKey 流程key(holiday)
     * @param userName             用户名(zhangsan)
     */
    @GetMapping("task")
    public ResponseEntity getTaskList(@RequestParam String processDefinitionKey, @RequestParam(required = false) String userName) {
        return ResponseEntity.ok(activitiService.getTaskList(processDefinitionKey, userName));
    }

    /**
     * 查询历史记录
     */
    @GetMapping("task/history")
    public ResponseEntity getTaskHistoryList(@RequestParam String processDefinitionKey) {
        return ResponseEntity.ok(activitiService.getTaskHistoryList(processDefinitionKey));
    }

    /**
     * 根据任务id完成任务
     *
     * @param taskId 任务id
     */
    @GetMapping("completeTask")
    public ResponseEntity completeTaskById(@RequestParam String taskId, @RequestParam(required = false) String day) {
        return ResponseEntity.ok(activitiService.completeTaskById(taskId, day));
    }

    /**
     * 根据任务id驳回任务
     *
     * @param taskId 任务id
     */
    @GetMapping("rejectTask")
    public ResponseEntity rejectTask(@RequestParam String taskId) {
        return ResponseEntity.ok(activitiService.rejectTask(taskId));
    }

    /**
     * 生成流程图（高亮）
     */
    @RequestMapping("viewProcessImgHighLighted")
    public void viewProcessImgHighLighted(@RequestParam String processInstanceId, HttpServletResponse response) {
        try {
            byte[] processImage = activitiService.getProcessImage(processInstanceId);
            OutputStream outputStream = response.getOutputStream();
            InputStream in = new ByteArrayInputStream(processImage);
            IOUtils.copy(in, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Writer writer = response.getWriter();
                writer.write("{\"code\":999,\"errorMsg\": \"processInstanceId "+ processInstanceId +" not found!\"}");
                writer.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 删除任务
     * @param processId
     */
    @RequestMapping("deleteTaskByProcessId")
    public ResponseEntity deleteTaskByProcessId(@RequestParam String processId){
        activitiService.deleteTaskByProcessId(processId);
        return ResponseEntity.ok("成功");
    }

    /**
     * 挂起激活流程定义
     *
     * @param processDefinitionId 流程定义Id
     * @return
     * @throws
     */
    @GetMapping("suspendOrActivateProcessDefinition")
    public ResponseEntity suspendOrActivateProcessDefinition(@RequestParam String processDefinitionId) {
        return ResponseEntity.ok(activitiService.suspendOrActivateProcessDefinition(processDefinitionId));
    }

//    /**
//     * 生成流程图
//     */
//    @RequestMapping("createProcessImg")
//    public void createProcessImg(@RequestParam String processInstanceId, HttpServletResponse response) throws Exception {
//        //获取历史流程实例
//        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
//        //根据流程定义获取输入流
//        InputStream is = repositoryService.getProcessDiagram(processInstance.getProcessDefinitionId());
//        BufferedImage bi = ImageIO.read(is);
//        File file = new File(processInstanceId + "Img.png");
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        FileOutputStream fos = new FileOutputStream(file);
//        ImageIO.write(bi, "png", fos);
//        fos.close();
//        is.close();
//        System.out.println("图片生成成功");
//        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("userId").list();
//        for (Task t : tasks) {
//            System.out.println(t.getName());
//        }
//    }
//
//    /**
//     * 生成流程图
//     */
//    @RequestMapping("viewProcessImg")
//    public void viewProcessImg(@RequestParam String processInstanceId, HttpServletResponse response) throws Exception {
//        //获取历史流程实例
//        try {
//            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
//            OutputStream outputStream = response.getOutputStream();
//            //根据流程定义获取输入流
//            InputStream in = repositoryService.getProcessDiagram(processInstance.getProcessDefinitionId());
//            IOUtils.copy(in, outputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
