"""
AI 管理接口

提供模型配置、成本监控、评测管理等能力。
"""

from fastapi import APIRouter, Depends

from app.api.deps import get_current_user, get_tenant_id

router = APIRouter()


@router.get('/models')
async def list_models(
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """获取可用的模型列表"""
    # TODO: 从 LLM 网关查询可用模型
    return {'models': []}


@router.get('/costs')
async def get_cost_stats(
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """获取 AI 调用成本统计"""
    # TODO: 统计 token 消耗与成本
    return {}


@router.post('/eval/run')
async def run_evaluation(
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """运行离线评测"""
    # TODO: 触发评测流程
    return {'message': '评测已启动'}
