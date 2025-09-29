package com.github.zhuyizhuo.sample.oss.controller;

import com.github.zhuyizhuo.sample.oss.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * OSS文件上传控制器
 */
@RestController
@RequestMapping("/api/oss")
public class OssController {
    
    @Autowired
    private OssService ossService;
    
    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 调用OSS服务上传文件
            String fileUrl = ossService.uploadFile(file);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "文件上传成功");
            result.put("data", fileUrl);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "文件上传失败：" + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteFile(@RequestParam("fileName") String fileName) {
        try {
            boolean success = ossService.deleteFile(fileName);
            
            Map<String, Object> result = new HashMap<>();
            if (success) {
                result.put("code", 200);
                result.put("message", "文件删除成功");
                return ResponseEntity.ok(result);
            } else {
                result.put("code", 500);
                result.put("message", "文件删除失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "文件删除失败：" + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 生成预签名URL
     */
    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, Object>> generatePresignedUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam(defaultValue = "3600") int expireTime) {
        try {
            String url = ossService.generatePresignedUrl(fileName, expireTime);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "预签名URL生成成功");
            result.put("data", url);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "预签名URL生成失败：" + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}