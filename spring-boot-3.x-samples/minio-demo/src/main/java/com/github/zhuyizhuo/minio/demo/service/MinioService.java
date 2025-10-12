package com.github.zhuyizhuo.minio.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * MinIO服务接口，定义文件操作的方法
 */
public interface MinioService {

    /**
     * 上传文件到MinIO服务器
     * @param file 要上传的文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件名）
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String bucketName, String objectName);

    /**
     * 上传文件到默认存储桶
     * @param file 要上传的文件
     * @param objectName 对象名称（文件名）
     * @return 文件访问URL
     */
    String uploadFileToDefaultBucket(MultipartFile file, String objectName);

    /**
     * 从MinIO服务器下载文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件名）
     * @return 文件输入流
     */
    InputStream downloadFile(String bucketName, String objectName);

    /**
     * 从默认存储桶下载文件
     * @param objectName 对象名称（文件名）
     * @return 文件输入流
     */
    InputStream downloadFileFromDefaultBucket(String objectName);

    /**
     * 删除MinIO服务器上的文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件名）
     */
    void deleteFile(String bucketName, String objectName);

    /**
     * 删除默认存储桶中的文件
     * @param objectName 对象名称（文件名）
     */
    void deleteFileFromDefaultBucket(String objectName);

    /**
     * 列出存储桶中的所有文件
     * @param bucketName 存储桶名称
     * @return 文件列表
     */
    List<String> listFiles(String bucketName);

    /**
     * 列出默认存储桶中的所有文件
     * @return 文件列表
     */
    List<String> listFilesInDefaultBucket();

    /**
     * 获取文件信息
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件名）
     * @return 文件信息映射
     */
    Map<String, String> getFileInfo(String bucketName, String objectName);

    /**
     * 获取默认存储桶中的文件信息
     * @param objectName 对象名称（文件名）
     * @return 文件信息映射
     */
    Map<String, String> getFileInfoFromDefaultBucket(String objectName);

    /**
     * 创建存储桶
     * @param bucketName 存储桶名称
     */
    void createBucket(String bucketName);

    /**
     * 检查存储桶是否存在
     * @param bucketName 存储桶名称
     * @return 是否存在
     */
    boolean bucketExists(String bucketName);

    /**
     * 生成文件的分享链接
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件名）
     * @param expires 过期时间（秒）
     * @return 分享链接
     */
    String generatePresignedUrl(String bucketName, String objectName, int expires);

    /**
     * 生成默认存储桶中文件的分享链接
     * @param objectName 对象名称（文件名）
     * @param expires 过期时间（秒）
     * @return 分享链接
     */
    String generatePresignedUrlForDefaultBucket(String objectName, int expires);

    /**
     * 获取所有存储桶列表
     * @return 存储桶名称列表
     */
    List<String> listAllBuckets();
}