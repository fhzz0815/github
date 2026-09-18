"""
AI 会话与流式对话接口

提供会话管理、流式对话、历史查询等能力。
"""

from fastapi import APIRouter, Depends

from app.api.deps import get_current_user, get_tenant_id

router = APIRouter()


@router.post('/sessions')
async def create_session(
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """创建新会话"""
    # TODO: 创建会话并返回会话ID
    return {'message': '创建会话成功'}


@router.get('/sessions/{session_id}/messages')
async def get_session_messages(
    session_id: str,
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """获取会话历史消息"""
    # TODO: 从数据库/缓存加载消息历史
    return {'messages': []}


@router.post('/chat/completions')
async def chat_completion(
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """流式对话接口（SSE）"""
    # TODO: 调用 LangGraph 编排引擎，返回 SSE 流式响应
    return {'message': '对话功能待实现'}
