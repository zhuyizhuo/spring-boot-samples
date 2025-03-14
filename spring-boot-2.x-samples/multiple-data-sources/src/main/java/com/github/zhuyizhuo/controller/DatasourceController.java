package com.github.zhuyizhuo.controller;

import com.github.zhuyizhuo.service.DatasourceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class DatasourceController {


    private DatasourceService datasourceService;

    public DatasourceController(DatasourceService datasourceService) {
        this.datasourceService = datasourceService;
    }

    @RequestMapping("query")
    public String query(){
        datasourceService.query();
        return "{\"code\":\"00\",\"msg\":\"SUCCESS\"}";
    }
}
