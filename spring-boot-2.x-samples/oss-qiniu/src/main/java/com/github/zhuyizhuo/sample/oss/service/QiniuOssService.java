package com.github.zhuyizhuo.sample.oss.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

/**
 * 七牛云OSS文件服务接口
 */
public interface QiniuOssService {

    /**
     * 上传文件到七牛云OSS
     * @param file 待上传的文件
     * @return 文件在七牛云上的URL
     */
    String uploadFile(MultipartFile file);

    /**
     * 通过文件流上传文件
     * @param inputStream 文件输入流
     * @param fileName 文件名
     * @param contentType 文件类型
     * @return 文件在七牛云上的URL
     */
    String uploadFile(InputStream inputStream, String fileName, String contentType);

    /**
     * 生成下载文件的URL
     * @param fileName 七牛云上的文件名
     * @param expireTime 过期时间（秒）
     * @return 带过期时间的下载URL
     */
    String generateDownloadUrl(String fileName, int expireTime);

    /**
     * 删除七牛云上的文件
     * @param fileName 七牛云上的文件名
     * @return 是否删除成功
     */
    boolean deleteFile(String fileName);

    /**
     * 检查存储空间是否存在
     * @param bucketName 空间名称
     * @return 是否存在
     */
    boolean bucketExists(String bucketName);

    /**
     * 获取空间内文件列表
     * @param bucketName 空间名称
     * @param prefix 前缀
     * @param limit 限制数量
     * @return 文件列表
     */
    Map<String, Object> listFiles(String bucketName, String prefix, int limit);
}