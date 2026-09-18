"""
API 依赖注入

提供当前用户、租户、数据库会话等 FastAPI 依赖项。
"""

from collections.abc import Generator

from fastapi import Header, HTTPException

from app.core import security
from app.core.exceptions import UnauthorizedException


def get_db() -> Generator:
    """获取数据库会话（需在 app/core/database.py 实现后启用）"""
    # db = SessionLocal()
    # try:
    #     yield db
    # finally:
    #     db.close()
    raise NotImplementedError('数据库会话未初始化')
    yield  # placeholder


async def get_current_user(
    authorization: str | None = Header(None),
) -> dict:
    """获取当前登录用户信息"""
    if not authorization or not authorization.startswith('Bearer '):
        raise UnauthorizedException('缺少令牌')

    token = authorization.split(' ')[1]
    payload = security.decode_token(token)
    if not payload:
        raise UnauthorizedException('令牌无效或已过期')

    return payload


async def get_tenant_id(
    x_tenant_id: str | None = Header(None),
) -> int:
    """获取当前租户 ID"""
    if not x_tenant_id:
        raise HTTPException(status_code=400, detail='缺少租户标识 X-Tenant-Id')
    return int(x_tenant_id)


async def get_community_id(
    x_community_id: str | None = Header(None),
) -> int | None:
    """获取当前社区 ID（可选）"""
    if x_community_id:
        return int(x_community_id)
    return None
