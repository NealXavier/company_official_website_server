# OSS API 前端调用示例

这个目录包含了前端JavaScript调用OSS API的示例代码和演示页面。

## 文件说明

### 1. `oss-api-examples.js`
核心JavaScript库，封装了所有OSS API的调用方法：

- `listFiles(prefix)` - 按前缀列举文件
- `listAllFiles()` - 列举所有文件
- `generatePreviewUrl(objectKey, expirationSeconds)` - 生成预览URL
- `generateDefaultPreviewUrl(objectKey)` - 生成默认预览URL（1小时过期）
- `batchGeneratePreviewUrls(objectKeys, expirationSeconds)` - 批量生成预览URL
- `setInlineContentDisposition(objectKey)` - 设置文件为内联预览
- `getFileInfo(objectKey)` - 获取文件信息
- `displayImagePreview(objectKey, imgElementId)` - 在HTML中显示图片预览
- `batchDisplayImagePreviews(objectKeys, containerId)` - 批量显示图片预览

### 2. `oss-demo.html`
交互式演示页面，展示了所有API功能的使用方法。

### 3. API端点列表

后端已经暴露了以下REST API端点：

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/oss/list` | 列举文件（支持前缀过滤） |
| GET | `/api/oss/list/all` | 列举所有文件 |
| GET | `/api/oss/preview-url` | 生成预览URL |
| GET | `/api/oss/preview-url/default` | 生成默认预览URL |
| POST | `/api/oss/batch-preview-urls` | 批量生成预览URL |
| POST | `/api/oss/set-inline/{objectKey}` | 设置文件为内联预览 |
| GET | `/api/oss/file-info` | 获取文件信息 |

## 使用方法

### 1. 基本使用

在HTML页面中引入JavaScript文件：

```html
<script src="oss-api-examples.js"></script>
```

然后直接调用函数：

```javascript
// 列举所有文件
const files = await listAllFiles();
console.log('所有文件:', files);

// 生成预览URL
const previewUrl = await generatePreviewUrl('example.jpg');
console.log('预览URL:', previewUrl);
```

### 2. 在Vue/React中使用

```javascript
// 导入函数
import { listAllFiles, generatePreviewUrl } from './oss-api-examples.js';

// 在组件中使用
export default {
  data() {
    return {
      files: [],
      previewUrl: ''
    };
  },
  methods: {
    async loadFiles() {
      try {
        this.files = await listAllFiles();
      } catch (error) {
        console.error('加载文件失败:', error);
      }
    },
    
    async generatePreview(fileKey) {
      try {
        this.previewUrl = await generatePreviewUrl(fileKey);
      } catch (error) {
        console.error('生成预览失败:', error);
      }
    }
  }
};
```

### 3. 图片预览示例

```html
<!-- HTML -->
<img id="preview-image" style="max-width: 300px; display: none;" alt="预览图片">

<!-- JavaScript -->
<script>
async function showImagePreview(objectKey) {
  try {
    const previewUrl = await generatePreviewUrl(objectKey);
    const imgElement = document.getElementById('preview-image');
    imgElement.src = previewUrl;
    imgElement.style.display = 'block';
  } catch (error) {
    alert('预览失败: ' + error.message);
  }
}

// 调用
showImagePreview('example.jpg');
</script>
```

### 4. 批量处理示例

```javascript
// 批量生成预览URL
const fileKeys = ['image1.jpg', 'image2.png', 'image3.gif'];
const previewUrls = await batchGeneratePreviewUrls(fileKeys);

// 批量显示图片
const container = document.getElementById('image-container');
previewUrls.forEach(url => {
  const img = document.createElement('img');
  img.src = url;
  img.style.maxWidth = '200px';
  img.style.margin = '10px';
  container.appendChild(img);
});
```

## 运行演示页面

1. 确保后端服务已启动：
   ```bash
   mvn spring-boot:run
   ```

2. 在浏览器中打开 `oss-demo.html` 文件

3. 测试各种功能：
   - 文件列表加载
   - 预览URL生成
   - 文件信息获取
   - 批量操作

## 注意事项

1. **跨域问题**：确保后端服务已配置CORS，允许前端域名访问API
2. **文件类型**：预览功能支持常见图片格式（jpg、png、gif、webp等）和视频格式（mp4等）
3. **URL过期时间**：预览URL默认1小时过期，可以根据需要调整
4. **错误处理**：所有API调用都包含错误处理，建议在实际使用中也添加适当的错误处理

## 扩展建议

1. **添加加载状态**：在API调用期间显示加载动画
2. **分页功能**：对于大量文件，可以实现分页加载
3. **文件上传**：可以添加文件上传功能
4. **文件删除**：可以添加文件删除功能
5. **搜索功能**：实现文件名搜索和过滤
6. **分类显示**：按文件类型分类显示文件

## 技术支持

如果遇到问题，可以：
1. 查看浏览器控制台的网络请求和错误信息
2. 检查后端服务是否正常运行
3. 确认API端点是否正确
4. 查看日志输出了解详细错误信息