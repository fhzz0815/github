"""
工具层 - 受控业务函数与注册表

提供智能体可调用的业务工具，所有工具经过权限校验和幂等处理。
"""

from app.tools.registry import ToolRegistry

tool_registry = ToolRegistry()

__all__ = ['tool_registry']
