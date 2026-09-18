"""
智能体层 - LangGraph 图与节点

基于 LangGraph 定义智能体编排图，按业务场景拆分不同的智能体。
"""

from app.agents.base import create_base_agent_graph

__all__ = ['create_base_agent_graph']
