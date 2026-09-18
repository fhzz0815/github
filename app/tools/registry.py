"""
工具注册表

管理所有可被智能体调用的工具，提供注册、查找、执行等能力。
"""

from collections.abc import Callable
from typing import Any


class ToolInfo:
    """工具元信息"""

    def __init__(
        self,
        name: str,
        description: str,
        func: Callable,
        parameters: dict[str, Any],
        required_permissions: list[str] | None = None,
        is_idempotent: bool = False,
    ):
        self.name = name
        self.description = description
        self.func = func
        self.parameters = parameters
        self.required_permissions = required_permissions or []
        self.is_idempotent = is_idempotent

    def to_openai_schema(self) -> dict[str, Any]:
        """转换为 OpenAI Function Calling 格式"""
        return {
            'type': 'function',
            'function': {
                'name': self.name,
                'description': self.description,
                'parameters': self.parameters,
            },
        }


class ToolRegistry:
    """工具注册表 - 单例"""

    def __init__(self):
        self._tools: dict[str, ToolInfo] = {}

    def register(self, tool_info: ToolInfo):
        """注册工具"""
        self._tools[tool_info.name] = tool_info

    def get(self, name: str) -> ToolInfo | None:
        """获取工具"""
        return self._tools.get(name)

    def list_tools(self) -> list[ToolInfo]:
        """获取所有工具列表"""
        return list(self._tools.values())

    def get_openai_tools(self) -> list[dict[str, Any]]:
        """获取所有工具的 OpenAI Function Calling 格式"""
        return [t.to_openai_schema() for t in self._tools.values()]

    def execute(self, name: str, **kwargs) -> Any:
        """执行指定工具"""
        tool = self.get(name)
        if not tool:
            raise ValueError(f'工具 "{name}" 不存在')
        return tool.func(**kwargs)
