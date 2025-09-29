package com.github.zhuyizhuo.sample.minio.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

/**
 * MinIO文件服务接口
 */
public interface MinioService {
    
    /**
     * 上传文件到MinIO
     * @param file 待上传的文件
     * @return 文件在MinIO上的URL
     */
    String uploadFile(MultipartFile file);
    
    /**
     * 通过文件流上传文件
     * @param inputStream 文件输入流
     * @param fileName 文件名
     * @param contentType 文件类型
     * @return 文件在MinIO上的URL
     */
    String uploadFile(InputStream inputStream, String fileName, String contentType);
    
    /**
     * 从MinIO下载文件
     * @param fileName MinIO上的文件名
     * @return 文件输入流
     */
    InputStream downloadFile(String fileName);
    
    /**
     * 删除MinIO上的文件
     * @param fileName MinIO上的文件名
     * @return 是否删除成功
     */
    boolean deleteFile(String fileName);
    
    /**
     * 生成预签名URL（临时访问）
     * @param fileName MinIO上的文件名
     * @param expireTime 过期时间（秒）
     * @return 预签名URL
     */
    String generatePresignedUrl(String fileName, int expireTime);
    
    /**
     * 创建存储桶
     * @param bucketName 桶名称
     * @return 是否创建成功
     */
    boolean createBucket(String bucketName);
    
    /**
     * 检查存储桶是否存在
     * @param bucketName 桶名称
     * @return 是否存在
     */
    boolean bucketExists(String bucketName);
    
    /**
     * 获取桶内文件列表
     * @param bucketName 桶名称
     * @param prefix 前缀
     * @param recursive 是否递归
     * @return 文件列表
     */
    Map<String, Object> listFiles(String bucketName, String prefix, boolean recursive);
}