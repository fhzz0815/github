"""
AI 智能体调度接口

提供智能体调用、任务状态查询、人工审批等能力。
"""

from fastapi import APIRouter, Depends

from app.api.deps import get_current_user, get_tenant_id

router = APIRouter()


@router.post('/invoke')
async def invoke_agent(
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """调用智能体执行任务"""
    # TODO: 根据请求内容路由到对应智能体
    return {'message': '智能体调用成功', 'task_id': ''}


@router.get('/tasks/{task_id}')
async def get_task_status(
    task_id: str,
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """查询智能体任务状态"""
    # TODO: 查询任务执行状态与结果
    return {'task_id': task_id, 'status': 'pending'}


@router.post('/tasks/{task_id}/approve')
async def approve_action(
    task_id: str,
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """人工审批智能体操作（如生成账单、发送通知）"""
    # TODO: 审批通过后继续执行智能体图
    return {'message': '审批完成'}
