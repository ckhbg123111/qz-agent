#!/usr/bin/env bash
# 在无网络的机器上执行：从 tar 加载镜像并启动容器（不构建、不拉取）
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"
IMAGES_DIR="${1:-./docker-images}"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker 未安装，请先安装 Docker."
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "未找到 docker compose 插件，请先安装 Docker Compose."
  exit 1
fi

if [ ! -d "$IMAGES_DIR" ]; then
  echo "镜像目录不存在: $IMAGES_DIR"
  echo "用法: $0 [镜像目录]"
  echo "请先将有网机上的 docker-images 目录（或你保存 tar 的目录）拷贝到本机，再执行此脚本."
  exit 1
fi

IMAGES_DIR="$(cd "$IMAGES_DIR" && pwd)"
echo "从目录加载镜像: $IMAGES_DIR"

for f in "$IMAGES_DIR"/qz-agent.tar "$IMAGES_DIR"/mysql.tar "$IMAGES_DIR"/redis.tar; do
  if [ -f "$f" ]; then
    echo "加载: $f"
    docker load -i "$f"
  else
    echo "跳过（文件不存在）: $f"
  fi
done

echo "启动容器（仅使用已有镜像，不构建、不拉取）..."
docker compose up -d --no-build

echo "容器状态:"
docker compose ps

echo "完成. 访问 http://localhost:8082/swagger-ui/index.html"
echo "（端口以 docker-compose.yml 为准，此处为 8082）"
