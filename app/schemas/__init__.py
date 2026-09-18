"""
数据契约层 - Pydantic 模型

定义请求/响应/内部数据模型，不依赖 ORM 会话。
"""

from app.schemas.chat import ChatRequest, ChatResponse, MessageSchema
from app.schemas.common import PaginationParams, ResponseModel

__all__ = [
    'ResponseModel',
    'PaginationParams',
    'ChatRequest',
    'ChatResponse',
    'MessageSchema',
]
