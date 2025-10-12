package com.github.zhuyizhuo.sample.oss.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * OSS文件服务接口
 */
public interface OssService {
    
    /**
     * 上传文件到OSS
     * @param file 待上传的文件
     * @return 文件在OSS上的URL
     */
    String uploadFile(MultipartFile file);
    
    /**
     * 通过文件流上传文件
     * @param inputStream 文件输入流
     * @param fileName 文件名
     * @return 文件在OSS上的URL
     */
    String uploadFile(InputStream inputStream, String fileName);
    
    /**
     * 从OSS下载文件
     * @param fileName OSS上的文件名
     * @return 文件输入流
     */
    InputStream downloadFile(String fileName);
    
    /**
     * 删除OSS上的文件
     * @param fileName OSS上的文件名
     * @return 是否删除成功
     */
    boolean deleteFile(String fileName);
    
    /**
     * 生成预签名URL（临时访问）
     * @param fileName OSS上的文件名
     * @param expireTime 过期时间（秒）
     * @return 预签名URL
     */
    String generatePresignedUrl(String fileName, int expireTime);
}