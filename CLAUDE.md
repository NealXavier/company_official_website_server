# 项目启动命令

## 精确控制命令（需明确指令）
- **启动应用** - 运行 `mvn spring-boot:run`
- **停止应用** - 停止后台进程
- **重启应用** - 仅在你明确说"重启应用"时执行

## Git分支命名规范（GitHub Flow）
- `main` - 主分支
- `feature/功能描述` - 新功能开发
- `fix/问题描述` - bug修复
- `docs/文档更新` - 文档修改
- `refactor/重构描述` - 代码重构
- 使用连字符连接，全小写

## 当前状态检查
- 运行 `ps aux | grep spring-boot` 查看是否运行
- 访问 http://localhost:8088/login 测试登录

## 管理员账号
- 用户名：admin
- 密码：123456
- 数据库表：Admins