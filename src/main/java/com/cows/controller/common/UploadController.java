package com.cows.controller.common;

import com.cows.commons.api.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * 一个简单的文件上传接口
 * 配置参数：限制上传大小、文件格式、存放位置、重命名上传文件
 * @author liyinchi
 * @date 2024/06/29
 */
@Slf4j
@RestController
@RequestMapping("/v1/fileUpload")
@Schema(name="文件上传", description="文件上传")
@Tag(name = "文件上传")
public class UploadController {
    private static final String UPLOAD_DIR = "./upload";
    // 存储文件名映射关系：storageName -> originalName
    private static final Map<String, String> FILE_NAME_MAPPING = new HashMap<>();

    @PostMapping("/upload")
    public BaseResponse<String> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        // 创建upload文件夹（如果不存在）
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new BaseResponse<>(1, "Failed to create upload directory", null);
        }

        String originalFilename = file.getOriginalFilename();
        // 对原始文件名进行安全处理，移除可能导致文件系统错误的字符
        String safeOriginalFilename = originalFilename.replaceAll("[^\\w.-]", "_");
        String fileExtension = safeOriginalFilename.substring(safeOriginalFilename.lastIndexOf(".") + 1);
        // 限制上传文件大小
        if (file.getSize() > 1024 * 1024 * 5) {
            return new BaseResponse<>(1, "File size too large", null);
        }
        // 限制上传文件格式（jpg、png、jpeg、bmp）
        if (!"jpg".equals(fileExtension) && !"png".equals(fileExtension) && !"jpeg".equals(fileExtension) && !"bmp".equals(fileExtension)) {
            return new BaseResponse<>(1, "Invalid file format", null);
        }

        // 双文件名策略：storageName用于文件系统存储，displayName用于前端展示
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String storageName = userId + "_" + timestamp + "_" + UUID.randomUUID() + "." + fileExtension; // 安全存储名
        String displayName = originalFilename; // 原始展示名

        // 保存文件映射关系
        FILE_NAME_MAPPING.put(storageName, displayName);
        log.info("File upload mapping - storageName: {}, displayName: {}", storageName, displayName);

        // Save the file to the server using storageName
        Path filePath = Paths.get(UPLOAD_DIR, storageName);
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            log.error("Failed to save file: {}", storageName, e);
            FILE_NAME_MAPPING.remove(storageName); // 清理映射关系
            return new BaseResponse<>(1, "Failed to save file", null);
        }

        return new BaseResponse<>(0, "File uploaded successfully", storageName);
    }

    /**
     * 获取文件的原始展示名称
     */
    @GetMapping("/getDisplayName")
    public BaseResponse<String> getDisplayName(@RequestParam("storageName") String storageName) {
        String displayName = FILE_NAME_MAPPING.get(storageName);
        if (displayName == null) {
            return new BaseResponse<>(1, "File not found", null);
        }
        return new BaseResponse<>(0, "Success", displayName);
    }
}
