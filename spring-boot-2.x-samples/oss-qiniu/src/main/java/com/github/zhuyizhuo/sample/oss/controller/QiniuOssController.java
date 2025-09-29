package com.github.zhuyizhuo.sample.oss.controller;

import com.github.zhuyizhuo.sample.oss.service.QiniuOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 七牛云OSS文件操作控制器
 */
@RestController
@RequestMapping("/api/qiniu-oss")
public class QiniuOssController {

    @Autowired
    private QiniuOssService qiniuOssService;

    /**
     * 上传文件
     * @param file 待上传的文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = qiniuOssService.uploadFile(file);

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
     * 生成带过期时间的下载URL
     * @param fileName 文件名
     * @param expireTime 过期时间（秒），默认为3600秒
     * @return 下载URL
     */
    @GetMapping("/download-url")
    public ResponseEntity<Map<String, Object>> generateDownloadUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "expireTime", defaultValue = "3600") int expireTime) {
        try {
            String downloadUrl = qiniuOssService.generateDownloadUrl(fileName, expireTime);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "生成下载URL成功");
            result.put("data", downloadUrl);
            result.put("expireTime", expireTime);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "生成下载URL失败：" + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
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
            boolean success = qiniuOssService.deleteFile(fileName);

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
     * 获取文件列表
     * @param bucketName 空间名称，不传入则使用默认空间
     * @param prefix 前缀
     * @param limit 限制数量，默认为100
     * @return 文件列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listFiles(
            @RequestParam(value = "bucketName", required = false) String bucketName,
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        try {
            Map<String, Object> fileList = qiniuOssService.listFiles(bucketName, prefix, limit);

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
     * 检查空间是否存在
     * @param bucketName 空间名称
     * @return 检查结果
     */
    @GetMapping("/bucket/exists")
    public ResponseEntity<Map<String, Object>> bucketExists(@RequestParam("bucketName") String bucketName) {
        try {
            boolean exists = qiniuOssService.bucketExists(bucketName);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", exists ? "空间存在" : "空间不存在");
            result.put("data", exists);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "检查空间是否存在失败：" + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}