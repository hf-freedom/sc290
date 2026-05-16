#!/bin/bash

echo "================================"
echo "会员储值消费系统 - 前端项目"
echo "================================"
echo ""

cd "$(dirname "$0")"

if [ ! -d "node_modules" ]; then
    echo "正在安装依赖..."
    npm install
    if [ $? -eq 0 ]; then
        echo "✓ 依赖安装成功"
    else
        echo "✗ 依赖安装失败"
        exit 1
    fi
else
    echo "✓ 依赖已安装"
fi

echo ""
echo "正在启动开发服务器..."
echo "访问地址: http://localhost:3003"
echo "按 Ctrl+C 停止服务器"
echo ""

npm run dev
