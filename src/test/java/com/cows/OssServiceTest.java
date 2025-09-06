package com.cows;

import com.cows.service.OssService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class OssServiceTest {
    
    @Autowired
    private OssService ossService;
    
    @Test
    public void testListAllFiles() {
        try {
            System.out.println("开始测试OSS文件列举...");
            List<String> files = ossService.listAllFiles();
            System.out.println("成功列举文件数量: " + files.size());
            
            if (files.isEmpty()) {
                System.out.println("Bucket中没有文件或列举失败");
            } else {
                System.out.println("前10个文件:");
                files.stream().limit(10).forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Test
    public void testListFilesWithPrefix() {
        try {
            System.out.println("开始测试带前缀的OSS文件列举...");
            List<String> files = ossService.listFilesByPrefix("images/");
            System.out.println("前缀为'images/'的文件数量: " + files.size());
            
            if (!files.isEmpty()) {
                System.out.println("前5个文件:");
                files.stream().limit(5).forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Test
    public void testGeneratePreviewUrl() {
        try {
            System.out.println("开始测试生成预览URL...");
            
            // 首先列举一些文件
            List<String> files = ossService.listFilesByPrefix("images/");
            if (files.isEmpty()) {
                System.out.println("没有找到文件用于测试预览URL生成");
                return;
            }
            
            // 获取第一个文件并提取objectKey
            String fullUrl = files.get(0);
            String objectKey = fullUrl.substring(fullUrl.indexOf(".aliyuncs.com/") + ".aliyuncs.com/".length());
            System.out.println("使用文件: " + objectKey);
            
            // 测试生成预览URL
            String previewUrl = ossService.generatePreviewUrl(objectKey, 3600);
            System.out.println("生成的预览URL: " + previewUrl);
            System.out.println("URL将在1小时后过期");
            
            // 验证URL包含inline参数
            if (previewUrl.contains("response-content-disposition=inline")) {
                System.out.println("✓ 预览URL正确包含了inline参数");
            } else {
                System.out.println("✗ 预览URL可能不包含inline参数");
            }
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}