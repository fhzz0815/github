"""
对话相关数据契约
"""

from typing import Any

from pydantic import BaseModel, Field


class MessageSchema(BaseModel):
    """消息模型"""

    role: str = Field(description='角色: user/assistant/system/tool')
    content: str | None = Field(default=None, description='消息内容')
    tool_calls: list[dict[str, Any]] | None = Field(default=None, description='工具调用')
    tool_call_id: str | None = Field(default=None, description='工具调用ID')


class ChatRequest(BaseModel):
    """对话请求"""

    session_id: str | None = Field(default=None, description='会话ID，新会话可不传')
    messages: list[MessageSchema] = Field(description='消息列表')
    stream: bool = Field(default=False, description='是否流式输出')
    temperature: float = Field(default=0.7, ge=0, le=2, description='温度参数')
    model: str | None = Field(default=None, description='模型名称')


class ChatResponse(BaseModel):
    """对话响应"""

    session_id: str = Field(description='会话ID')
    message: MessageSchema = Field(description='响应消息')
    usage: dict[str, int] | None = Field(default=None, description='Token使用情况')
