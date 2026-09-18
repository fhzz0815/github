"""
安全与鉴权模块

提供 JWT 令牌生成/验证、密码哈希、租户上下文管理等能力。
"""

from datetime import UTC, datetime, timedelta
from typing import Any

from jose import JWTError, jwt
from passlib.context import CryptContext

from app.core.config import settings

# 密码加密上下文
pwd_context = CryptContext(schemes=['bcrypt'], deprecated='auto')


def hash_password(password: str) -> str:
    """对密码进行哈希处理"""
    return pwd_context.hash(password)


def verify_password(plain_password: str, hashed_password: str) -> bool:
    """验证密码与哈希是否匹配"""
    return pwd_context.verify(plain_password, hashed_password)


def create_access_token(
    data: dict[str, Any],
    expires_delta: timedelta | None = None,
) -> str:
    """创建 JWT 访问令牌"""
    to_encode = data.copy()
    expire = datetime.now(UTC) + (
        expires_delta or timedelta(seconds=settings.JWT_ACCESS_TOKEN_EXPIRE_SECONDS)
    )
    to_encode.update(
        {
            'exp': expire,
            'iat': datetime.now(UTC),
            'jti': str(hash(str(data))),  # 简化版 jti，生产环境应使用 UUID
        }
    )
    return jwt.encode(to_encode, settings.JWT_SECRET_KEY, algorithm='HS256')


def create_refresh_token(
    data: dict[str, Any],
    expires_delta: timedelta | None = None,
) -> str:
    """创建 JWT 刷新令牌"""
    to_encode = data.copy()
    expire = datetime.now(UTC) + (
        expires_delta or timedelta(seconds=settings.JWT_REFRESH_TOKEN_EXPIRE_SECONDS)
    )
    to_encode.update(
        {
            'exp': expire,
            'iat': datetime.now(UTC),
        }
    )
    return jwt.encode(to_encode, settings.JWT_SECRET_KEY, algorithm='HS256')


def decode_token(token: str) -> dict[str, Any] | None:
    """解码并验证 JWT 令牌"""
    try:
        payload = jwt.decode(token, settings.JWT_SECRET_KEY, algorithms=['HS256'])
        return payload
    except JWTError:
        return None
