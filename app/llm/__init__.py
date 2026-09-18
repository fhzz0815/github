"""
模型层 - LLM 网关

提供多模型路由、调用、重试、计量、过滤等能力。
统一入口，屏蔽底层模型差异。
"""

from app.llm.gateway import LLMGateway

llm_gateway = LLMGateway()

__all__ = ['llm_gateway']
