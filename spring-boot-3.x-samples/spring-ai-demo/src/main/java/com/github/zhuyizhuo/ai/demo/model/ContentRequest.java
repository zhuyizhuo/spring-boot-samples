package com.github.zhuyizhuo.ai.demo.model;

import lombok.Data;

/**
 * 内容处理请求模型
 */
@Data
public class ContentRequest {
    private String action; // summarize, extract-keywords, translate
    private String text; // 待处理的文本内容
    private String targetLanguage; // 目标语言（用于翻译）
    private Integer summaryLength; // 摘要长度（短/中/长）
    private Integer keywordCount; // 关键词数量
    private String format; // 输出格式
    
    public ContentRequest() {
        this.summaryLength = 2; // 默认中等长度摘要
        this.keywordCount = 10; // 默认提取10个关键词
        this.format = "text"; // 默认文本格式
    }
}