# 多环境配置管理功能计划

## 功能概述

实现Spring Boot应用的多环境配置管理，支持开发环境和测试环境的配置分离，确保敏感信息安全。

## 功能需求

### 核心需求
- [x] 开发环境使用独立的配置文件（application-dev.properties）
- [x] 测试环境使用主配置文件（application.properties）
- [x] 敏感信息（数据库密码、阿里云OSS密钥）安全隔离
- [x] 模板文件（application-dev.properties.template）供团队参考
- [x] 实际配置文件不被提交到代码仓库

### 阿里云OSS配置需求
- [x] 支持阿里云OSS多环境配置
- [x] 开发环境使用真实OSS密钥
- [x] 测试环境使用占位符密钥
- [x] 解决"Could not resolve placeholder"配置错误

## 已实现功能

### 1. 配置文件结构
```
src/main/resources/
├── application.properties              # 测试环境配置（可提交）
├── application-dev.properties          # 开发环境配置（忽略提交）
└── application-dev.properties.template # 开发环境模板（可提交）
```

### 2. 具体配置内容

#### application.properties（测试环境）
- 包含测试环境基础配置
- OSS配置使用占位符：`TEST_ACCESS_KEY`、`TEST_SECRET_KEY`
- 数据库配置使用测试环境参数
- 可安全提交到代码仓库

#### application-dev.properties（开发环境）
- 包含完整开发环境配置
- 阿里云OSS真实配置（敏感信息）
- 数据库配置使用真实连接参数
- **不会被提交到代码仓库**

#### application-dev.properties.template（模板）
- 提供配置项占位符参考
- 包含所有必要的配置项说明
- 团队成员复制后填入实际值
- **会被提交到代码仓库**

### 3. Git配置
- 更新`.gitignore`忽略实际配置文件
- 保留模板文件在版本控制中
- 防止敏感信息意外提交

### 4. 启动方式
```bash
# 开发环境（默认）
mvn spring-boot:run

# 测试环境
mvn spring-boot:run -Dspring.profiles.active=test
```

## 使用指南

### 首次配置步骤
1. 复制模板文件：
   ```bash
   cp src/main/resources/application-dev.properties.template src/main/resources/application-dev.properties
   ```

2. 编辑`application-dev.properties`，填入真实配置值

3. 验证配置：
   ```bash
   mvn spring-boot:run
   ```

### 环境切换
- **开发环境**：默认使用`application-dev.properties`
- **测试环境**：使用`application.properties`

### 注意事项
- 永远不要直接编辑模板文件填入真实值
- 实际配置文件只在本地使用
- 配置变更时更新模板并通知团队

## 技术实现

### Spring Boot Profile机制
- 使用`spring.profiles.active`控制环境切换
- 支持通过环境变量和命令行参数配置

### 配置优先级
1. `application-dev.properties`（最高优先级）
2. `application.properties`（默认配置）

### 安全考虑
- 敏感信息与代码分离
- Git忽略实际配置文件
- 模板文件提供配置参考

## 后续优化

### 可扩展功能
- [ ] 支持更多环境（prod、staging等）
- [ ] 环境配置加密存储
- [ ] 配置项动态刷新
- [ ] 配置验证机制

### 集成改进
- [ ] CI/CD环境配置管理
- [ ] Docker容器环境配置
- [ ] Kubernetes配置映射

## 相关文件

- `src/main/resources/application.properties` - 测试环境配置
- `src/main/resources/application-dev.properties` - 开发环境配置（本地）
- `src/main/resources/application-dev.properties.template` - 配置模板
- `ENVIRONMENT_CONFIG.md` - 详细使用说明
- `.gitignore` - Git忽略规则

## 状态
✅ **已完成** - 多环境配置功能已完整实现并测试通过