# 智慧社区AI系统 - 项目规则

## 技术栈
- **后端**: Python 3.12 + FastAPI + SQLAlchemy 2.x + Alembic
- **AI 编排**: LangChain + LangGraph
- **数据库**: MySQL 8 (主库) + Redis 7 (缓存/会话)
- **消息队列**: RabbitMQ / Redis (Celery)
- **向量库**: pgvector / Milvus
- **前端**: Vue 3 + Vite + Element Plus (管理端) / uni-app (移动端)
- **部署**: Docker + Docker Compose + Kubernetes

## 项目结构
```
smart-community/
├── app/                    # 后端应用
│   ├── api/               # API 路由层
│   ├── agents/            # LangGraph 智能体编排
│   ├── tools/             # 工具注册与业务工具
│   ├── rag/               # 检索增强生成
│   ├── llm/               # LLM 网关
│   ├── memory/            # 记忆服务
│   ├── models/            # SQLAlchemy ORM 模型
│   ├── schemas/           # Pydantic 数据契约
│   ├── services/          # 领域服务
│   └── tasks/             # Celery 异步任务
├── tests/                 # 测试
├── migrations/            # Alembic 迁移
├── deploy/                # 部署配置
└── requirements/          # 依赖清单
```

## 编码规范
1. **Python**: 遵循 PEP 8，使用 Ruff 进行代码检查和格式化
2. **命名**: 变量/函数使用 snake_case，类使用 PascalCase，常量使用 UPPER_CASE
3. **类型注解**: 所有函数和方法的参数/返回值必须有类型注解
4. **导入顺序**: 标准库 -> 第三方库 -> 本地模块，每组间空一行

## 运行命令
```bash
# 安装依赖
pip install -r requirements/dev.txt

# 启动开发服务器
uvicorn app.main:app --reload --port 8000

# 运行测试
pytest tests/ -q

# 代码检查
ruff check app/
ruff format app/

# 类型检查
mypy app/

# 数据库迁移
alembic upgrade head

# 启动 Celery Worker
celery -A app.tasks.celery_app worker -l info

# 启动 Docker 环境
docker-compose up -d
```

## 数据库规范
- 全表使用 utf8mb4 字符集，排序规则 utf8mb4_0900_ai_ci
- 所有业务表必须包含 tenant_id 字段（多租户隔离）
- 金额字段使用 DECIMAL(14,2)，禁止 FLOAT/DOUBLE
- 状态/类型枚举字段使用 SMALLINT UNSIGNED
- 外键约束由应用层保证，数据库不建外键

## 多租户规范
1. tenant_id 由鉴权上下文注入，禁止前端传入
2. 所有唯一索引以 tenant_id 为首列
3. 数据权限在 WHERE 中追加社区/部门范围条件
4. AI 检索同样注入租户与社区过滤

## AI 开发规范
1. 业务代码禁止直接调用 AI/LLM 依赖
2. 所有 AI 调用通过 LLM 网关统一入口
3. 每次 AI 调用必须记录 Trace（链路追踪）
4. 提示词模板统一管理在 prompts/ 目录
5. 工具调用必须经过权限校验和幂等处理
