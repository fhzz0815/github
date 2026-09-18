"""
知识库管理接口

提供文档上传、知识库检索、索引管理等能力。
"""

from fastapi import APIRouter, Depends

from app.api.deps import get_current_user, get_tenant_id

router = APIRouter()


@router.post('/documents')
async def upload_document(
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """上传文档到知识库"""
    # TODO: 接收文件 -> 解析 -> 切片 -> 向量化 -> 存储
    return {'message': '文档上传成功'}


@router.get('/documents')
async def list_documents(
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """获取知识库文档列表"""
    return {'documents': []}


@router.post('/search')
async def search_knowledge(
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """检索知识库"""
    # TODO: 向量检索 + 重排
    return {'results': []}


@router.delete('/documents/{doc_id}')
async def delete_document(
    doc_id: str,
    current_user: dict = Depends(get_current_user),
    tenant_id: int = Depends(get_tenant_id),
):
    """删除知识库文档"""
    return {'message': '文档已删除'}
