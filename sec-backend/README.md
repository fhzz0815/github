# 智慧餐厅后台管理系统 - 后端

## 项目简介
智慧餐厅后台管理系统服务端，基于 Spring Boot 4.1.0 + MyBatis 构建，提供门店、菜品、订单、会员、库存等核心业务的 RESTful API 服务。

## 技术栈
- **核心框架**：Spring Boot 4.1.0
- **Java 版本**：Java 21
- **持久层**：MyBatis + PageHelper 分页
- **数据库**：MySQL 8.0
- **连接池**：Druid
- **缓存**：Redis + Redisson
- **消息队列**：RabbitMQ
- **认证**：JWT
- **接口文档**：SpringDoc OpenAPI 3（Swagger）
- **工具类**：Hutool + Guava
- **日志**：Logback + SLF4J

## 环境要求
- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+

## 快速开始

### 1. 初始化数据库
```bash
# 执行建表脚本
mysql -u root -p < ../database/smart_restaurant.sql
```

### 2. 修改配置
编辑 `src/main/resources/application-dev.yml`，修改数据库、Redis、RabbitMQ 连接信息。

### 3. 启动服务
```bash
# 编译
mvn clean compile

# 运行（默认端口 8080）
mvn spring-boot:run
```

### 4. 访问接口文档
启动后访问：`http://localhost:8080/swagger-ui.html`

## 目录结构

```
sec-backend/
├── src/main/java/com/iwe3/sec/
│   ├── SecApplication.java       # 启动类
│   ├── common/                   # 通用类（Result、异常处理、JWT）
│   ├── config/                   # 配置类（WebMvc、Redis）
│   ├── controller/               # 表现层（43个 + Auth）
│   ├── entity/                   # 实体类（43个）
│   ├── mapper/                   # 数据访问层（43个）
│   ├── service/                  # 业务接口（43个 + Auth）
│   │   └── impl/                 # 业务实现
│   └── ...
├── src/main/resources/
│   ├── mapper/                   # MyBatis XML（43个）
│   ├── application.yml           # 主配置
│   ├── application-dev.yml       # 开发环境
│   ├── application-prod.yml      # 生产环境
│   └── logback-spring.xml        # 日志配置
└── pom.xml
```

## 接口规范
- 统一前缀：`/api/v1`
- 统一响应：`{ "code": 0, "message": "ok", "data": {} }`
- 认证方式：Header `Authorization: Bearer {token}`
- 错误码：0成功，1xxx参数，2xxx业务，3xxx权限，4xxx支付，5xxx系统

## 部署

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/sec-backend.jar --spring.profiles.active=prod
```

## 贡献指南
1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/xxx`
3. 提交代码：`git commit -m 'feat: xxx'`
4. 推送分支：`git push origin feature/xxx`
5. 提交 Pull Request

## Git 分支规范
- `main`：主分支，生产环境代码
- `develop`：开发分支
- `feature/*`：特性分支
- `release/*`：发布分支
- `hotfix/*`：热修复分支
