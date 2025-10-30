package com.example.quartzdemo.service.impl;

import com.example.quartzdemo.dto.JobRequest;
import com.example.quartzdemo.service.QuartzJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuartzJobServiceImpl implements QuartzJobService {

    @Autowired
    private Scheduler scheduler;

    @Override
    public String createJob(JobRequest jobRequest) throws SchedulerException {
        // 构建JobDetail
        JobDetail jobDetail = JobBuilder.newJob().ofType(getJobClass(jobRequest.getJobClassName()))
                .withIdentity(jobRequest.getJobName(), jobRequest.getJobGroup())
                .withDescription(jobRequest.getDescription())
                .storeDurably()
                .build();

        // 构建CronTrigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobRequest.getTriggerName(), jobRequest.getTriggerGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(jobRequest.getCronExpression()))
                .build();

        // 调度任务
        scheduler.scheduleJob(jobDetail, trigger);
        log.info("创建定时任务成功: {} - {}", jobRequest.getJobGroup(), jobRequest.getJobName());
        return "创建定时任务成功";
    }

    @Override
    public String updateJob(JobRequest jobRequest) throws SchedulerException {
        // 获取旧的触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(jobRequest.getTriggerName(), jobRequest.getTriggerGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        // 更新触发器的cron表达式
        trigger = trigger.getTriggerBuilder()
                .withSchedule(CronScheduleBuilder.cronSchedule(jobRequest.getCronExpression()))
                .build();

        // 重新调度任务
        scheduler.rescheduleJob(triggerKey, trigger);
        log.info("更新定时任务成功: {} - {}", jobRequest.getJobGroup(), jobRequest.getJobName());
        return "更新定时任务成功";
    }

    @Override
    public String deleteJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.deleteJob(jobKey);
        log.info("删除定时任务成功: {} - {}", jobGroup, jobName);
        return "删除定时任务成功";
    }

    @Override
    public String pauseJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.pauseJob(jobKey);
        log.info("暂停定时任务成功: {} - {}", jobGroup, jobName);
        return "暂停定时任务成功";
    }

    @Override
    public String resumeJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.resumeJob(jobKey);
        log.info("恢复定时任务成功: {} - {}", jobGroup, jobName);
        return "恢复定时任务成功";
    }

    @Override
    public String triggerJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.triggerJob(jobKey);
        log.info("立即执行定时任务: {} - {}", jobGroup, jobName);
        return "立即执行定时任务成功";
    }

    @Override
    public Map<String, Object> getAllJobs() throws SchedulerException {
        List<Map<String, Object>> jobList = new ArrayList<>();

        // 获取所有的任务组
        for (String groupName : scheduler.getJobGroupNames()) {
            // 获取组下所有的任务
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                
                for (Trigger trigger : triggers) {
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    String cronExpression = null;
                    
                    if (trigger instanceof CronTrigger) {
                        cronExpression = ((CronTrigger) trigger).getCronExpression();
                    }
                    
                    Map<String, Object> jobInfo = new HashMap<>();
                    jobInfo.put("jobName", jobKey.getName());
                    jobInfo.put("jobGroup", jobKey.getGroup());
                    jobInfo.put("triggerName", trigger.getKey().getName());
                    jobInfo.put("triggerGroup", trigger.getKey().getGroup());
                    jobInfo.put("jobClassName", jobDetail.getJobClass().getName());
                    jobInfo.put("description", jobDetail.getDescription());
                    jobInfo.put("cronExpression", cronExpression);
                    jobInfo.put("status", triggerState.name());
                    
                    jobList.add(jobInfo);
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("jobs", jobList);
        result.put("total", jobList.size());
        
        return result;
    }

    /**
     * 根据类名获取Job类
     */
    @SuppressWarnings("unchecked")
    private Class<? extends Job> getJobClass(String jobClassName) {
        try {
            return (Class<? extends Job>) Class.forName(jobClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到Job类: " + jobClassName, e);
        }
    }
}