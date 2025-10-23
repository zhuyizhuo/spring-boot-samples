package com.github.zhuyizhuo.esdemo.controller;

import com.github.zhuyizhuo.esdemo.config.ElasticsearchDemoProperties;
import com.github.zhuyizhuo.esdemo.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Thymeleaf页面控制器
 * 提供前端页面访问
 */
@Controller
public class PageController {

    @Autowired
    private EsService esService;
    
    @Autowired
    private ElasticsearchDemoProperties properties;

    /**
     * 首页 - Elasticsearch动态查询页面
     */
    @GetMapping("/index")
    public String index(Model model) {
        // 获取当前索引名称
        String currentIndex = esService.getCurrentIndex();
        model.addAttribute("currentIndex", currentIndex);
        model.addAttribute("pageTitle", "Spring Boot 3.x Elasticsearch 动态查询示例");
        return "index";
    }

    /**
     * 系统信息页面
     */
    @GetMapping("/system")
    public String system(Model model) {
        model.addAttribute("currentIndex", esService.getCurrentIndex());
        model.addAttribute("pageTitle", "系统管理");
        
        // 应用信息
        model.addAttribute("applicationName", "Elasticsearch Demo");
        model.addAttribute("applicationVersion", "1.0.0");
        model.addAttribute("springBootVersion", "3.x");
        
        // 获取系统启动时间
        model.addAttribute("startupTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(properties.getStartupTime())));
        
        // Elasticsearch配置信息
        model.addAttribute("esUris", properties.getUris());
        model.addAttribute("esConnectionTimeout", properties.getConnectionTimeout());
        model.addAttribute("esSocketTimeout", properties.getSocketTimeout());
        
        // Java环境信息
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("javaVendor", System.getProperty("java.vendor"));
        model.addAttribute("osName", System.getProperty("os.name"));
        model.addAttribute("osArch", System.getProperty("os.arch"));
        model.addAttribute("availableProcessors", Runtime.getRuntime().availableProcessors());
        
        return "system";
    }
}