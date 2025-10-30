package com.example.quartzdemo.config;

import com.example.quartzdemo.job.PrintTimeJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class QuartzConfig implements ApplicationRunner {

    @Autowired
    private Scheduler scheduler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 初始化一个默认的定时任务（每5秒执行一次）
        initDefaultJob();
    }

    /**
     * 初始化默认定时任务
     */
    private void initDefaultJob() throws SchedulerException {
        // 检查任务是否已存在
        JobKey jobKey = JobKey.jobKey("defaultPrintTimeJob", "defaultJobGroup");
        if (scheduler.checkExists(jobKey)) {
            log.info("默认定时任务已存在，跳过初始化");
            return;
        }

        // 创建JobDetail
        JobDetail jobDetail = JobBuilder.newJob(PrintTimeJob.class)
                .withIdentity("defaultPrintTimeJob", "defaultJobGroup")
                .withDescription("默认的打印时间定时任务")
                .storeDurably()
                .build();

        // 创建CronTrigger（每5秒执行一次）
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("defaultPrintTimeTrigger", "defaultTriggerGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                .build();

        // 调度任务
        scheduler.scheduleJob(jobDetail, trigger);
        log.info("初始化默认定时任务成功");
    }
}