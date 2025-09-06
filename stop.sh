#!/bin/bash

echo "停止公司官网服务器..."

# 查找并停止Spring Boot进程
PID=$(pgrep -f "spring-boot:run")
if [ -n "$PID" ]; then
    kill $PID
    echo "✅ 已停止进程 $PID"
else
    echo "❌ 未找到运行中的服务"
fi

# 清理可能存在的PID文件
rm -f app.pid 2>/dev/null