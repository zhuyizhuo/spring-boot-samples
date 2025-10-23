package com.github.zhuyizhuo.ai.demo.model;

import lombok.Data;

/**
 * 代码处理响应模型
 */
@Data
public class CodeResponse {
    private String action;
    private String language;
    private String result;
    private boolean success;
    private String error;
}