package com.github.zhuyizhuo.sample.oss.service.impl;

import com.github.zhuyizhuo.sample.oss.config.QiniuOssConfig;
import com.github.zhuyizhuo.sample.oss.service.QiniuOssService;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 七牛云OSS文件服务实现类
 */
@Service
public class QiniuOssServiceImpl implements QiniuOssService {

    private static final Logger logger = LoggerFactory.getLogger(QiniuOssServiceImpl.class);

    @Autowired
    private QiniuOssConfig qiniuOssConfig;

    @Autowired
    private Configuration configuration;

    @Autowired
    private Auth auth;

    @Override
    public String uploadFile(MultipartFile file) {
        logger.debug("开始上传文件，原始文件名: {}", file != null ? file.getOriginalFilename() : "null");

        if (file == null || file.isEmpty()) {
            logger.error("文件上传失败：文件不能为空");
            throw new RuntimeException("文件不能为空");
        }

        try {
            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = generateFileName(fileExtension);

            // 获取文件类型
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            logger.debug("准备上传文件到七牛云OSS，空间名: {}, 文件名: {}, 文件大小: {} bytes",
                    qiniuOssConfig.getBucketName(), fileName, file.getSize());

            // 创建上传管理器
            UploadManager uploadManager = new UploadManager(configuration);

            // 生成上传凭证
            String upToken = auth.uploadToken(qiniuOssConfig.getBucketName());

            // 上传文件
            Response response = uploadManager.put(file.getInputStream(), fileName, upToken, null, contentType);

            // 解析上传结果
            if (response.isOK()) {
                // 返回文件URL
                String fileUrl = qiniuOssConfig.getFileUrl(fileName);
                logger.info("文件上传成功，文件URL: {}", fileUrl);
                return fileUrl;
            } else {
                logger.error("文件上传失败，响应状态码: {}, 响应内容: {}", response.statusCode, response.bodyString());
                throw new RuntimeException("文件上传失败：" + response.bodyString());
            }
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
            // 生成唯一的文件名
            String fileExtension = getFileExtension(fileName);
            String newFileName = generateFileName(fileExtension);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            logger.debug("准备通过文件流上传文件到七牛云OSS，空间名: {}, 文件名: {}",
                    qiniuOssConfig.getBucketName(), newFileName);

            // 创建上传管理器
            UploadManager uploadManager = new UploadManager(configuration);

            // 生成上传凭证
            String upToken = auth.uploadToken(qiniuOssConfig.getBucketName());

            // 上传文件
            Response response = uploadManager.put(inputStream, newFileName, upToken, null, contentType);

            // 解析上传结果
            if (response.isOK()) {
                // 返回文件URL
                String fileUrl = qiniuOssConfig.getFileUrl(newFileName);
                logger.info("文件流上传成功，文件URL: {}", fileUrl);
                return fileUrl;
            } else {
                logger.error("文件流上传失败，响应状态码: {}, 响应内容: {}", response.statusCode, response.bodyString());
                throw new RuntimeException("文件上传失败：" + response.bodyString());
            }
        } catch (Exception e) {
            logger.error("文件流上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        } finally {
            try {
                inputStream.close();
                logger.debug("文件流已关闭");
            } catch (IOException e) {
                logger.warn("关闭文件流时发生异常: {}", e.getMessage(), e);
                // 忽略关闭流的异常
            }
        }
    }

    @Override
    public String generateDownloadUrl(String fileName, int expireTime) {
        logger.debug("开始生成带过期时间的下载URL，文件名: {}, 过期时间: {}秒", fileName, expireTime);

        if (fileName == null || fileName.isEmpty()) {
            logger.error("生成下载URL失败：文件名不能为空");
            throw new RuntimeException("文件名不能为空");
        }

        try {
            // 生成带过期时间的下载URL
            String downloadUrl = auth.privateDownloadUrl(qiniuOssConfig.getFileUrl(fileName), expireTime);

            logger.info("下载URL生成成功，文件名: {}, 过期时间: {}秒", fileName, expireTime);
            return downloadUrl;
        } catch (Exception e) {
            logger.error("生成下载URL失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成下载URL失败", e);
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
            // 创建Bucket管理器
            BucketManager bucketManager = new BucketManager(auth, configuration);

            // 删除文件
            Response response = bucketManager.delete(qiniuOssConfig.getBucketName(), fileName);

            if (response.isOK()) {
                logger.info("文件删除成功，文件名: {}", fileName);
                return true;
            } else {
                logger.error("文件删除失败，响应状态码: {}, 响应内容: {}", response.statusCode, response.bodyString());
                throw new RuntimeException("文件删除失败：" + response.bodyString());
            }
        } catch (Exception e) {
            logger.error("文件删除失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public boolean bucketExists(String bucketName) {
        logger.debug("开始检查空间是否存在，空间名: {}", bucketName);

        if (bucketName == null || bucketName.isEmpty()) {
            logger.error("检查空间是否存在失败：空间名称不能为空");
            throw new RuntimeException("空间名称不能为空");
        }

        try {
            // 创建Bucket管理器
            BucketManager bucketManager = new BucketManager(auth, configuration);

            // 尝试获取空间信息，如果不存在会抛出异常
            bucketManager.stat(bucketName, "test-key");
            logger.debug("空间检查结果：空间名: {}, 是否存在: {}", bucketName, true);
            return true;
        } catch (Exception e) {
            // 捕获特定的异常，判断是否为空间不存在的情况
            if (e.getMessage().contains("no such bucket")) {
                logger.debug("空间检查结果：空间名: {}, 是否存在: {}", bucketName, false);
                return false;
            } else {
                logger.error("检查空间是否存在失败: {}", e.getMessage(), e);
                throw new RuntimeException("检查空间是否存在失败", e);
            }
        }
    }

    @Override
    public Map<String, Object> listFiles(String bucketName, String prefix, int limit) {
        logger.debug("开始获取文件列表，空间名: {}, 前缀: {}, 限制数量: {}", bucketName, prefix, limit);

        if (bucketName == null || bucketName.isEmpty()) {
            bucketName = qiniuOssConfig.getBucketName();
        }

        if (prefix == null) {
            prefix = "";
        }

        if (limit <= 0) {
            limit = 100; // 默认获取100个文件
        }

        try {
            // 创建Bucket管理器
            BucketManager bucketManager = new BucketManager(auth, configuration);

            // 获取文件列表
            FileListing fileListing = bucketManager.listFiles(bucketName, prefix, null, limit, null);

            List<Map<String, Object>> fileList = new ArrayList<>();
            if (fileListing != null && fileListing.items != null) {
                for (FileInfo fileInfo : fileListing.items) {
                    Map<String, Object> fileData = new HashMap<>();
                    fileData.put("name", fileInfo.key);
                    fileData.put("size", fileInfo.fsize);
                    fileData.put("lastModified", new Date(fileInfo.putTime / 10000));
                    fileData.put("type", fileInfo.mimeType);
                    fileData.put("url", qiniuOssConfig.getFileUrl(fileInfo.key));
                    fileList.add(fileData);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("files", fileList);
            result.put("total", fileList.size());
            result.put("hasNext", fileListing != null && fileListing.marker != null);

            logger.info("获取文件列表成功，空间名: {}, 文件数量: {}", bucketName, fileList.size());
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
            return fileName.substring(fileName.lastIndexOf(""));
        }
        return "";
    }

    /**
     * 生成唯一的文件名
     */
    private String generateFileName(String fileExtension) {
        return UUID.randomUUID().toString().replace("-", "") + fileExtension;
    }
}