"""
LLM 网关

统一管理多模型供应商的调用，支持路由、重试、限流和成本计量。
"""

from collections.abc import AsyncGenerator
from typing import Any

from app.core.config import settings
from app.core.logger import get_logger

logger = get_logger(__name__)


class LLMGateway:
    """LLM 网关 - 统一模型调用入口"""

    def __init__(self):
        self._provider = settings.LLM_PROVIDER
        self._default_model = settings.OPENAI_MODEL
        self._clients = {}

    async def chat(
        self,
        messages: list[dict[str, str]],
        model: str | None = None,
        temperature: float = 0.7,
        stream: bool = False,
        **kwargs,
    ) -> dict[str, Any]:
        """
        调用大模型对话接口

        Args:
            messages: 消息列表 [{"role": "user", "content": "..."}]
            model: 模型名称，缺省使用默认模型
            temperature: 温度参数
            stream: 是否流式输出
            **kwargs: 其他参数

        Returns:
            模型响应
        """
        model = model or self._default_model
        # TODO: 初始化客户端，调用 AI SDK
        logger.info('调用LLM', provider=self._provider, model=model)
        return {'role': 'assistant', 'content': 'LLM 网关待实现'}

    async def chat_stream(
        self,
        messages: list[dict[str, str]],
        model: str | None = None,
        temperature: float = 0.7,
        **kwargs,
    ) -> AsyncGenerator[str, None]:
        """
        流式调用大模型

        Args:
            messages: 消息列表
            model: 模型名称
            temperature: 温度参数

        Yields:
            文本片段
        """
        model = model or self._default_model
        # TODO: 实现流式调用
        yield 'LLM 网关流式功能待实现'

    async def count_tokens(self, messages: list[dict[str, str]]) -> int:
        """统计消息的 token 数"""
        # TODO: 使用 tiktoken 统计
        return 0
