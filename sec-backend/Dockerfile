# ============================================================
# 基础镜像：使用 Eclipse Temurin JDK 21（Alpine 版，体积小）
# ============================================================
FROM eclipse-temurin:21-jre-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制 Maven 构建产物（jar 包）
COPY target/sec-backend.jar app.jar

# 解压 jar 包（为后续分层镜像做准备）
RUN java -Djarmode=layertools -jar app.jar extract

# ============================================================
# 运行镜像：只包含运行所需的最小文件
# ============================================================
FROM eclipse-temurin:21-jre-alpine

# 安装时区数据，设置时区为上海
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# 创建非 root 用户，提升安全性
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# 从 builder 阶段复制分层文件
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# 使用非 root 用户运行
USER appuser

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget -qO- http://localhost:8080/smart_restaurant/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
