"""
智慧社区AI系统 - 应用入口

基于 FastAPI 构建，提供 RESTful API 和 AI Agent 编排能力。
遵循分层架构: api -> agents/services -> models/core
"""

from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.v1.router import api_router
from app.core.config import settings
from app.core.logger import setup_logging


@asynccontextmanager
async def lifespan(application: FastAPI):
    """应用生命周期管理：启动时初始化，关闭时清理资源"""
    setup_logging()
    # TODO: 初始化数据库连接池
    # TODO: 初始化 Redis 连接池
    # TODO: 初始化 LLM 网关
    yield
    # TODO: 关闭连接池
    # TODO: 关闭 Redis 连接


app = FastAPI(
    title=settings.APP_NAME,
    version=settings.APP_VERSION,
    description='智慧社区AI系统 - 多租户SaaS物业管理平台',
    docs_url='/docs' if settings.APP_ENV == 'dev' else None,
    redoc_url='/redoc' if settings.APP_ENV == 'dev' else None,
    lifespan=lifespan,
)

# CORS 配置
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=True,
    allow_methods=['*'],
    allow_headers=['*'],
)

# 注册路由
app.include_router(api_router, prefix=settings.API_V1_PREFIX)


@app.get('/health', tags=['系统'])
async def health_check():
    """健康检查接口"""
    return {
        'status': 'ok',
        'app': settings.APP_NAME,
        'version': settings.APP_VERSION,
        'env': settings.APP_ENV,
    }
