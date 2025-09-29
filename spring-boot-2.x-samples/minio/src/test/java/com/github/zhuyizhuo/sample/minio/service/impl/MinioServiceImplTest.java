package com.github.zhuyizhuo.sample.minio.service.impl;

import com.github.zhuyizhuo.sample.minio.config.MinioConfig;
import com.github.zhuyizhuo.sample.minio.config.TestConfig;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
public class MinioServiceImplTest {

    @MockBean
    private MinioClient minioClient;

    @MockBean
    private MinioConfig minioConfig;

    @InjectMocks
    private MinioServiceImpl minioService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 配置MinioConfig的模拟行为
        when(minioConfig.getBucketName()).thenReturn("test-bucket");
        when(minioConfig.getDomain()).thenReturn("http://localhost:9000/");
        when(minioConfig.getDirPrefix()).thenReturn("upload/");
    }

    @Test
    public void testUploadFile_Success() throws Exception {
        // 准备测试数据
        byte[] content = "Test file content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "test.txt", "test.txt", "text/plain", content);
        
        // 模拟minioClient的行为
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        doNothing().when(minioClient).putObject(any(PutObjectArgs.class));
        
        // 执行测试
        String result = minioService.uploadFile(file);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.startsWith("http://localhost:9000/test-bucket/upload/"));
        
        // 验证方法调用
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    public void testUploadFile_EmptyFile() {
        // 准备测试数据
        MockMultipartFile emptyFile = new MockMultipartFile(
                "empty.txt", "empty.txt", "text/plain", new byte[0]);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> minioService.uploadFile(emptyFile));
        assertEquals("文件不能为空", exception.getMessage());
    }

    @Test
    public void testUploadFile_NullFile() {
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> minioService.uploadFile(null));
        assertEquals("文件不能为空", exception.getMessage());
    }

    @Test
    public void testUploadFile_WithInputStream_Success() throws Exception {
        // 准备测试数据
        byte[] content = "Test content from InputStream".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        
        // 模拟minioClient的行为
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        doNothing().when(minioClient).putObject(any(PutObjectArgs.class));
        
        // 执行测试
        String result = minioService.uploadFile(inputStream, "test-file.txt", "text/plain");
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.startsWith("http://localhost:9000/test-bucket/upload/"));
        
        // 验证方法调用
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    public void testDownloadFile_Success() throws Exception {
        // 准备测试数据
        String fileName = "upload/test-file.txt";
        byte[] content = "Download test content".getBytes();
        InputStream mockInputStream = new ByteArrayInputStream(content);
        
        // 模拟GetObjectResponse对象
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        when(mockResponse.read(any(byte[].class), anyInt(), anyInt())).thenAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            int offset = invocation.getArgument(1);
            int length = invocation.getArgument(2);
            int bytesToRead = Math.min(length, content.length);
            System.arraycopy(content, 0, buffer, offset, bytesToRead);
            return bytesToRead;
        });
        doNothing().when(mockResponse).close();
        
        // 模拟minioClient的行为
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);
        
        // 执行测试
        InputStream result = minioService.downloadFile(fileName);
        
        // 验证结果
        assertNotNull(result);
        
        // 验证方法调用
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    public void testDeleteFile_Success() throws Exception {
        // 准备测试数据
        String fileName = "upload/test-file.txt";
        
        // 模拟minioClient的行为
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));
        
        // 执行测试
        boolean result = minioService.deleteFile(fileName);
        
        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    public void testGeneratePresignedUrl_Success() throws Exception {
        // 准备测试数据
        String fileName = "upload/test-file.txt";
        int expireTime = 3600;
        String expectedUrl = "http://localhost:9000/test-bucket/upload/test-file.txt?presigned-url";
        
        // 模拟minioClient的行为
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn(expectedUrl);
        
        // 执行测试
        String result = minioService.generatePresignedUrl(fileName, expireTime);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(expectedUrl, result);
        
        // 验证方法调用
        verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    public void testCreateBucket_Success() throws Exception {
        // 准备测试数据
        String bucketName = "new-test-bucket";
        
        // 模拟minioClient的行为
        doNothing().when(minioClient).makeBucket(any(MakeBucketArgs.class));
        
        // 执行测试
        boolean result = minioService.createBucket(bucketName);
        
        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    public void testBucketExists_Success() throws Exception {
        // 准备测试数据
        String bucketName = "existing-bucket";
        
        // 模拟minioClient的行为
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        
        // 执行测试
        boolean result = minioService.bucketExists(bucketName);
        
        // 验证结果
        assertTrue(result);
        
        // 验证方法调用
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
    }
}