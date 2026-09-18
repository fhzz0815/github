"""
AI 相关数据模型

包含 AI 会话、消息、调用记录、审计日志等模型。
"""

from datetime import datetime

from sqlalchemy import JSON, BigInteger, DateTime, Integer, SmallInteger, String, Text
from sqlalchemy.orm import Mapped, mapped_column

from app.models.base import Base, TenantMixin, TimestampMixin


class AiSession(Base, TenantMixin, TimestampMixin):
    """AI 会话表"""

    __tablename__ = 'ai_session'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    session_id: Mapped[str] = mapped_column(
        String(64), unique=True, nullable=False, comment='会话唯一标识'
    )
    user_id: Mapped[int] = mapped_column(Integer, nullable=False, comment='用户ID')
    title: Mapped[str] = mapped_column(String(128), comment='会话标题')
    agent_type: Mapped[str] = mapped_column(String(32), comment='智能体类型')
    status: Mapped[int] = mapped_column(SmallInteger, default=1, comment='状态: 1=进行中, 2=已结束')
    total_messages: Mapped[int] = mapped_column(Integer, default=0, comment='消息总数')
    total_tokens: Mapped[int] = mapped_column(BigInteger, default=0, comment='消耗总token数')


class AiMessage(Base, TenantMixin, TimestampMixin):
    """AI 消息表"""

    __tablename__ = 'ai_message'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    session_id: Mapped[str] = mapped_column(String(64), nullable=False, comment='所属会话ID')
    role: Mapped[str] = mapped_column(
        String(16), nullable=False, comment='角色: user/assistant/system/tool'
    )
    content: Mapped[str] = mapped_column(Text, comment='消息内容')
    tool_calls: Mapped[dict] = mapped_column(JSON, comment='工具调用信息')
    tool_call_id: Mapped[str] = mapped_column(String(64), comment='工具调用ID')
    token_count: Mapped[int] = mapped_column(Integer, default=0, comment='消息token数')


class AiTrace(Base, TenantMixin):
    """AI 调用链路记录表"""

    __tablename__ = 'ai_trace'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    trace_id: Mapped[str] = mapped_column(String(64), unique=True, nullable=False, comment='追踪ID')
    session_id: Mapped[str] = mapped_column(String(64), comment='会话ID')
    user_id: Mapped[int] = mapped_column(Integer, comment='用户ID')
    agent_type: Mapped[str] = mapped_column(String(32), comment='智能体类型')
    model: Mapped[str] = mapped_column(String(64), comment='使用的模型')
    prompt_tokens: Mapped[int] = mapped_column(Integer, default=0, comment='提示token数')
    completion_tokens: Mapped[int] = mapped_column(Integer, default=0, comment='生成token数')
    total_cost: Mapped[float] = mapped_column(comment='调用成本')
    duration_ms: Mapped[int] = mapped_column(Integer, comment='耗时（毫秒）')
    status: Mapped[str] = mapped_column(String(16), comment='状态: success/failed')
    error_message: Mapped[str] = mapped_column(Text, comment='错误信息')
    created_at: Mapped[datetime] = mapped_column(DateTime, nullable=False, comment='创建时间')


class AiAuditLog(Base, TenantMixin, TimestampMixin):
    """AI 审计日志表"""

    __tablename__ = 'ai_audit_log'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    trace_id: Mapped[str] = mapped_column(String(64), comment='关联追踪ID')
    action: Mapped[str] = mapped_column(String(32), nullable=False, comment='操作类型')
    prompt_version: Mapped[str] = mapped_column(String(32), comment='提示词模板版本')
    model: Mapped[str] = mapped_column(String(64), comment='模型名称')
    input_summary: Mapped[str] = mapped_column(String(256), comment='输入摘要')
    output_summary: Mapped[str] = mapped_column(String(256), comment='输出摘要')
    result: Mapped[str] = mapped_column(String(16), comment='结果: pass/filtered/rejected')
