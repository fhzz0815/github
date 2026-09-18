"""
异步任务层 - Celery 任务

提供异步和定时任务，如知识库索引、AI 成本对账、评测等。
"""

from app.tasks.celery_app import celery_app

__all__ = ['celery_app']
