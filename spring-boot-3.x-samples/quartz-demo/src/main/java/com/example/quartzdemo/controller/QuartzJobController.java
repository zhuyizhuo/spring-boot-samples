package com.example.quartzdemo.controller;

import com.example.quartzdemo.dto.JobRequest;
import com.example.quartzdemo.service.QuartzJobService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
@RequestMapping("/quartz")
public class QuartzJobController {

    @Autowired
    private QuartzJobService quartzJobService;

    /**
     * 任务管理页面
     */
    @GetMapping("/index")
    public String index(Model model) throws SchedulerException {
        Map<String, Object> jobs = quartzJobService.getAllJobs();
        model.addAttribute("jobs", jobs.get("jobs"));
        return "quartz/index";
    }

    /**
     * 获取所有任务
     */
    @GetMapping("/jobs")
    @ResponseBody
    public Map<String, Object> getAllJobs() throws SchedulerException {
        return quartzJobService.getAllJobs();
    }

    /**
     * 创建任务
     */
    @PostMapping("/job")
    @ResponseBody
    public Map<String, Object> createJob(@RequestBody JobRequest jobRequest) {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            String message = quartzJobService.createJob(jobRequest);
            result.put("success", true);
            result.put("message", message);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建任务失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 更新任务
     */
    @PutMapping("/job")
    @ResponseBody
    public Map<String, Object> updateJob(@RequestBody JobRequest jobRequest) {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            String message = quartzJobService.updateJob(jobRequest);
            result.put("success", true);
            result.put("message", message);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新任务失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/job/{jobName}/{jobGroup}")
    @ResponseBody
    public Map<String, Object> deleteJob(@PathVariable String jobName, @PathVariable String jobGroup) {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            String message = quartzJobService.deleteJob(jobName, jobGroup);
            result.put("success", true);
            result.put("message", message);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除任务失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 暂停任务
     */
    @PostMapping("/job/pause/{jobName}/{jobGroup}")
    @ResponseBody
    public Map<String, Object> pauseJob(@PathVariable String jobName, @PathVariable String jobGroup) {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            String message = quartzJobService.pauseJob(jobName, jobGroup);
            result.put("success", true);
            result.put("message", message);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "暂停任务失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 恢复任务
     */
    @PostMapping("/job/resume/{jobName}/{jobGroup}")
    @ResponseBody
    public Map<String, Object> resumeJob(@PathVariable String jobName, @PathVariable String jobGroup) {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            String message = quartzJobService.resumeJob(jobName, jobGroup);
            result.put("success", true);
            result.put("message", message);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "恢复任务失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 立即执行任务
     */
    @PostMapping("/job/trigger/{jobName}/{jobGroup}")
    @ResponseBody
    public Map<String, Object> triggerJob(@PathVariable String jobName, @PathVariable String jobGroup) {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            String message = quartzJobService.triggerJob(jobName, jobGroup);
            result.put("success", true);
            result.put("message", message);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "立即执行任务失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 任务创建表单页面
     */
    @GetMapping("/job/form")
    public String jobForm() {
        return "quartz/job-form";
    }
}