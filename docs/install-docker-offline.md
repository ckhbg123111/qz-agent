# 内网/离线安装 Docker 与 Docker Compose

适用于无外网或只能访问内网 yum/apt 源的环境。

---

## 思路概览

| 方式 | 适用场景 |
|------|----------|
| **内网 yum/apt 源** | 公司已有 Docker 仓库镜像，只需改源配置 |
| **离线包安装** | 完全无网，在有网机下载 rpm/deb 和二进制，拷到内网机安装 |

---

## 一、内网 YUM/APT 源方式

前提：内网已有 Docker 官方仓库的镜像（如 Nexus、Artifactory、或同步好的 `yum.dockerproject.org` / `download.docker.com`）。

### 1. YUM 系（CentOS / RHEL / Rocky / AlmaLinux）

**有网机上看当前用的 Docker 源：**
```bash
cat /etc/yum.repos.d/docker*.repo
# 或
cat /etc/yum.repos.d/*.repo | grep -A5 docker
```

**在内网机上改为内网地址**（示例，按你们实际地址改）：

```bash
# 新建或编辑
sudo tee /etc/yum.repos.d/docker.repo << 'EOF'
[docker-ce-stable]
name=Docker CE Stable - $basearch
baseurl=http://内网镜像IP或域名/docker-ce/linux/centos/$releasever/$basearch/stable
# 或 RHEL: .../docker-ce/linux/rhel/$releasever/$basearch/stable
enabled=1
gpgcheck=0
# 若内网有 GPG 钥：gpgcheck=1 并配置 gpgkey=
EOF
```

然后安装：
```bash
sudo yum makecache
sudo yum install -y docker-ce docker-ce-cli containerd.io
```

**RHEL 8+ 用 dnf：**
```bash
sudo dnf makecache
sudo dnf install -y docker-ce docker-ce-cli containerd.io
```

### 2. APT 系（Debian / Ubuntu）

**有网机上看当前 Docker 源：**
```bash
cat /etc/apt/sources.list.d/docker.list
# 或
ls /etc/apt/sources.list.d/
```

**在内网机上改为内网地址：**

```bash
# 示例：Ubuntu Jammy
sudo tee /etc/apt/sources.list.d/docker.list << 'EOF'
deb [arch=amd64] http://内网镜像IP或域名/linux/ubuntu jammy stable
EOF

sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io
```

Debian 或其它版本把 `jammy` 换成对应代号（如 `bookworm`），并确认内网镜像路径一致。

### 3. 内网没有现成 Docker 源时

需要运维先在可上网的机器上**同步** Docker 官方仓库到内网 Nexus/YUM/APT 镜像，再在内网机按上面方式把 `baseurl` / `deb` 指到该内网地址。同步方法见各镜像工具文档（如 Nexus 的 Docker 代理、或 `reposync` 等）。

---

## 二、完全离线：用安装包 + 二进制

适用于**完全没有 yum/apt 外网、也没有内网 Docker 源**的情况：在有网机下载，拷到无网机安装。

### 1. 在有网机上下载（与无网机同系统、同架构）

**YUM 系（CentOS 7/8、RHEL、Rocky 等）：**
```bash
mkdir -p ~/docker-offline
cd ~/docker-offline

# 安装 yum-utils 以便用 yumdownloader
sudo yum install -y yum-utils

# 下载 Docker 相关 rpm（不安装）
sudo yumdownloader --resolve --destdir=. docker-ce docker-ce-cli containerd.io

# 若上面报错，可先加官方源再下载：
# sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
# sudo yumdownloader --resolve --destdir=. docker-ce docker-ce-cli containerd.io
```

**APT 系（Ubuntu/Debian）：**
```bash
mkdir -p ~/docker-offline
cd ~/docker-offline

# 先加 Docker 源并 update，再下载 deb（不安装）
sudo apt-get update
sudo apt-get install -y dpkg-dev
apt-get download $(apt-cache depends --recurse --no-recommends docker-ce docker-ce-cli containerd.io 2>/dev/null | grep "deb " | awk '{print $2}' | sort -u)
# 或简单点：从 https://download.docker.com/linux/ubuntu/dists/ 对应版本 pool/stable/ 里手动下 docker-ce、docker-ce-cli、containerd.io 的 .deb
```

**Docker Compose 插件（推荐，与 Docker 一起用）：**
```bash
# 官方 Compose 插件是 docker-compose-plugin，会提供一个二进制
# 离线时可直接下载 standalone 的 docker compose 二进制（见下）
```

**Docker Compose 独立二进制（无网机只需这一个即可 `docker compose`）：**
```bash
# 在 ~/docker-offline 下执行，选与无网机一致的架构
# Linux x86_64
curl -sL "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" -o docker-compose
chmod +x docker-compose

# 若为 ARM64（如树莓派、鲲鹏）
# curl -sL "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-aarch64" -o docker-compose
```

把 `~/docker-offline` 里所有文件（rpm 或 deb + `docker-compose` 二进制）拷到 U 盘或内网共享。

### 2. 在无网机上安装

**YUM 系：**
```bash
cd /path/to/docker-offline   # 你拷贝过来的目录
sudo yum localinstall -y *.rpm
# 或
sudo rpm -ivh *.rpm
```

**APT 系：**
```bash
cd /path/to/docker-offline
sudo dpkg -i *.deb
sudo apt-get install -f -y   # 若有依赖缺失，尽量用本地 deb 补全后再 -f
```

**Docker Compose 二进制：**
```bash
sudo cp docker-compose /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
# 验证
docker compose version
```

### 3. 开机自启与权限

```bash
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER   # 当前用户免 sudo 用 docker（需重新登录生效）
```

---

## 三、简要对照

- **有内网 yum/apt 镜像**：在 repo 里把 Docker 的 baseurl/deb 改成内网地址，然后 `yum install` / `apt install`，再按需装 Docker Compose 插件或二进制。
- **完全离线**：有网机用 `yumdownloader`/`apt download` 拿齐 rpm/deb，再下好 `docker-compose-linux-x86_64`，拷到无网机用 `yum localinstall`/`dpkg -i` 和 `cp docker-compose /usr/local/bin` 安装。

按你司是「有内网源」还是「完全离线」选其一即可。
