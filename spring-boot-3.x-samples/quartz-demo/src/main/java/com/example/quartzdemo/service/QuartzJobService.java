package com.example.quartzdemo.service;

import org.quartz.SchedulerException;
import com.example.quartzdemo.dto.JobRequest;
import java.util.Map;

public interface QuartzJobService {

    /**
     * 创建定时任务
     */
    String createJob(JobRequest jobRequest) throws SchedulerException;

    /**
     * 更新定时任务
     */
    String updateJob(JobRequest jobRequest) throws SchedulerException;

    /**
     * 删除定时任务
     */
    String deleteJob(String jobName, String jobGroup) throws SchedulerException;

    /**
     * 暂停定时任务
     */
    String pauseJob(String jobName, String jobGroup) throws SchedulerException;

    /**
     * 恢复定时任务
     */
    String resumeJob(String jobName, String jobGroup) throws SchedulerException;

    /**
     * 立即执行一次定时任务
     */
    String triggerJob(String jobName, String jobGroup) throws SchedulerException;

    /**
     * 获取所有任务状态
     */
    Map<String, Object> getAllJobs() throws SchedulerException;
}