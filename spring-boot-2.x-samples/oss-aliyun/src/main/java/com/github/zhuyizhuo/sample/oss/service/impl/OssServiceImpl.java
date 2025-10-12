package com.github.zhuyizhuo.sample.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObject;
import com.github.zhuyizhuo.sample.oss.config.OssConfig;
import com.github.zhuyizhuo.sample.oss.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * OSS文件服务实现类
 */
@Service
public class OssServiceImpl implements OssService {
    
    @Autowired
    private OSS ossClient;
    
    @Autowired
    private OssConfig ossConfig;
    
    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        try {
            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = generateFileName(fileExtension);
            
            // 上传文件到OSS
            ossClient.putObject(ossConfig.getBucketName(), fileName, file.getInputStream());
            
            // 返回文件URL
            return ossConfig.getDomain() + fileName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    @Override
    public String uploadFile(InputStream inputStream, String fileName) {
        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("文件流或文件名不能为空");
        }
        
        try {
            // 生成唯一的文件名
            String fileExtension = getFileExtension(fileName);
            String newFileName = generateFileName(fileExtension);
            
            // 上传文件到OSS
            ossClient.putObject(ossConfig.getBucketName(), newFileName, inputStream);
            
            // 返回文件URL
            return ossConfig.getDomain() + newFileName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // 忽略关闭流的异常
            }
        }
    }
    
    @Override
    public InputStream downloadFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("文件名不能为空");
        }
        
        try {
            // 从OSS获取文件
            OSSObject ossObject = ossClient.getObject(ossConfig.getBucketName(), fileName);
            return ossObject.getObjectContent();
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }
    
    @Override
    public boolean deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("文件名不能为空");
        }
        
        try {
            // 从OSS删除文件
            ossClient.deleteObject(ossConfig.getBucketName(), fileName);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败", e);
        }
    }
    
    @Override
    public String generatePresignedUrl(String fileName, int expireTime) {
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("文件名不能为空");
        }
        
        try {
            // 设置URL过期时间
            Date expiration = new Date(System.currentTimeMillis() + expireTime * 1000L);
            
            // 生成预签名URL
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    ossConfig.getBucketName(), fileName);
            request.setExpiration(expiration);
            
            URL url = ossClient.generatePresignedUrl(request);
            return url.toString();
        } catch (Exception e) {
            throw new RuntimeException("生成预签名URL失败", e);
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
        return ossConfig.getDirPrefix() + UUID.randomUUID().toString().replace("-", "") + fileExtension;
    }
}