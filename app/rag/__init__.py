"""
检索增强生成层

提供文档解析、文本切片、向量化、检索和重排等能力。
"""

from app.rag.service import RAGService

rag_service = RAGService()

__all__ = ['rag_service']
