#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker 未安装，请先安装 Docker."
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "未找到 docker compose 插件，请先安装 Docker Compose."
  exit 1
fi

echo "开始构建与启动容器..."
docker compose up -d --build

echo "容器状态:"
docker compose ps

echo "完成. 访问 http://localhost:8080/swagger-ui/index.html"
