package com.github.zhuyizhuo.sample.minio.controller;

import com.github.zhuyizhuo.sample.minio.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * MinIO文件操作控制器
 */
@RestController
@RequestMapping("/api/minio")
public class MinioController {
    
    @Autowired
    private MinioService minioService;
    
    /**
     * 上传文件
     * @param file 待上传的文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = minioService.uploadFile(file);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "文件上传成功");
            result.put("data", fileUrl);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "文件上传失败：" + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 下载文件
     * @param fileName 文件名
     * @return 文件下载响应
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("fileName") String fileName) {
        try {
            // 获取文件流
            InputStream inputStream = minioService.downloadFile(fileName);
            
            // 使用Java 8兼容的方式读取文件内容
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] bytes = outputStream.toByteArray();
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(bytes.length);
            
            // 关闭流
            inputStream.close();
            outputStream.close();
            
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "文件下载失败：" + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * 删除文件
     * @param fileName 文件名
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteFile(@RequestParam("fileName") String fileName) {
        try {
            boolean success = minioService.deleteFile(fileName);
            
            Map<String, Object> result = new HashMap<>();
            if (success) {
                result.put("code", 200);
                result.put("message", "文件删除成功");
            } else {
                result.put("code", 400);
                result.put("message", "文件删除失败");
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "文件删除失败：" + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 生成预签名URL
     * @param fileName 文件名
     * @param expireTime 过期时间（秒），默认为3600秒
     * @return 预签名URL
     */
    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, Object>> generatePresignedUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "expireTime", defaultValue = "3600") int expireTime) {
        try {
            String presignedUrl = minioService.generatePresignedUrl(fileName, expireTime);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "生成预签名URL成功");
            result.put("data", presignedUrl);
            result.put("expireTime", expireTime);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "生成预签名URL失败：" + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 获取文件列表
     * @param bucketName 桶名称，不传入则使用默认桶
     * @param prefix 前缀
     * @param recursive 是否递归
     * @return 文件列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listFiles(
            @RequestParam(value = "bucketName", required = false) String bucketName,
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "recursive", defaultValue = "true") boolean recursive) {
        try {
            Map<String, Object> fileList = minioService.listFiles(bucketName, prefix, recursive);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "获取文件列表成功");
            result.put("data", fileList);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "获取文件列表失败：" + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 创建桶
     * @param bucketName 桶名称
     * @return 创建结果
     */
    @PostMapping("/bucket")
    public ResponseEntity<Map<String, Object>> createBucket(@RequestParam("bucketName") String bucketName) {
        try {
            boolean success = minioService.createBucket(bucketName);
            
            Map<String, Object> result = new HashMap<>();
            if (success) {
                result.put("code", 200);
                result.put("message", "桶创建成功");
            } else {
                result.put("code", 400);
                result.put("message", "桶创建失败");
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "桶创建失败：" + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 检查桶是否存在
     * @param bucketName 桶名称
     * @return 检查结果
     */
    @GetMapping("/bucket/exists")
    public ResponseEntity<Map<String, Object>> bucketExists(@RequestParam("bucketName") String bucketName) {
        try {
            boolean exists = minioService.bucketExists(bucketName);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", exists ? "桶存在" : "桶不存在");
            result.put("data", exists);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "检查桶是否存在失败：" + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}