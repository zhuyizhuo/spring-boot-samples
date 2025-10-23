package com.github.zhuyizhuo.ai.demo.model;

import lombok.Data;

/**
 * 代码处理请求模型
 */
@Data
public class CodeRequest {
    private String action; // generate, explain, review
    private String language; // java, python, javascript, etc.
    private String code; // 代码内容（用于解释或审查）
    private String prompt; // 提示词（用于生成代码）
    private String requirements; // 需求描述（用于代码生成或审查）
    private boolean includeComments; // 是否包含注释
    private String style; // 代码风格
    
    public CodeRequest() {
        this.includeComments = true;
        this.style = "standard";
    }
}