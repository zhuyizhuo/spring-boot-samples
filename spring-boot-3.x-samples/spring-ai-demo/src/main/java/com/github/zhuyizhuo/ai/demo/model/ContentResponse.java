package com.github.zhuyizhuo.ai.demo.model;

import lombok.Data;
import java.util.List;

/**
 * 内容处理响应模型
 */
@Data
public class ContentResponse {
    private String action;
    private String result;
    private List<String> keywords;
    private boolean success;
    private String error;
}