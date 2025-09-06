/**
 * OSS API JavaScript调用示例
 * 这些函数展示了如何在前端JavaScript中调用OSS相关的REST API
 */

// 基础配置
const API_BASE_URL = 'http://localhost:8088/api/oss';

/**
 * 列举OSS文件
 * @param {string} prefix - 文件前缀（可选）
 * @returns {Promise<string[]>} - 文件URL列表
 */
async function listFiles(prefix = '') {
    try {
        const params = new URLSearchParams();
        if (prefix) {
            params.append('prefix', prefix);
        }
        
        const response = await fetch(`${API_BASE_URL}/list?${params}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const fileList = await response.json();
        console.log('文件列表:', fileList);
        return fileList;
    } catch (error) {
        console.error('列举文件失败:', error);
        throw error;
    }
}

/**
 * 列举所有OSS文件
 * @returns {Promise<string[]>} - 文件URL列表
 */
async function listAllFiles() {
    try {
        const response = await fetch(`${API_BASE_URL}/list/all`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const fileList = await response.json();
        console.log('所有文件列表:', fileList);
        return fileList;
    } catch (error) {
        console.error('列举所有文件失败:', error);
        throw error;
    }
}

/**
 * 生成预览URL
 * @param {string} objectKey - OSS文件key
 * @param {number} expirationSeconds - 过期时间（秒，可选，默认3600）
 * @returns {Promise<string>} - 预览URL
 */
async function generatePreviewUrl(objectKey, expirationSeconds = 3600) {
    try {
        const params = new URLSearchParams();
        params.append('objectKey', objectKey);
        if (expirationSeconds !== 3600) {
            params.append('expirationSeconds', expirationSeconds);
        }
        
        const response = await fetch(`${API_BASE_URL}/preview-url?${params}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const previewUrl = await response.text();
        console.log('预览URL:', previewUrl);
        return previewUrl;
    } catch (error) {
        console.error('生成预览URL失败:', error);
        throw error;
    }
}

/**
 * 生成默认预览URL（1小时过期）
 * @param {string} objectKey - OSS文件key
 * @returns {Promise<string>} - 预览URL
 */
async function generateDefaultPreviewUrl(objectKey) {
    try {
        const params = new URLSearchParams();
        params.append('objectKey', objectKey);
        
        const response = await fetch(`${API_BASE_URL}/preview-url/default?${params}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const previewUrl = await response.text();
        console.log('默认预览URL:', previewUrl);
        return previewUrl;
    } catch (error) {
        console.error('生成默认预览URL失败:', error);
        throw error;
    }
}

/**
 * 批量生成预览URL
 * @param {string[]} objectKeys - OSS文件key列表
 * @param {number} expirationSeconds - 过期时间（秒，可选，默认3600）
 * @returns {Promise<string[]>} - 预览URL列表
 */
async function batchGeneratePreviewUrls(objectKeys, expirationSeconds = 3600) {
    try {
        const params = new URLSearchParams();
        if (expirationSeconds !== 3600) {
            params.append('expirationSeconds', expirationSeconds);
        }
        
        const response = await fetch(`${API_BASE_URL}/batch-preview-urls?${params}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(objectKeys)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const previewUrls = await response.json();
        console.log('批量预览URL列表:', previewUrls);
        return previewUrls;
    } catch (error) {
        console.error('批量生成预览URL失败:', error);
        throw error;
    }
}

/**
 * 设置文件为内联预览模式
 * @param {string} objectKey - OSS文件key
 * @returns {Promise<string>} - 成功消息
 */
async function setInlineContentDisposition(objectKey) {
    try {
        const response = await fetch(`${API_BASE_URL}/set-inline/${objectKey}`, {
            method: 'POST'
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const message = await response.text();
        console.log('设置内联预览成功:', message);
        return message;
    } catch (error) {
        console.error('设置内联预览失败:', error);
        throw error;
    }
}

/**
 * 获取文件信息
 * @param {string} objectKey - OSS文件key
 * @returns {Promise<Object>} - 文件信息对象
 */
async function getFileInfo(objectKey) {
    try {
        const params = new URLSearchParams();
        params.append('objectKey', objectKey);
        
        const response = await fetch(`${API_BASE_URL}/file-info?${params}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const fileInfo = await response.json();
        console.log('文件信息:', fileInfo);
        return fileInfo;
    } catch (error) {
        console.error('获取文件信息失败:', error);
        throw error;
    }
}

/**
 * 在HTML中显示图片预览
 * @param {string} objectKey - OSS文件key
 * @param {string} imgElementId - img元素ID
 */
async function displayImagePreview(objectKey, imgElementId) {
    try {
        const previewUrl = await generatePreviewUrl(objectKey);
        const imgElement = document.getElementById(imgElementId);
        if (imgElement) {
            imgElement.src = previewUrl;
            imgElement.style.display = 'block';
        }
    } catch (error) {
        console.error('显示图片预览失败:', error);
    }
}

/**
 * 批量显示图片预览
 * @param {string[]} objectKeys - OSS文件key列表
 * @param {string} containerId - 容器元素ID
 */
async function batchDisplayImagePreviews(objectKeys, containerId) {
    try {
        const previewUrls = await batchGeneratePreviewUrls(objectKeys);
        const container = document.getElementById(containerId);
        
        if (container) {
            container.innerHTML = '';
            previewUrls.forEach((url, index) => {
                const img = document.createElement('img');
                img.src = url;
                img.style.maxWidth = '200px';
                img.style.margin = '10px';
                img.alt = `图片 ${index + 1}`;
                container.appendChild(img);
            });
        }
    } catch (error) {
        console.error('批量显示图片预览失败:', error);
    }
}

/**
 * 使用示例
 */
async function exampleUsage() {
    try {
        // 示例1: 列举所有文件
        const allFiles = await listAllFiles();
        console.log('所有文件:', allFiles);
        
        // 示例2: 按前缀列举文件
        const imageFiles = await listFiles('images/');
        console.log('图片文件:', imageFiles);
        
        // 示例3: 生成单个预览URL
        if (allFiles.length > 0) {
            const previewUrl = await generatePreviewUrl('example.jpg');
            console.log('预览URL:', previewUrl);
            
            // 示例4: 获取文件信息
            const fileInfo = await getFileInfo('example.jpg');
            console.log('文件信息:', fileInfo);
            
            // 示例5: 在页面中显示图片
            // displayImagePreview('example.jpg', 'preview-img');
        }
        
        // 示例6: 批量生成预览URL
        if (allFiles.length > 1) {
            const fileKeys = allFiles.slice(0, 5).map(url => {
                // 从URL中提取objectKey
                const parts = url.split('/');
                return parts[parts.length - 1];
            });
            const batchUrls = await batchGeneratePreviewUrls(fileKeys);
            console.log('批量预览URL:', batchUrls);
        }
        
    } catch (error) {
        console.error('示例执行失败:', error);
    }
}

// 导出函数供其他模块使用
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        listFiles,
        listAllFiles,
        generatePreviewUrl,
        generateDefaultPreviewUrl,
        batchGeneratePreviewUrls,
        setInlineContentDisposition,
        getFileInfo,
        displayImagePreview,
        batchDisplayImagePreviews,
        exampleUsage
    };
}