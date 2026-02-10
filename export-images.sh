#!/usr/bin/env bash
# 在有网络的机器上执行：导出所有镜像为 tar，便于拷贝到无网机
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"
OUTPUT_DIR="${1:-./docker-images}"

mkdir -p "$OUTPUT_DIR"
OUTPUT_DIR="$(cd "$OUTPUT_DIR" && pwd)"

echo "正在导出镜像到: $OUTPUT_DIR"

# 确保 app 镜像存在（若未构建则先构建）
if ! docker image inspect qz-agent:latest >/dev/null 2>&1; then
  echo "未找到 qz-agent:latest，先执行构建..."
  docker compose build app
fi

docker save -o "$OUTPUT_DIR/qz-agent.tar"          qz-agent:latest
docker save -o "$OUTPUT_DIR/mysql.tar"             mysql:8.4
docker save -o "$OUTPUT_DIR/redis.tar"             redis:7.4

echo "导出完成. 请将目录 $OUTPUT_DIR 拷贝到无网机后执行 deploy-offline.sh"
ls -la "$OUTPUT_DIR"/*.tar
