package com.github.zhuyizhuo.japidocs.sample;

import io.github.yedaxia.apidocs.Docs;
import io.github.yedaxia.apidocs.DocsConfig;

/**
 * @author zhuo
 */
public class JApiDocsApplication {

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir") + "/spring-boot-2.x-samples/spring-boot-samples-japidocs";
        DocsConfig config = new DocsConfig();
        // 项目根目录
        config.setProjectPath(projectPath);
        // 项目名称
        config.setProjectName("SpringBoot 集成 JApiDocs 生成");
        // 声明该API的版本
        config.setApiVersion("V1.0");
        // 生成API 文档所在目录
        config.setDocsPath(projectPath + "/docs");
        // 配置自动生成
        config.setAutoGenerate(Boolean.TRUE);
        // 执行生成文档
        Docs.buildHtmlDocs(config);
    }
}
