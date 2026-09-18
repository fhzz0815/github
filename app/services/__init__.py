"""
领域服务层

提供业务编排与事务边界，协调多个模型完成业务操作。
"""

from app.services.ai_service import AiService

ai_service = AiService()

__all__ = ['ai_service']
