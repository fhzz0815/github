"""
API v1 路由聚合

按功能域组织路由，所有 AI 相关接口以 /ai 为前缀。
"""

from fastapi import APIRouter

from app.api.v1 import admin, agents, chat, knowledge

api_router = APIRouter()

# === AI 会话与对话 ===
api_router.include_router(chat.router, prefix='/ai', tags=['AI 对话'])

# === AI 智能体调度 ===
api_router.include_router(agents.router, prefix='/ai/agents', tags=['AI 智能体'])

# === 知识库管理 ===
api_router.include_router(knowledge.router, prefix='/ai/knowledge', tags=['AI 知识库'])

# === AI 管理（模型配置、成本等） ===
api_router.include_router(admin.router, prefix='/ai/admin', tags=['AI 管理'])
