"""
Alembic 迁移环境配置

根据运行环境动态加载数据库 URL 和目标元数据。
"""
import sys
from pathlib import Path

from alembic import context
from sqlalchemy import engine_from_config, pool

# 将项目根目录加入 sys.path
sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from app.core.config import settings
from app.models import Base

# 导入所有模型以确保 Base.metadata 包含全部表
import app.models.community  # noqa: F401
import app.models.ai  # noqa: F401

target_metadata = Base.metadata


def run_migrations_offline():
    """离线模式运行迁移（仅生成 SQL 脚本）"""
    url = settings.DATABASE_URL
    context.configure(
        url=url,
        target_metadata=target_metadata,
        literal_binds=True,
        dialect_opts={'paramstyle': 'named'},
    )

    with context.begin_transaction():
        context.run_migrations()


def run_migrations_online():
    """在线模式运行迁移（直接执行到数据库）"""
    configuration = config.get_section(config.config_ini_section)
    configuration['sqlalchemy.url'] = settings.DATABASE_URL
    connectable = engine_from_config(
        configuration,
        prefix='sqlalchemy.',
        poolclass=pool.NullPool,
    )

    with connectable.connect() as connection:
        context.configure(
            connection=connection,
            target_metadata=target_metadata,
        )

        with context.begin_transaction():
            context.run_migrations()


# alembic 上下文变量
from alembic.config import Config as AlembicConfig  # noqa: E402

config: AlembicConfig = context.config  # type: ignore

if context.is_offline_mode():
    run_migrations_offline()
else:
    run_migrations_online()
