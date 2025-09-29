package com.github.zhuyizhuo.sample.minio.service.impl;

import com.github.zhuyizhuo.sample.minio.config.MinioConfig;
import com.github.zhuyizhuo.sample.minio.service.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MinIO文件服务模拟实现类
 * 用于在无法连接到实际MinIO服务器时提供功能支持
 */
@Service("minioServiceMock")
public class MinioServiceMockImpl implements MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioServiceMockImpl.class);

    @Autowired
    private MinioConfig minioConfig;

    // 模拟文件存储
    private static final Map<String, byte[]> FILE_STORAGE = new ConcurrentHashMap<>();
    // 模拟桶存储
    private static final Set<String> BUCKETS = ConcurrentHashMap.newKeySet();

    static {
        // 初始化时创建默认桶
        BUCKETS.add("test");
        logger.info("MinIO服务模拟实现已初始化，当前模式为离线模拟模式");
    }

    @Override
    public String uploadFile(MultipartFile file) {
        logger.debug("[模拟模式] 开始上传文件，原始文件名: {}", file != null ? file.getOriginalFilename() : "null");

        if (file == null || file.isEmpty()) {
            logger.error("[模拟模式] 文件上传失败：文件不能为空");
            throw new RuntimeException("文件不能为空");
        }

        try {
            // 检查桶是否存在，不存在则创建
            if (!bucketExists(minioConfig.getBucketName())) {
                logger.info("[模拟模式] 桶不存在，开始创建桶: {}", minioConfig.getBucketName());
                createBucket(minioConfig.getBucketName());
            }

            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = generateFileName(fileExtension);

            // 保存文件到模拟存储
            byte[] fileBytes = file.getBytes();
            FILE_STORAGE.put(fileName, fileBytes);

            // 返回模拟的文件URL
            String fileUrl = minioConfig.getDomain() + minioConfig.getBucketName() + "/" + fileName;
            logger.info("[模拟模式] 文件上传成功，文件名: {}, 文件大小: {} bytes, 文件URL: {}", 
                fileName, fileBytes.length, fileUrl);
            return fileUrl;
        } catch (Exception e) {
            logger.error("[模拟模式] 文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String contentType) {
        logger.debug("[模拟模式] 开始通过文件流上传文件，原始文件名: {}", fileName);

        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            logger.error("[模拟模式] 文件流上传失败：文件流或文件名不能为空");
            throw new RuntimeException("文件流或文件名不能为空");
        }

        try {
            // 检查桶是否存在，不存在则创建
            if (!bucketExists(minioConfig.getBucketName())) {
                logger.info("[模拟模式] 桶不存在，开始创建桶: {}", minioConfig.getBucketName());
                createBucket(minioConfig.getBucketName());
            }

            // 生成唯一的文件名
            String fileExtension = getFileExtension(fileName);
            String newFileName = generateFileName(fileExtension);

            // 读取文件流内容
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] fileBytes = buffer.toByteArray();

            // 保存文件到模拟存储
            FILE_STORAGE.put(newFileName, fileBytes);

            // 生成模拟的文件URL
            String fileUrl = getDomain() + getBucketName() + "/" + newFileName;
            logger.info("[模拟模式] 文件流上传成功，文件名: {}, 文件大小: {} bytes, 文件URL: {}", 
                newFileName, fileBytes.length, fileUrl);
            return fileUrl;
        } catch (Exception e) {
            logger.error("[模拟模式] 文件流上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        } finally {
            try {
                inputStream.close();
                logger.debug("[模拟模式] 文件流已关闭");
            } catch (Exception e) {
                logger.warn("[模拟模式] 关闭文件流时发生异常: {}", e.getMessage(), e);
                // 忽略关闭流的异常
            }
        }
    }

    @Override
    public InputStream downloadFile(String fileName) {
        logger.debug("[模拟模式] 开始下载文件，文件名: {}", fileName);

        if (fileName == null || fileName.isEmpty()) {
            logger.error("[模拟模式] 文件下载失败：文件名不能为空");
            throw new RuntimeException("文件名不能为空");
        }

        try {
            // 从模拟存储中获取文件
            byte[] fileBytes = FILE_STORAGE.get(fileName);
            if (fileBytes == null) {
                logger.error("[模拟模式] 文件下载失败：文件不存在，文件名: {}", fileName);
                throw new RuntimeException("文件不存在");
            }

            logger.info("[模拟模式] 文件下载成功，文件名: {}, 文件大小: {} bytes", fileName, fileBytes.length);
            return new ByteArrayInputStream(fileBytes);
        } catch (Exception e) {
            logger.error("[模拟模式] 文件下载失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    public boolean deleteFile(String fileName) {
        logger.debug("[模拟模式] 开始删除文件，文件名: {}", fileName);

        if (fileName == null || fileName.isEmpty()) {
            logger.error("[模拟模式] 文件删除失败：文件名不能为空");
            throw new RuntimeException("文件名不能为空");
        }

        try {
            // 从模拟存储中删除文件
            byte[] removedFile = FILE_STORAGE.remove(fileName);
            boolean success = removedFile != null;

            if (success) {
                logger.info("[模拟模式] 文件删除成功，文件名: {}", fileName);
            } else {
                logger.warn("[模拟模式] 文件不存在，无法删除，文件名: {}", fileName);
            }

            return success;
        } catch (Exception e) {
            logger.error("[模拟模式] 文件删除失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public String generatePresignedUrl(String fileName, int expireTime) {
        logger.debug("[模拟模式] 开始生成预签名URL，文件名: {}, 过期时间: {}秒", fileName, expireTime);

        if (fileName == null || fileName.isEmpty()) {
            logger.error("[模拟模式] 生成预签名URL失败：文件名不能为空");
            throw new RuntimeException("文件名不能为空");
        }

        try {
            // 检查文件是否存在
            if (!FILE_STORAGE.containsKey(fileName)) {
                logger.error("[模拟模式] 生成预签名URL失败：文件不存在，文件名: {}", fileName);
                throw new RuntimeException("文件不存在");
            }

            // 生成模拟的预签名URL
            String presignedUrl = getDomain() + getBucketName() + "/" + fileName + 
                "?expires=" + (System.currentTimeMillis() + expireTime * 1000);

            logger.info("[模拟模式] 预签名URL生成成功，文件名: {}, 过期时间: {}秒", fileName, expireTime);
            return presignedUrl;
        } catch (Exception e) {
            logger.error("[模拟模式] 生成预签名URL失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }

    @Override
    public boolean createBucket(String bucketName) {
        logger.debug("[模拟模式] 开始创建桶，桶名: {}", bucketName);

        if (bucketName == null || bucketName.isEmpty()) {
            logger.error("[模拟模式] 创建桶失败：桶名称不能为空");
            throw new RuntimeException("桶名称不能为空");
        }

        try {
            // 在模拟存储中创建桶
            boolean success = BUCKETS.add(bucketName);

            if (success) {
                logger.info("[模拟模式] 桶创建成功，桶名: {}", bucketName);
            } else {
                logger.warn("[模拟模式] 桶已存在，桶名: {}", bucketName);
            }

            return success;
        } catch (Exception e) {
            logger.error("[模拟模式] 创建桶失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建桶失败", e);
        }
    }

    @Override
    public boolean bucketExists(String bucketName) {
        logger.debug("[模拟模式] 开始检查桶是否存在，桶名: {}", bucketName);

        if (bucketName == null || bucketName.isEmpty()) {
            logger.error("[模拟模式] 检查桶是否存在失败：桶名称不能为空");
            throw new RuntimeException("桶名称不能为空");
        }

        try {
            // 检查桶是否存在于模拟存储中
            boolean exists = BUCKETS.contains(bucketName);
            logger.debug("[模拟模式] 桶检查结果：桶名: {}, 是否存在: {}", bucketName, exists);
            return exists;
        } catch (Exception e) {
            logger.error("[模拟模式] 检查桶是否存在失败: {}", e.getMessage(), e);
            throw new RuntimeException("检查桶是否存在失败", e);
        }
    }

    @Override
    public Map<String, Object> listFiles(String bucketName, String prefix, boolean recursive) {
        logger.debug("[模拟模式] 开始获取文件列表，桶名: {}, 前缀: {}, 是否递归: {}", bucketName, prefix, recursive);

        if (bucketName == null || bucketName.isEmpty()) {
            bucketName = getBucketName();
        }

        if (prefix == null) {
            prefix = "";
        }

        try {
            // 检查桶是否存在
            if (!bucketExists(bucketName)) {
                logger.error("[模拟模式] 获取文件列表失败：桶不存在，桶名: {}", bucketName);
                throw new RuntimeException("桶不存在");
            }

            List<Map<String, Object>> fileList = new ArrayList<>();

            // 遍历模拟存储中的文件
            for (Map.Entry<String, byte[]> entry : FILE_STORAGE.entrySet()) {
                String fileName = entry.getKey();
                // 过滤前缀
                if (prefix.isEmpty() || fileName.startsWith(prefix)) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", fileName);
                    fileInfo.put("size", entry.getValue().length);
                    fileInfo.put("lastModified", new Date());
                    fileInfo.put("isDir", false);
                    fileList.add(fileInfo);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("files", fileList);
            result.put("total", fileList.size());

            logger.info("[模拟模式] 获取文件列表成功，桶名: {}, 文件数量: {}", bucketName, fileList.size());
            return result;
        } catch (Exception e) {
            logger.error("[模拟模式] 获取文件列表失败: {}", e.getMessage(), e);
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
        return getDirPrefix() + UUID.randomUUID().toString().replace("-", "") + fileExtension;
    }
    
    /**
     * 获取配置的域名
     */
    private String getDomain() {
        try {
            return minioConfig.getClass().getMethod("getDomain").invoke(minioConfig).toString();
        } catch (Exception e) {
            logger.warn("无法通过反射获取domain，使用默认值", e);
            return "http://localhost:9000/";
        }
    }
    
    /**
     * 获取配置的桶名
     */
    private String getBucketName() {
        try {
            return minioConfig.getClass().getMethod("getBucketName").invoke(minioConfig).toString();
        } catch (Exception e) {
            logger.warn("无法通过反射获取bucketName，使用默认值", e);
            return "test";
        }
    }
    
    /**
     * 获取配置的目录前缀
     */
    private String getDirPrefix() {
        try {
            return minioConfig.getClass().getMethod("getDirPrefix").invoke(minioConfig).toString();
        } catch (Exception e) {
            logger.warn("无法通过反射获取dirPrefix，使用默认值", e);
            return "upload/";
        }
    }
}