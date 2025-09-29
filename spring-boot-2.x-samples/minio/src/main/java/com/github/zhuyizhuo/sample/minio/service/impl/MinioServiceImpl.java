package com.github.zhuyizhuo.sample.minio.service.impl;

import com.github.zhuyizhuo.sample.minio.config.MinioConfig;
import com.github.zhuyizhuo.sample.minio.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * MinIO文件服务实现类
 */
public class MinioServiceImpl implements MinioService {
    
    private static final Logger logger = LoggerFactory.getLogger(MinioServiceImpl.class);
    
    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private MinioConfig minioConfig;
    
    @Override
    public String uploadFile(MultipartFile file) {
        logger.debug("开始上传文件，原始文件名: {}", file != null ? file.getOriginalFilename() : "null");
        
        if (file == null || file.isEmpty()) {
            logger.error("文件上传失败：文件不能为空");
            throw new RuntimeException("文件不能为空");
        }
        
        try {
            // 检查桶是否存在，不存在则创建
            if (!bucketExists(minioConfig.getBucketName())) {
                logger.info("桶不存在，开始创建桶: {}", minioConfig.getBucketName());
                createBucket(minioConfig.getBucketName());
            }
            
            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = generateFileName(fileExtension);
            
            // 获取文件类型
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            logger.debug("准备上传文件到MinIO，桶名: {}, 文件名: {}, 文件大小: {} bytes", 
                minioConfig.getBucketName(), fileName, file.getSize());
            
            // 上传文件到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(contentType)
                            .build());
            
            // 返回文件URL
            String fileUrl = minioConfig.getDomain() + minioConfig.getBucketName() + "/" + fileName;
            logger.info("文件上传成功，文件URL: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    @Override
    public String uploadFile(InputStream inputStream, String fileName, String contentType) {
        logger.debug("开始通过文件流上传文件，原始文件名: {}", fileName);
        
        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            logger.error("文件流上传失败：文件流或文件名不能为空");
            throw new RuntimeException("文件流或文件名不能为空");
        }
        
        try {
            // 检查桶是否存在，不存在则创建
            if (!bucketExists(minioConfig.getBucketName())) {
                logger.info("桶不存在，开始创建桶: {}", minioConfig.getBucketName());
                createBucket(minioConfig.getBucketName());
            }
            
            // 生成唯一的文件名
            String fileExtension = getFileExtension(fileName);
            String newFileName = generateFileName(fileExtension);
            
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            logger.debug("准备通过文件流上传文件到MinIO，桶名: {}, 文件名: {}", 
                minioConfig.getBucketName(), newFileName);
            
            // 上传文件到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(newFileName)
                            .stream(inputStream, -1, 5 * 1024 * 1024) // 5MB 分块
                            .contentType(contentType)
                            .build());
            
            // 返回文件URL
            String fileUrl = minioConfig.getDomain() + minioConfig.getBucketName() + "/" + newFileName;
            logger.info("文件流上传成功，文件URL: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            logger.error("文件流上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        } finally {
            try {
                inputStream.close();
                logger.debug("文件流已关闭");
            } catch (Exception e) {
                logger.warn("关闭文件流时发生异常: {}", e.getMessage(), e);
                // 忽略关闭流的异常
            }
        }
    }
    
    @Override
    public InputStream downloadFile(String fileName) {
        logger.debug("开始下载文件，文件名: {}", fileName);
        
        if (fileName == null || fileName.isEmpty()) {
            logger.error("文件下载失败：文件名不能为空");
            throw new RuntimeException("文件名不能为空");
        }
        
        try {
            // 从MinIO获取文件
            logger.debug("准备从MinIO下载文件，桶名: {}, 文件名: {}", 
                minioConfig.getBucketName(), fileName);
            
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .build());
            
            logger.info("文件下载成功，文件名: {}", fileName);
            return inputStream;
        } catch (Exception e) {
            logger.error("文件下载失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件下载失败", e);
        }
    }
    
    @Override
    public boolean deleteFile(String fileName) {
        logger.debug("开始删除文件，文件名: {}", fileName);
        
        if (fileName == null || fileName.isEmpty()) {
            logger.error("文件删除失败：文件名不能为空");
            throw new RuntimeException("文件名不能为空");
        }
        
        try {
            // 从MinIO删除文件
            logger.debug("准备从MinIO删除文件，桶名: {}, 文件名: {}", 
                minioConfig.getBucketName(), fileName);
            
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .build());
            
            logger.info("文件删除成功，文件名: {}", fileName);
            return true;
        } catch (Exception e) {
            logger.error("文件删除失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件删除失败", e);
        }
    }
    
    @Override
    public String generatePresignedUrl(String fileName, int expireTime) {
        logger.debug("开始生成预签名URL，文件名: {}, 过期时间: {}秒", fileName, expireTime);
        
        if (fileName == null || fileName.isEmpty()) {
            logger.error("生成预签名URL失败：文件名不能为空");
            throw new RuntimeException("文件名不能为空");
        }
        
        try {
            // 生成预签名URL
            logger.debug("准备生成预签名URL，桶名: {}, 文件名: {}", 
                minioConfig.getBucketName(), fileName);
            
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .expiry(expireTime, TimeUnit.SECONDS)
                            .build());
            
            logger.info("预签名URL生成成功，文件名: {}, 过期时间: {}秒", fileName, expireTime);
            return presignedUrl;
        } catch (Exception e) {
            logger.error("生成预签名URL失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }
    
    @Override
    public boolean createBucket(String bucketName) {
        logger.debug("开始创建桶，桶名: {}", bucketName);
        
        if (bucketName == null || bucketName.isEmpty()) {
            logger.error("创建桶失败：桶名称不能为空");
            throw new RuntimeException("桶名称不能为空");
        }
        
        try {
            // 创建桶
            logger.debug("准备创建桶: {}", bucketName);
            
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());
            
            logger.info("桶创建成功，桶名: {}", bucketName);
            return true;
        } catch (Exception e) {
            logger.error("创建桶失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建桶失败", e);
        }
    }
    
    @Override
    public boolean bucketExists(String bucketName) {
        logger.debug("开始检查桶是否存在，桶名: {}", bucketName);
        
        if (bucketName == null || bucketName.isEmpty()) {
            logger.error("检查桶是否存在失败：桶名称不能为空");
            throw new RuntimeException("桶名称不能为空");
        }
        
        try {
            // 检查桶是否存在
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build());
            
            logger.debug("桶检查结果：桶名: {}, 是否存在: {}", bucketName, exists);
            return exists;
        } catch (Exception e) {
            logger.error("检查桶是否存在失败: {}", e.getMessage(), e);
            throw new RuntimeException("检查桶是否存在失败", e);
        }
    }
    
    @Override
    public Map<String, Object> listFiles(String bucketName, String prefix, boolean recursive) {
        logger.debug("开始获取文件列表，桶名: {}, 前缀: {}, 是否递归: {}", bucketName, prefix, recursive);
        
        if (bucketName == null || bucketName.isEmpty()) {
            bucketName = minioConfig.getBucketName();
        }
        
        if (prefix == null) {
            prefix = "";
        }
        
        try {
            List<Map<String, Object>> fileList = new ArrayList<>();
            
            // 列出桶内文件
            logger.debug("准备列出桶内文件，桶名: {}, 前缀: {}, 是否递归: {}", bucketName, prefix, recursive);
            
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(recursive)
                            .build());
            
            for (Result<Item> result : results) {
                Item item = result.get();
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("name", item.objectName());
                fileInfo.put("size", item.size());
                fileInfo.put("lastModified", item.lastModified());
                fileInfo.put("isDir", item.isDir());
                fileList.add(fileInfo);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("files", fileList);
            result.put("total", fileList.size());
            
            logger.info("获取文件列表成功，桶名: {}, 文件数量: {}", bucketName, fileList.size());
            return result;
        } catch (Exception e) {
            logger.error("获取文件列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取文件列表失败", e);
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }
    
    /**
     * 生成唯一的文件名
     */
    private String generateFileName(String fileExtension) {
        return minioConfig.getDirPrefix() + UUID.randomUUID().toString().replace("-", "") + fileExtension;
    }
}