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

### 0. 配置环境变量（推荐）

项目根目录提供了 `example.env` 模板。请先复制为 `.env` 并按需修改：

```bash
cp example.env .env
```

说明：
- `docker compose` 会自动读取项目根目录 `.env`。
- 本项目本地 Spring Boot 启动也会读取 `.env`（`spring.config.import=optional:file:.env[.properties]`）。
- 修改 `.env` 后，必须重启进程才会生效。

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

## 三、本地启动时使用 .env

如果你不是用 Docker，而是本地直接启动 Spring Boot：

1. 在项目根目录准备 `.env`（可由 `example.env` 复制）。
2. 启动应用（例如在项目根目录执行）：
   ```bash
   mvn -pl qz-agent-web spring-boot:run
   ```
3. 修改 `.env` 后，停止并重新启动应用，配置才会生效。

## 端口说明

- 应用：`8082`
- MySQL：`3307`
- Redis：`3381`

与 `docker-compose.yml` 中一致。
