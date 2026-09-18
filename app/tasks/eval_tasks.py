"""
评测与运维相关异步任务
"""

from celery import shared_task

from app.core.logger import get_logger

logger = get_logger(__name__)


@shared_task
def sync_ai_cost():
    """同步 AI 调用成本数据"""
    logger.info('开始同步AI成本数据')
    # TODO: 从 Trace 表汇总成本数据
    return {'status': 'completed'}


@shared_task
def run_evaluation_suite(suite_name: str):
    """运行离线评测套件"""
    logger.info('运行评测套件', suite=suite_name)
    # TODO: 执行评测流程并生成报告
    return {'suite': suite_name, 'status': 'completed'}
