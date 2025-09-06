package com.cows.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ResponseHeaderOverrides;
import java.net.URL;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OssService {
    
    private final OSS ossClient;
    
    public OssService(OSS ossClient) {
        this.ossClient = ossClient;
    }
    
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;
    
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    
    public List<String> listFiles(String prefix, int maxKeys) {
        List<String> fileList = new ArrayList<>();
        
        try {
            ObjectListing objectListing;
            if (prefix != null && !prefix.isEmpty()) {
                objectListing = ossClient.listObjects(bucketName, prefix);
            } else {
                objectListing = ossClient.listObjects(bucketName);
            }
            
            int count = 0;
            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                if (count >= maxKeys) {
                    break;
                }
                String fullUrl = "https://" + bucketName + "." + endpoint + "/" + objectSummary.getKey();
                fileList.add(fullUrl);
                count++;
            }
            
            log.info("成功列举 {} 个文件，前缀: {}", fileList.size(), prefix);
            
        } catch (Exception e) {
            log.error("列举OSS文件失败: {}", e.getMessage());
            throw new RuntimeException("列举OSS文件失败: " + e.getMessage());
        }
        
        return fileList;
    }
    
    public List<String> listAllFiles() {
        return listFiles(null, 1000);
    }
    
    public List<String> listFilesByPrefix(String prefix) {
        return listFiles(prefix, 1000);
    }
    
    /**
     * 设置文件的Content-Disposition为inline，实现浏览器预览
     * @param objectKey OSS文件key
     */
    public void setInlineContentDisposition(String objectKey) {
        try {
            // 获取原文件的元数据
            ObjectMetadata metadata = ossClient.getObjectMetadata(bucketName, objectKey);
            
            // 设置Content-Disposition为inline
            metadata.setContentDisposition("inline");
            
            // 根据文件扩展名设置正确的Content-Type
            String contentType = getContentType(objectKey);
            if (contentType != null) {
                metadata.setContentType(contentType);
            }
            
            // 使用拷贝操作更新元数据（OSS更新元数据的标准方式）
            CopyObjectRequest request = new CopyObjectRequest(bucketName, objectKey, bucketName, objectKey);
            request.setNewObjectMetadata(metadata);
            
            ossClient.copyObject(request);
            
            log.info("成功设置文件 {} 的Content-Disposition为inline", objectKey);
        } catch (Exception e) {
            log.error("设置文件Content-Disposition失败: {}", e.getMessage());
            throw new RuntimeException("设置文件Content-Disposition失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据文件扩展名获取对应的Content-Type
     * @param fileName 文件名
     * @return Content-Type
     */
    private String getContentType(String fileName) {
        if (fileName == null) return null;
        
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerName.endsWith(".png")) {
            return "image/png";
        } else if (lowerName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerName.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerName.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (lowerName.endsWith(".bmp")) {
            return "image/bmp";
        } else if (lowerName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerName.endsWith(".mp4")) {
            return "video/mp4";
        } else if (lowerName.endsWith(".mp3")) {
            return "audio/mpeg";
        }
        return null;
    }
    
    /**
     * 生成带预览参数的签名URL（推荐方案）da
     * @param objectKey OSS文件key
     * @param expirationSeconds URL过期时间（秒）
     * @return 带预览参数的签名URL
     */
    public String generatePreviewUrl(String objectKey, int expirationSeconds) {
        try {
            // 设置过期时间
            Date expiration = new Date(System.currentTimeMillis() + expirationSeconds * 1000);
            
            // 创建签名URL请求
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey);
            request.setExpiration(expiration);
            
            // 创建响应头覆盖对象，强制浏览器预览而不是下载
            ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();
            responseHeaders.setContentDisposition("inline");
            request.setResponseHeaders(responseHeaders);
            
            URL signedUrl = ossClient.generatePresignedUrl(request);
            
            log.info("成功生成文件 {} 的预览URL，过期时间：{}秒", objectKey, expirationSeconds);
            return signedUrl.toString();
            
        } catch (Exception e) {
            log.error("生成预览URL失败: {}", e.getMessage());
            throw new RuntimeException("生成预览URL失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成默认过期时间（1小时）的预览URL
     * @param objectKey OSS文件key
     * @return 预览URL
     */
    public String generatePreviewUrl(String objectKey) {
        return generatePreviewUrl(objectKey, 3600); // 默认1小时过期
    }
    
    /**
     * 批量生成预览URL
     * @param objectKeys 文件key列表
     * @param expirationSeconds URL过期时间（秒）
     * @return 预览URL列表
     */
    public List<String> batchGeneratePreviewUrls(List<String> objectKeys, int expirationSeconds) {
        List<String> previewUrls = new ArrayList<>();
        for (String objectKey : objectKeys) {
            try {
                String previewUrl = generatePreviewUrl(objectKey, expirationSeconds);
                previewUrls.add(previewUrl);
            } catch (Exception e) {
                log.error("生成文件 {} 的预览URL失败: {}", objectKey, e.getMessage());
                // 如果生成失败，返回原始URL作为备选
                previewUrls.add("https://" + bucketName + "." + endpoint + "/" + objectKey);
            }
        }
        return previewUrls;
    }
    
    /**
     * 获取OSS文件的基本信息
     * @param objectKey OSS文件key
     * @return 文件信息Map
     */
    public Map<String, Object> getFileInfo(String objectKey) {
        try {
            ObjectMetadata metadata = ossClient.getObjectMetadata(bucketName, objectKey);
            
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("key", objectKey);
            fileInfo.put("size", metadata.getContentLength());
            fileInfo.put("contentType", metadata.getContentType());
            fileInfo.put("lastModified", metadata.getLastModified());
            fileInfo.put("etag", metadata.getETag());
            fileInfo.put("contentDisposition", metadata.getContentDisposition());
            fileInfo.put("url", "https://" + bucketName + "." + endpoint + "/" + objectKey);
            
            log.info("成功获取文件 {} 的信息", objectKey);
            return fileInfo;
            
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", e.getMessage());
            throw new RuntimeException("获取文件信息失败: " + e.getMessage());
        }
    }
}