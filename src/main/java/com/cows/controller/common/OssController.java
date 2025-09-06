package com.cows.controller.common;

import com.cows.commons.api.BaseResponse;
import com.cows.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/osss")
@RequiredArgsConstructor
@Tag(name = "OSS管理", description = "阿里云OSS文件管理接口")
public class OssController {
    
    private final OssService ossService;
    
    @GetMapping("/getAllOsss")
    @Operation(summary = "获取所有OSS文件", description = "获取bucket中所有文件")
    public BaseResponse<List<String>> getAllOsss() {
        log.info("获取所有OSS文件");
        List<String> files = ossService.listAllFiles();
        return BaseResponse.success(files);
    }
    
    @GetMapping("/getOsssByPrefix")
    @Operation(summary = "按前缀获取OSS文件", description = "按指定前缀获取OSS文件")
    public BaseResponse<List<String>> getOsssByPrefix(
            @Parameter(description = "文件前缀", required = true)
            @RequestParam String prefix) {
        
        log.info("按前缀获取OSS文件: {}", prefix);
        List<String> files = ossService.listFilesByPrefix(prefix);
        return BaseResponse.success(files);
    }
    
    @GetMapping("/generatePreviewUrl")
    @Operation(summary = "生成预览URL", description = "生成带inline Content-Disposition的预览URL")
    public BaseResponse<String> generatePreviewUrl(
            @Parameter(description = "OSS文件key", required = true)
            @RequestParam String objectKey,
            @Parameter(description = "URL过期时间（秒），默认3600秒", example = "3600")
            @RequestParam(required = false, defaultValue = "3600") int expirationSeconds) {
        
        log.info("生成文件 {} 的预览URL，过期时间：{}秒", objectKey, expirationSeconds);
        String previewUrl = ossService.generatePreviewUrl(objectKey, expirationSeconds);
        return BaseResponse.success(previewUrl);
    }
    
    @GetMapping("/generateDefaultPreviewUrl")
    @Operation(summary = "生成默认预览URL", description = "生成1小时过期的预览URL")
    public BaseResponse<String> generateDefaultPreviewUrl(
            @Parameter(description = "OSS文件key", required = true)
            @RequestParam String objectKey) {
        
        log.info("生成文件 {} 的默认预览URL", objectKey);
        String previewUrl = ossService.generatePreviewUrl(objectKey);
        return BaseResponse.success(previewUrl);
    }
    
    @PostMapping("/batchGeneratePreviewUrls")
    @Operation(summary = "批量生成预览URL", description = "批量生成多个文件的预览URL")
    public BaseResponse<List<String>> batchGeneratePreviewUrls(
            @Parameter(description = "OSS文件key列表", required = true)
            @RequestBody List<String> objectKeys,
            @Parameter(description = "URL过期时间（秒），默认3600秒", example = "3600")
            @RequestParam(required = false, defaultValue = "3600") int expirationSeconds) {
        
        log.info("批量生成 {} 个文件的预览URL，过期时间：{}秒", objectKeys.size(), expirationSeconds);
        List<String> previewUrls = ossService.batchGeneratePreviewUrls(objectKeys, expirationSeconds);
        return BaseResponse.success(previewUrls);
    }
    
    @PostMapping("/setInlineContentDisposition")
    @Operation(summary = "设置文件内联预览", description = "设置文件的Content-Disposition为inline")
    public BaseResponse<String> setInlineContentDisposition(
            @Parameter(description = "OSS文件key", required = true)
            @RequestParam String objectKey) {
        
        log.info("设置文件 {} 的Content-Disposition为inline", objectKey);
        ossService.setInlineContentDisposition(objectKey);
        return BaseResponse.success("文件 " + objectKey + " 已成功设置为内联预览模式");
    }
    
    @GetMapping("/getOssInfoByKey")
    @Operation(summary = "获取OSS文件信息", description = "获取OSS文件的基本信息")
    public BaseResponse<Map<String, Object>> getOssInfoByKey(
            @Parameter(description = "OSS文件key", required = true)
            @RequestParam String objectKey) {
        
        log.info("获取文件 {} 的信息", objectKey);
        try {
            Map<String, Object> fileInfo = ossService.getFileInfo(objectKey);
            return BaseResponse.success(fileInfo);
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", e.getMessage());
            return (BaseResponse<Map<String, Object>>) BaseResponse.error(500, "获取文件信息失败: " + e.getMessage());
        }
    }
}