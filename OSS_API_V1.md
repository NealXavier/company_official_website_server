# OSS API V1 统一命名规范

## 接口地址变更

为了统一项目命名风格，OSS API接口已从 `/api/oss/**` 变更为 `/v1/osss/**`，并采用与其他控制器一致的命名规范。

## 新接口列表

| 原接口 | 新接口 | 方法 | 描述 |
|--------|--------|------|------|
| `/api/oss/list` | `/v1/osss/getOsssByPrefix` | GET | 按前缀获取OSS文件 |
| `/api/oss/list/all` | `/v1/osss/getAllOsss` | GET | 获取所有OSS文件 |
| `/api/oss/preview-url` | `/v1/osss/generatePreviewUrl` | GET | 生成预览URL |
| `/api/oss/preview-url/default` | `/v1/osss/generateDefaultPreviewUrl` | GET | 生成默认预览URL |
| `/api/oss/batch-preview-urls` | `/v1/osss/batchGeneratePreviewUrls` | POST | 批量生成预览URL |
| `/api/oss/set-inline/{objectKey}` | `/v1/osss/setInlineContentDisposition` | POST | 设置文件内联预览 |
| `/api/oss/file-info` | `/v1/osss/getOssInfoByKey` | GET | 获取OSS文件信息 |

## 请求/响应格式

所有接口统一使用 `BaseResponse<T>` 格式：

```json
{
  "code": 0,           // 0表示成功，其他表示错误
  "message": "success", // 响应消息
  "data": [...]        // 具体数据
}
```

## JavaScript SDK V1

已更新 `oss-sdk-v1.js` 适配新接口格式：

```javascript
// 获取所有文件
const files = await OssSDK.getAllOsss();

// 按前缀获取文件
const imageFiles = await OssSDK.getOsssByPrefix('images/');

// 生成预览URL
const previewUrl = await OssSDK.generatePreviewUrl('example.jpg');

// 获取文件信息
const fileInfo = await OssSDK.getOssInfoByKey('example.jpg');
```

## 完整示例

```javascript
// 引入SDK
<script src="oss-sdk-v1.js"></script>

// 使用示例
async function loadImages() {
    try {
        // 获取所有图片文件
        const files = await OssSDK.getOsssByPrefix('images/');
        
        // 批量生成预览URL
        const previewUrls = await OssSDK.batchGeneratePreviewUrls(files.slice(0, 5));
        
        // 在页面中显示
        previewUrls.forEach(url => {
            const img = document.createElement('img');
            img.src = url;
            document.body.appendChild(img);
        });
    } catch (error) {
        console.error('加载失败:', error);
    }
}
```

## 安全更新

SecurityConfig已同步更新，新路径 `/v1/osss/**` 已加入白名单，无需登录即可访问。

## 注意事项

1. 基础路径从 `/api/oss` 变更为 `/v1/osss`
2. 所有接口返回统一的 `BaseResponse<T>` 格式
3. JavaScript SDK已同步更新，建议使用新的 `oss-sdk-v1.js`
4. 原有的 `oss-api-examples.js` 已标记为过时

## 向后兼容

为了平滑过渡，建议前端代码同时兼容新旧接口，或逐步迁移到新接口格式。