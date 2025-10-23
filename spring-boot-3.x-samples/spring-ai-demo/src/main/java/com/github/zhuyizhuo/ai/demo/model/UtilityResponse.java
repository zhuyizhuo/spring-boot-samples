package com.github.zhuyizhuo.ai.demo.model;

import lombok.Data;

/**
 * 实用工具响应模型
 */
@Data
public class UtilityResponse {
    private String action;
    private String result;
    private boolean success;
    private String error;
}