package com.cows;

import com.cows.config.OssConfig;
import com.cows.service.OssService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.List;
import java.util.Properties;

public class OssTestMain {
    
    public static void main(String[] args) {
        try {
            // 从环境变量读取配置，避免硬编码敏感信息
            String endpoint = System.getenv("OSS_ENDPOINT");
            String bucketName = System.getenv("OSS_BUCKET_NAME");
            String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
            String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");
            
            // 检查必要的环境变量
            if (endpoint == null || bucketName == null || accessKeyId == null || accessKeySecret == null) {
                System.err.println("错误：请设置以下环境变量：");
                System.err.println("OSS_ENDPOINT - 阿里云OSS endpoint");
                System.err.println("OSS_BUCKET_NAME - 阿里云OSS bucket名称");
                System.err.println("OSS_ACCESS_KEY_ID - 阿里云OSS access key ID");
                System.err.println("OSS_ACCESS_KEY_SECRET - 阿里云OSS access key secret");
                System.err.println("");
                System.err.println("示例：");
                System.err.println("export OSS_ENDPOINT=oss-cn-region.aliyuncs.com");
                System.err.println("export OSS_BUCKET_NAME=your-bucket");
                System.err.println("export OSS_ACCESS_KEY_ID=your-access-key");
                System.err.println("export OSS_ACCESS_KEY_SECRET=your-access-secret");
                return;
            }
            
            // 创建Spring上下文
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            
            // 设置环境属性
            StandardEnvironment environment = new StandardEnvironment();
            Properties props = new Properties();
            props.setProperty("aliyun.oss.endpoint", endpoint);
            props.setProperty("aliyun.oss.bucket-name", bucketName);
            props.setProperty("aliyun.oss.access-key-id", accessKeyId);
            props.setProperty("aliyun.oss.access-key-secret", accessKeySecret);
            
            environment.getPropertySources().addFirst(new PropertiesPropertySource("testProps", props));
            context.setEnvironment(environment);
            
            // 注册配置类
            context.register(OssConfig.class);
            context.register(OssService.class);
            context.refresh();
            
            // 获取OssService并测试
            OssService ossService = context.getBean(OssService.class);
            
            System.out.println("=== 测试OSS文件列举功能 ===");
            System.out.println("Bucket: " + bucketName);
            System.out.println("Endpoint: " + endpoint);
            
            // 测试列举所有文件
            System.out.println("\n1. 列举所有文件:");
            List<String> allFiles = ossService.listAllFiles();
            System.out.println("文件总数: " + allFiles.size());
            
            if (!allFiles.isEmpty()) {
                System.out.println("前10个文件:");
                allFiles.stream().limit(10).forEach(System.out::println);
            } else {
                System.out.println("Bucket中没有文件");
            }
            
            // 测试按前缀列举
            System.out.println("\n2. 测试按前缀列举文件 (前缀: images/):");
            List<String> prefixFiles = ossService.listFilesByPrefix("images/");
            System.out.println("前缀匹配文件数: " + prefixFiles.size());
            
            if (!prefixFiles.isEmpty()) {
                System.out.println("前5个文件:");
                prefixFiles.stream().limit(5).forEach(System.out::println);
            } else {
                System.out.println("没有匹配前缀的文件");
            }
            
            System.out.println("\n=== 测试完成 ===");
            
            // 测试预览URL生成功能
            System.out.println("\n3. 测试生成预览URL:");
            if (!allFiles.isEmpty()) {
                String firstFileUrl = allFiles.get(0);
                String objectKey = firstFileUrl.substring(firstFileUrl.indexOf(".aliyuncs.com/") + ".aliyuncs.com/".length());
                System.out.println("使用文件: " + objectKey);
                
                String previewUrl = ossService.generatePreviewUrl(objectKey, 3600);
                System.out.println("生成的预览URL: " + previewUrl);
                
                // 验证URL参数
                if (previewUrl.contains("response-content-disposition=inline")) {
                    System.out.println("✓ 预览URL正确包含了inline参数");
                } else {
                    System.out.println("✗ 警告：预览URL可能不包含inline参数");
                }
            } else {
                System.out.println("没有文件可用于测试预览URL生成");
            }
            
            context.close();
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}