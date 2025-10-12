package com.github.zhuyizhuo.minio.demo.controller;

import com.github.zhuyizhuo.minio.demo.service.MinioService;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MinIO控制器，提供文件操作的RESTful API接口
 */
@RestController
@RequestMapping("/api/minio")
@Slf4j
public class MinioController {

    private final MinioService minioService;

    @Autowired
    public MinioController(MinioService minioService) {
        this.minioService = minioService;
    }

    /**
     * 上传文件到指定存储桶
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bucketName") String bucketName,
            @RequestParam(value = "objectName", required = false) String objectName) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 如果未指定objectName，则使用原文件名
            if (objectName == null || objectName.isEmpty()) {
                objectName = file.getOriginalFilename();
            }

            // 上传文件
            String fileUrl = minioService.uploadFile(file, bucketName, objectName);

            response.put("success", true);
            response.put("message", "文件上传成功");
            response.put("fileUrl", fileUrl);
            response.put("fileName", objectName);
            response.put("fileSize", file.getSize());
            response.put("contentType", file.getContentType());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 上传文件到默认存储桶
     */
    @PostMapping("/upload/default")
    public ResponseEntity<Map<String, Object>> uploadFileToDefaultBucket(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "objectName", required = false) String objectName) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 如果未指定objectName，则使用原文件名
            if (objectName == null || objectName.isEmpty()) {
                objectName = file.getOriginalFilename();
            }

            // 上传文件到默认存储桶
            String fileUrl = minioService.uploadFileToDefaultBucket(file, objectName);

            response.put("success", true);
            response.put("message", "文件上传成功");
            response.put("fileUrl", fileUrl);
            response.put("fileName", objectName);
            response.put("fileSize", file.getSize());
            response.put("contentType", file.getContentType());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 下载指定存储桶中的文件
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam("bucketName") String bucketName,
            @RequestParam("objectName") String objectName,
            @RequestParam(value = "thumbnail", required = false, defaultValue = "false") boolean thumbnail) {
        try {
            // 获取文件信息
            Map<String, String> fileInfo = minioService.getFileInfo(bucketName, objectName);
            
            // 下载文件
            try (InputStream inputStream = minioService.downloadFile(bucketName, objectName)) {
                byte[] fileBytes = inputStream.readAllBytes();
                
                // 设置响应头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(fileInfo.get("contentType")));
                
                // 如果是缩略图请求并且是图片文件，则不设置附件下载
                if (thumbnail && fileInfo.get("contentType").startsWith("image/")) {
                    headers.setContentDispositionFormData("inline", 
                            URLEncoder.encode(objectName, StandardCharsets.UTF_8.toString()));
                } else {
                    headers.setContentDispositionFormData("attachment", 
                            URLEncoder.encode(objectName, StandardCharsets.UTF_8.toString()));
                }
                headers.setContentLength(fileBytes.length);
                
                return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("文件下载失败: " + e.getMessage()).getBytes());
        }
    }

    /**
     * 下载默认存储桶中的文件
     */
    @GetMapping("/download/default")
    public ResponseEntity<byte[]> downloadFileFromDefaultBucket(
            @RequestParam("objectName") String objectName,
            @RequestParam(value = "thumbnail", required = false, defaultValue = "false") boolean thumbnail) {
        try {
            // 获取文件信息
            Map<String, String> fileInfo = minioService.getFileInfoFromDefaultBucket(objectName);
            
            // 下载文件
            try (InputStream inputStream = minioService.downloadFileFromDefaultBucket(objectName)) {
                byte[] fileBytes = inputStream.readAllBytes();
                
                // 设置响应头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(fileInfo.get("contentType")));
                
                // 如果是缩略图请求并且是图片文件，则不设置附件下载
                if (thumbnail && fileInfo.get("contentType").startsWith("image/")) {
                    headers.setContentDispositionFormData("inline", 
                            URLEncoder.encode(objectName, StandardCharsets.UTF_8.toString()));
                } else {
                    headers.setContentDispositionFormData("attachment", 
                            URLEncoder.encode(objectName, StandardCharsets.UTF_8.toString()));
                }
                headers.setContentLength(fileBytes.length);
                
                return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("文件下载失败: " + e.getMessage()).getBytes());
        }
    }

    /**
     * 删除指定存储桶中的文件
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteFile(
            @RequestParam("bucketName") String bucketName,
            @RequestParam("objectName") String objectName) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 删除文件
            minioService.deleteFile(bucketName, objectName);
            
            response.put("success", true);
            response.put("message", "文件删除成功");
            response.put("fileName", objectName);
            response.put("bucketName", bucketName);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "文件删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除默认存储桶中的文件
     */
    @DeleteMapping("/default")
    public ResponseEntity<Map<String, Object>> deleteFileFromDefaultBucket(
            @RequestParam("objectName") String objectName) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 删除文件
            minioService.deleteFileFromDefaultBucket(objectName);
            
            response.put("success", true);
            response.put("message", "文件删除成功");
            response.put("fileName", objectName);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "文件删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 列出指定存储桶中的所有文件
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listFiles(
            @RequestParam("bucketName") String bucketName) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 列出文件
            List<String> files = minioService.listFiles(bucketName);
            
            response.put("success", true);
            response.put("message", "文件列表获取成功");
            response.put("files", files);
            response.put("total", files.size());
            response.put("bucketName", bucketName);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("获取文件列表失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "获取文件列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 列出默认存储桶中的所有文件
     */
    @GetMapping("/list/default")
    public ResponseEntity<Map<String, Object>> listFilesInDefaultBucket() {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 列出文件
            List<String> files = minioService.listFilesInDefaultBucket();
            
            response.put("success", true);
            response.put("message", "文件列表获取成功");
            response.put("files", files);
            response.put("total", files.size());
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("获取文件列表失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "获取文件列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取指定文件的信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getFileInfo(
            @RequestParam("bucketName") String bucketName,
            @RequestParam("objectName") String objectName) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取文件信息
            Map<String, String> fileInfo = minioService.getFileInfo(bucketName, objectName);
            
            response.put("success", true);
            response.put("message", "文件信息获取成功");
            response.put("fileInfo", fileInfo);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "获取文件信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取默认存储桶中文件的信息
     */
    @GetMapping("/info/default")
    public ResponseEntity<Map<String, Object>> getFileInfoFromDefaultBucket(
            @RequestParam("objectName") String objectName) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取文件信息
            Map<String, String> fileInfo = minioService.getFileInfoFromDefaultBucket(objectName);
            
            response.put("success", true);
            response.put("message", "文件信息获取成功");
            response.put("fileInfo", fileInfo);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "获取文件信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建存储桶
     */
    @PostMapping("/bucket")
    public ResponseEntity<Map<String, Object>> createBucket(
            @RequestParam("bucketName") String bucketName) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 检查存储桶是否已存在
            if (minioService.bucketExists(bucketName)) {
                response.put("success", false);
                response.put("message", "存储桶已存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // 创建存储桶
            minioService.createBucket(bucketName);
            
            response.put("success", true);
            response.put("message", "存储桶创建成功");
            response.put("bucketName", bucketName);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("存储桶创建失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "存储桶创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 检查存储桶是否存在
     */
    @GetMapping("/bucket/exists")
    public ResponseEntity<Map<String, Object>> bucketExists(
            @RequestParam("bucketName") String bucketName) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 检查存储桶是否存在
            boolean exists = minioService.bucketExists(bucketName);
            
            response.put("success", true);
            response.put("message", exists ? "存储桶存在" : "存储桶不存在");
            response.put("exists", exists);
            response.put("bucketName", bucketName);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("检查存储桶是否存在失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "检查存储桶是否存在失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取所有存储桶列表
     */
    @GetMapping("/buckets/list")
    public ResponseEntity<Map<String, Object>> listAllBuckets() {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取所有存储桶列表
            List<String> buckets = minioService.listAllBuckets();
            
            response.put("success", true);
            response.put("message", "存储桶列表获取成功");
            response.put("buckets", buckets);
            response.put("total", buckets.size());
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("获取存储桶列表失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "获取存储桶列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 生成文件分享链接
     */
    @GetMapping("/share")
    public ResponseEntity<Map<String, Object>> generateShareLink(
            @RequestParam("bucketName") String bucketName,
            @RequestParam("objectName") String objectName,
            @RequestParam(value = "expires", defaultValue = "86400") int expires) { // 默认24小时
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 生成分享链接
            String shareUrl = minioService.generatePresignedUrl(bucketName, objectName, expires);
            
            response.put("success", true);
            response.put("message", "分享链接生成成功");
            response.put("shareUrl", shareUrl);
            response.put("expires", expires);
            response.put("fileName", objectName);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("分享链接生成失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "分享链接生成失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 生成默认存储桶中文件的分享链接
     */
    @GetMapping("/share/default")
    public ResponseEntity<Map<String, Object>> generateShareLinkForDefaultBucket(
            @RequestParam("objectName") String objectName,
            @RequestParam(value = "expires", defaultValue = "86400") int expires) { // 默认24小时
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 生成分享链接
            String shareUrl = minioService.generatePresignedUrlForDefaultBucket(objectName, expires);
            
            response.put("success", true);
            response.put("message", "分享链接生成成功");
            response.put("shareUrl", shareUrl);
            response.put("expires", expires);
            response.put("fileName", objectName);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("分享链接生成失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "分享链接生成失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}