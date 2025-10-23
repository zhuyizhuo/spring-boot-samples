package com.github.zhuyizhuo.ai.demo.model;

import lombok.Data;

/**
 * 实用工具请求模型
 */
@Data
public class UtilityRequest {
    private String action; // email, outline, learning-plan, etc.
    private String topic; // 主题
    private String audience; // 受众
    private String tone; // 语气风格
    private String details; // 详细要求
    private String format; // 输出格式
    private Integer sections; // 章节/部分数量
    
    public UtilityRequest() {
        this.tone = "professional";
        this.format = "text";
        this.sections = 3;
    }
}