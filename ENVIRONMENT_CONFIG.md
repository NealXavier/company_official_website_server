# 环境配置说明

## 配置文件结构

### 1. application.properties（测试环境）
- 默认配置文件，包含测试环境的基础配置
- 所有配置都可以安全提交到代码仓库
- 包含完整的OSS和数据库配置，使用测试环境的值

### 2. application-dev.properties（开发环境）
- 开发环境的实际配置文件
- 包含真实的敏感信息（数据库密码、OSS密钥等）
- **不会被提交到代码仓库**
- 通过`.gitignore`忽略

### 3. application-dev.properties.template（开发环境模板）
- 开发环境配置模板
- 包含配置项的占位符
- **会被提交到代码仓库**，用于团队成员参考

## 环境切换方法

### 开发环境（默认）
开发环境默认激活，无需额外配置：
```bash
mvn spring-boot:run
```

### 测试环境
如果需要使用测试环境配置，取消注释application.properties中的：
```properties
# spring.profiles.active=dev
```
改为：
```properties
spring.profiles.active=test
```

或者通过命令行参数指定：
```bash
mvn spring-boot:run -Dspring.profiles.active=test
```

## 首次使用步骤

1. **复制模板文件**：
   ```bash
   cp src/main/resources/application-dev.properties.template src/main/resources/application-dev.properties
   ```

2. **填入实际配置**：
   编辑`application-dev.properties`，填入真实的数据库密码、OSS密钥等

3. **验证配置**：
   ```bash
   mvn spring-boot:run
   ```

## 注意事项

- 永远不要直接编辑`application-dev.properties.template`填入真实值
- `application-dev.properties`文件只在本地使用，不会提交到仓库
- 如果配置项有变更，记得更新模板文件并通知团队成员
- 测试环境的配置可以直接修改`application.properties`并提交

## Git状态检查

确保以下文件状态正确：
```bash
# 应该被忽略的文件
git status --ignored | grep application-dev.properties

# 应该被跟踪的文件
git status | grep application-dev.properties.template
```