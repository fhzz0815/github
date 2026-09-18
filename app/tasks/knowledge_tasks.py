"""
知识库相关异步任务
"""

from celery import shared_task

from app.core.logger import get_logger

logger = get_logger(__name__)


@shared_task(bind=True, max_retries=3)
def reindex_knowledge(self):
    """重建知识库索引"""
    logger.info('开始重建知识库索引')
    # TODO: 遍历所有文档重新向量化
    return {'status': 'completed'}


@shared_task(bind=True, max_retries=3)
def process_document(self, doc_id: str, content: str, metadata: dict):
    """异步处理文档（解析 -> 切片 -> 向量化）"""
    logger.info('处理文档', doc_id=doc_id)
    # TODO: 文档处理流程
    return {'doc_id': doc_id, 'status': 'processed'}
