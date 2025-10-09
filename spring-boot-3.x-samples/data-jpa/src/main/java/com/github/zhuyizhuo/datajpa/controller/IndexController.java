//package com.github.zhuyizhuo.datajpa.controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//public class IndexController {
//
//    @Value("${server.port}")
//    private String serverPort;
//
//    @Value("${server.servlet.context-path}")
//    private String contextPath;
//
//    @GetMapping("/")
//    public Map<String, Object> index() {
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Spring Boot Data JPA 项目测试页面");
//        response.put("application-name", "data-jpa");
//        response.put("version", "1.0.0");
//
//        // 构建访问地址信息
//        Map<String, String> urls = new HashMap<>();
//        String baseUrl = "http://localhost:" + serverPort + contextPath;
//        urls.put("首页", baseUrl + "/");
//        urls.put("用户API", baseUrl + "/api/users");
//        urls.put("Swagger UI", baseUrl + "/swagger-ui.html");
//        urls.put("API文档", baseUrl + "/api-docs");
//
//        response.put("available-urls", urls);
//
//        return response;
//    }
//}