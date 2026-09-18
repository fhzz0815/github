# ============================================================
# 智慧社区AI系统 - Docker 镜像构建
# 多阶段构建：依赖安装 -> 应用打包 -> 生产镜像
# ============================================================

# ---- 第一阶段：依赖安装 ----
FROM python:3.12-slim AS builder

WORKDIR /build

# 安装系统依赖
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# 安装 Python 依赖
COPY requirements/base.txt requirements/ai.txt ./
RUN pip install --no-cache-dir --user -r base.txt -r ai.txt

# ---- 第二阶段：生产镜像 ----
FROM python:3.12-slim

WORKDIR /app

# 安装运行时系统依赖
RUN apt-get update && apt-get install -y --no-install-recommends \
    libpq-dev \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 从 builder 阶段复制已安装的依赖
COPY --from=builder /root/.local /root/.local
ENV PATH=/root/.local/bin:$PATH

# 复制应用代码
COPY app/ ./app/
COPY pyproject.toml ./
COPY alembic.ini ./
COPY migrations/ ./migrations/

# 创建非 root 用户运行
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8000/health || exit 1

EXPOSE 8000

# 启动应用（使用 gunicorn + uvicorn worker）
CMD ["gunicorn", "app.main:app", "--worker-class", "uvicorn.workers.UvicornWorker", "--bind", "0.0.0.0:8000", "--workers", "4", "--timeout", "120"]
