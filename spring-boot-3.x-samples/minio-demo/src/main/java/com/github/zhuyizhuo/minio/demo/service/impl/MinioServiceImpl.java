package com.github.zhuyizhuo.minio.demo.service.impl;

import com.github.zhuyizhuo.minio.demo.config.MinioConfig;
import com.github.zhuyizhuo.minio.demo.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * MinIO服务实现类，实现文件操作的具体逻辑
 */
@Service
@Slf4j
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Autowired
    public MinioServiceImpl(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
        
        // 初始化时检查默认存储桶是否存在，如果不存在则创建
        try {
            if (!bucketExists(minioConfig.getBucketName())) {
                createBucket(minioConfig.getBucketName());
                log.info("创建默认存储桶: {}", minioConfig.getBucketName());
            }
        } catch (Exception e) {
            log.error("检查或创建默认存储桶失败: {}", e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String bucketName, String objectName) {
        try {
            // 检查存储桶是否存在
            if (!bucketExists(bucketName)) {
                createBucket(bucketName);
                log.info("创建存储桶: {}", bucketName);
            }

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("文件上传成功: {} 到存储桶: {}", objectName, bucketName);

            // 生成文件访问URL
            return generatePresignedUrl(bucketName, objectName, 60 * 60 * 24); // 24小时有效期
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public String uploadFileToDefaultBucket(MultipartFile file, String objectName) {
        return uploadFile(file, minioConfig.getBucketName(), objectName);
    }

    @Override
    public InputStream downloadFile(String bucketName, String objectName) {
        try {
            // 检查文件是否存在
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            // 下载文件
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage());
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    public InputStream downloadFileFromDefaultBucket(String objectName) {
        return downloadFile(minioConfig.getBucketName(), objectName);
    }

    @Override
    public void deleteFile(String bucketName, String objectName) {
        try {
            // 删除文件
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("文件删除成功: {} 从存储桶: {}", objectName, bucketName);
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage());
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public void deleteFileFromDefaultBucket(String objectName) {
        deleteFile(minioConfig.getBucketName(), objectName);
    }

    @Override
    public List<String> listFiles(String bucketName) {
        List<String> fileList = new ArrayList<>();
        try {
            // 列出存储桶中的所有文件
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .recursive(true)
                            .build()
            );

            // 遍历结果
            for (Result<Item> result : results) {
                Item item = result.get();
                fileList.add(item.objectName());
            }
        } catch (Exception e) {
            log.error("列出文件失败: {}", e.getMessage());
            throw new RuntimeException("列出文件失败", e);
        }
        return fileList;
    }

    @Override
    public List<String> listFilesInDefaultBucket() {
        return listFiles(minioConfig.getBucketName());
    }

    @Override
    public Map<String, String> getFileInfo(String bucketName, String objectName) {
        Map<String, String> fileInfo = new HashMap<>();
        try {
            // 获取文件信息
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            // 提取文件信息
            fileInfo.put("name", objectName);
            fileInfo.put("size", String.valueOf(stat.size()));
            fileInfo.put("contentType", stat.contentType());
            fileInfo.put("etag", stat.etag());
            fileInfo.put("lastModified", stat.lastModified().toString());
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", e.getMessage());
            throw new RuntimeException("获取文件信息失败", e);
        }
        return fileInfo;
    }

    @Override
    public Map<String, String> getFileInfoFromDefaultBucket(String objectName) {
        return getFileInfo(minioConfig.getBucketName(), objectName);
    }

    @Override
    public void createBucket(String bucketName) {
        try {
            // 创建存储桶
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } catch (Exception e) {
            log.error("创建存储桶失败: {}", e.getMessage());
            throw new RuntimeException("创建存储桶失败", e);
        }
    }

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            // 检查存储桶是否存在
            return minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } catch (Exception e) {
            log.error("检查存储桶是否存在失败: {}", e.getMessage());
            throw new RuntimeException("检查存储桶是否存在失败", e);
        }
    }

    @Override
    public String generatePresignedUrl(String bucketName, String objectName, int expires) {
        try {
            // 生成预签名URL
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expires, TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}", e.getMessage());
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }

    @Override
    public String generatePresignedUrlForDefaultBucket(String objectName, int expires) {
        return generatePresignedUrl(minioConfig.getBucketName(), objectName, expires);
    }

    @Override
    public List<String> listAllBuckets() {
        List<String> bucketList = new ArrayList<>();
        try {
            // 列出所有存储桶
            List<Bucket> buckets = minioClient.listBuckets();
            
            // 提取存储桶名称
            for (Bucket bucket : buckets) {
                bucketList.add(bucket.name());
            }
        } catch (Exception e) {
            log.error("获取存储桶列表失败: {}", e.getMessage());
            throw new RuntimeException("获取存储桶列表失败", e);
        }
        return bucketList;
    }
}