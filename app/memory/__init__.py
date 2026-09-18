"""
记忆层

提供会话历史管理、摘要压缩、长期画像等能力。
"""

from app.memory.service import MemoryService

memory_service = MemoryService()

__all__ = ['memory_service']
