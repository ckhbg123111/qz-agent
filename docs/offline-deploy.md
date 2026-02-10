# 无网环境部署说明

在有网机器上构建并导出镜像，拷贝到无网机后加载并启动。

## 一、在有网机器上（你当前已构建完成）

1. **导出镜像**（默认导出到 `./docker-images`）：
   ```bash
   chmod +x export-images.sh
   ./export-images.sh
   ```
   或指定目录：`./export-images.sh /path/to/output`

2. **拷贝到无网机**  
   将整个 `docker-images` 目录（内含 `qz-agent.tar`、`mysql.tar`、`redis.tar`）以及以下文件一起拷到无网机项目根目录：
   - `docker-compose.yml`
   - `deploy-offline.sh`
   - （可选）`.env`（若你在有网机用过环境变量）

## 二、在无网机器上

1. **安装 Docker 与 Docker Compose**  
   无网机也需提前安装好 Docker 和 Compose（用离线包或内网源）。

2. **加载镜像并启动**：
   ```bash
   chmod +x deploy-offline.sh
   ./deploy-offline.sh
   ```
   若镜像目录不是 `./docker-images`，则：
   ```bash
   ./deploy-offline.sh /path/to/docker-images
   ```

3. **访问**  
   浏览器打开：`http://<无网机IP>:8082/swagger-ui/index.html`

## 端口说明

- 应用：`8082`
- MySQL：`3307`
- Redis：`3381`

与 `docker-compose.yml` 中一致。
