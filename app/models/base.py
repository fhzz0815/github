"""
基础 ORM 模型

提供公共基类、时间戳混入、多租户混入等基础能力。
"""

from datetime import datetime

from sqlalchemy import DateTime, Integer, SmallInteger, func
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column


class Base(DeclarativeBase):
    """SQLAlchemy 声明式基类"""

    pass


class TimestampMixin:
    """时间戳混入 - 提供 created_at / updated_at 字段"""

    created_at: Mapped[datetime] = mapped_column(
        DateTime,
        default=func.now(),
        nullable=False,
        comment='创建时间',
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime,
        default=func.now(),
        onupdate=func.now(),
        nullable=False,
        comment='更新时间',
    )


class TenantMixin:
    """多租户混入 - 提供 tenant_id 字段"""

    tenant_id: Mapped[int] = mapped_column(
        Integer,
        nullable=False,
        comment='租户ID（物业公司ID）',
    )


class StatusMixin:
    """状态混入 - 提供 status 字段"""

    status: Mapped[int] = mapped_column(
        SmallInteger,
        default=1,
        nullable=False,
        comment='状态: 1=启用, 0=停用',
    )
