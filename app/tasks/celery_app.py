"""
Celery 应用配置

初始化 Celery 应用，注册任务模块。
"""

from celery import Celery

from app.core.config import settings

celery_app = Celery(
    'smart_community',
    broker=settings.CELERY_BROKER_URL,
    backend=settings.CELERY_RESULT_BACKEND,
    include=[
        'app.tasks.knowledge_tasks',
        'app.tasks.eval_tasks',
    ],
)

celery_app.conf.update(
    task_serializer='json',
    accept_content=['json'],
    result_serializer='json',
    timezone='Asia/Shanghai',
    enable_utc=True,
    task_track_started=True,
    task_acks_late=True,
    worker_prefetch_multiplier=1,
    beat_schedule={
        'reindex-knowledge': {
            'task': 'app.tasks.knowledge_tasks.reindex_knowledge',
            'schedule': 3600,  # 每小时
        },
        'sync-ai-cost': {
            'task': 'app.tasks.eval_tasks.sync_ai_cost',
            'schedule': 86400,  # 每天
        },
    },
)
