package com.example.quartzdemo.dto;

import lombok.Data;

@Data
public class JobRequest {
    
    /**
     * 任务名称
     */
    private String jobName;
    
    /**
     * 任务组名
     */
    private String jobGroup;
    
    /**
     * 触发器名称
     */
    private String triggerName;
    
    /**
     * 触发器组名
     */
    private String triggerGroup;
    
    /**
     * 任务类名称
     */
    private String jobClassName;
    
    /**
     * 定时表达式
     */
    private String cronExpression;
    
    /**
     * 任务描述
     */
    private String description;
}