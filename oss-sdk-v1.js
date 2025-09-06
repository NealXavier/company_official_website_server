/**
 * OSS JavaScript SDK v1 - 适配项目统一命名风格
 * 独立的OSS操作库，可在任何外部JS文件中调用
 * 使用方式：直接引入此文件，然后调用相关方法
 */

(function(window) {
    'use strict';

    // 配置
    const CONFIG = {
        API_BASE_URL: 'http://localhost:8088/v1/osss',
        DEFAULT_TIMEOUT: 30000,
        MAX_RETRIES: 3,
        RETRY_DELAY: 1000
    };

    // 工具函数
    const Utils = {
        // 延迟函数
        delay: (ms) => new Promise(resolve => setTimeout(resolve, ms)),
        
        // 重试机制
        retry: async (fn, maxRetries = CONFIG.MAX_RETRIES, delay = CONFIG.RETRY_DELAY) => {
            let lastError;
            for (let i = 0; i < maxRetries; i++) {
                try {
                    return await fn();
                } catch (error) {
                    lastError = error;
                    if (i < maxRetries - 1) {
                        await Utils.delay(delay * (i + 1));
                    }
                }
            }
            throw lastError;
        },
        
        // 构建URL参数
        buildQueryString: (params) => {
            const searchParams = new URLSearchParams();
            Object.keys(params).forEach(key => {
                if (params[key] !== undefined && params[key] !== null) {
                    searchParams.append(key, params[key]);
                }
            });
            return searchParams.toString();
        },
        
        // 统一处理API响应
        handleApiResponse: async (response) => {
            if (!response.ok) {
                let errorMessage = `HTTP错误: ${response.status}`;
                try {
                    const errorData = await response.text();
                    errorMessage += ` - ${errorData}`;
                } catch (e) {
                    // 忽略解析错误
                }
                throw new Error(errorMessage);
            }
            
            const data = await response.json();
            // 处理BaseResponse格式
            if (data.code === 0) {
                return data.data;
            } else {
                throw new Error(data.message || 'API调用失败');
            }
        }
    };

    // API请求函数
    const ApiClient = {
        // GET请求
        get: async (endpoint, params = {}) => {
            const queryString = Utils.buildQueryString(params);
            const url = `${CONFIG.API_BASE_URL}${endpoint}${queryString ? '?' + queryString : ''}`;
            
            return Utils.retry(async () => {
                const response = await fetch(url, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    signal: AbortSignal.timeout(CONFIG.DEFAULT_TIMEOUT)
                });
                return Utils.handleApiResponse(response);
            });
        },
        
        // POST请求
        post: async (endpoint, data = {}, params = {}) => {
            const queryString = Utils.buildQueryString(params);
            const url = `${CONFIG.API_BASE_URL}${endpoint}${queryString ? '?' + queryString : ''}`;
            
            return Utils.retry(async () => {
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data),
                    signal: AbortSignal.timeout(CONFIG.DEFAULT_TIMEOUT)
                });
                return Utils.handleApiResponse(response);
            });
        }
    };

    // 主要的OSS SDK对象
    const OssSDK = {
        
        /**
         * 配置SDK
         * @param {Object} config - 配置对象
         * @param {string} config.apiBaseUrl - API基础URL
         * @param {number} config.timeout - 请求超时时间（毫秒）
         * @param {number} config.maxRetries - 最大重试次数
         */
        config: function(config) {
            if (config.apiBaseUrl) {
                CONFIG.API_BASE_URL = config.apiBaseUrl.replace(/\/$/, '');
            }
            if (config.timeout) {
                CONFIG.DEFAULT_TIMEOUT = config.timeout;
            }
            if (config.maxRetries) {
                CONFIG.MAX_RETRIES = config.maxRetries;
            }
            return this;
        },

        /**
         * 获取所有OSS文件
         * @returns {Promise<string[]>} - 文件URL列表
         */
        getAllOsss: function() {
            return ApiClient.get('/getAllOsss');
        },

        /**
         * 按前缀获取OSS文件
         * @param {string} prefix - 文件前缀
         * @returns {Promise<string[]>} - 文件URL列表
         */
        getOsssByPrefix: function(prefix) {
            return ApiClient.get('/getOsssByPrefix', { prefix: prefix });
        },

        /**
         * 生成预览URL
         * @param {string} objectKey - OSS文件key
         * @param {number} expirationSeconds - 过期时间（秒，可选，默认3600）
         * @returns {Promise<string>} - 预览URL
         */
        generatePreviewUrl: function(objectKey, expirationSeconds = 3600) {
            return ApiClient.get('/generatePreviewUrl', {
                objectKey: objectKey,
                expirationSeconds: expirationSeconds
            });
        },

        /**
         * 生成默认预览URL（1小时过期）
         * @param {string} objectKey - OSS文件key
         * @returns {Promise<string>} - 预览URL
         */
        generateDefaultPreviewUrl: function(objectKey) {
            return ApiClient.get('/generateDefaultPreviewUrl', {
                objectKey: objectKey
            });
        },

        /**
         * 批量生成预览URL
         * @param {string[]} objectKeys - OSS文件key列表
         * @param {number} expirationSeconds - 过期时间（秒，可选，默认3600）
         * @returns {Promise<string[]>} - 预览URL列表
         */
        batchGeneratePreviewUrls: function(objectKeys, expirationSeconds = 3600) {
            return ApiClient.post('/batchGeneratePreviewUrls', objectKeys, {
                expirationSeconds: expirationSeconds
            });
        },

        /**
         * 设置文件为内联预览模式
         * @param {string} objectKey - OSS文件key
         * @returns {Promise<string>} - 成功消息
         */
        setInlineContentDisposition: function(objectKey) {
            return ApiClient.post('/setInlineContentDisposition', null, {
                objectKey: objectKey
            });
        },

        /**
         * 获取文件信息
         * @param {string} objectKey - OSS文件key
         * @returns {Promise<Object>} - 文件信息对象
         */
        getOssInfoByKey: function(objectKey) {
            return ApiClient.get('/getOssInfoByKey', {
                objectKey: objectKey
            });
        },

        /**
         * 判断文件是否为图片
         * @param {string} fileName - 文件名
         * @returns {boolean} - 是否为图片
         */
        isImageFile: function(fileName) {
            if (!fileName) return false;
            const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg', 'bmp'];
            const extension = fileName.split('.').pop().toLowerCase();
            return imageExtensions.includes(extension);
        },

        /**
         * 判断文件是否为视频
         * @param {string} fileName - 文件名
         * @returns {boolean} - 是否为视频
         */
        isVideoFile: function(fileName) {
            if (!fileName) return false;
            const videoExtensions = ['mp4', 'webm', 'ogg', 'avi', 'mov'];
            const extension = fileName.split('.').pop().toLowerCase();
            return videoExtensions.includes(extension);
        },

        /**
         * 判断文件是否为音频
         * @param {string} fileName - 文件名
         * @returns {boolean} - 是否为音频
         */
        isAudioFile: function(fileName) {
            if (!fileName) return false;
            const audioExtensions = ['mp3', 'wav', 'ogg', 'aac', 'flac'];
            const extension = fileName.split('.').pop().toLowerCase();
            return audioExtensions.includes(extension);
        },

        /**
         * 从URL中提取objectKey
         * @param {string} url - OSS文件URL
         * @returns {string} - objectKey
         */
        extractObjectKey: function(url) {
            if (!url) return '';
            try {
                const urlObj = new URL(url);
                const pathname = urlObj.pathname;
                return pathname.startsWith('/') ? pathname.substring(1) : pathname;
            } catch (e) {
                const parts = url.split('/');
                return parts[parts.length - 1] || '';
            }
        },

        /**
         * 获取文件扩展名
         * @param {string} fileName - 文件名
         * @returns {string} - 扩展名（小写）
         */
        getFileExtension: function(fileName) {
            if (!fileName) return '';
            const parts = fileName.split('.');
            return parts.length > 1 ? parts.pop().toLowerCase() : '';
        },

        /**
         * 格式化文件大小
         * @param {number} bytes - 字节数
         * @returns {string} - 格式化的大小字符串
         */
        formatFileSize: function(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        },

        /**
         * 格式化日期
         * @param {Date|string} date - 日期对象或字符串
         * @returns {string} - 格式化日期字符串
         */
        formatDate: function(date) {
            if (!date) return '';
            const d = new Date(date);
            return d.toLocaleString('zh-CN');
        }
    };

    // 暴露到全局
    if (typeof module !== 'undefined' && module.exports) {
        // CommonJS
        module.exports = OssSDK;
    } else if (typeof define === 'function' && define.amd) {
        // AMD
        define(function() {
            return OssSDK;
        });
    } else {
        // 浏览器全局变量
        window.OssSDK = OssSDK;
    }

})(typeof window !== 'undefined' ? window : this);