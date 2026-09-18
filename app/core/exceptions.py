"""
统一异常定义

定义业务异常基类和具体异常类型，配合 GlobalExceptionHandler 统一返回格式。
"""

from typing import Any


class AppException(Exception):
    """业务异常基类"""

    def __init__(
        self,
        code: str,
        message: str,
        status_code: int = 400,
        details: dict[str, Any] | None = None,
    ):
        self.code = code
        self.message = message
        self.status_code = status_code
        self.details = details or {}
        super().__init__(self.message)


class NotFoundException(AppException):
    """资源不存在"""

    def __init__(self, resource: str, resource_id: Any = None):
        msg = f'{resource}不存在'
        if resource_id:
            msg += f' (id={resource_id})'
        super().__init__(code='NOT_FOUND', message=msg, status_code=404)


class UnauthorizedException(AppException):
    """未授权"""

    def __init__(self, message: str = '未登录或令牌已失效'):
        super().__init__(code='UNAUTHORIZED', message=message, status_code=401)


class ForbiddenException(AppException):
    """权限不足"""

    def __init__(self, message: str = '权限不足，无法执行此操作'):
        super().__init__(code='FORBIDDEN', message=message, status_code=403)


class ValidationException(AppException):
    """参数校验失败"""

    def __init__(self, message: str, details: dict[str, Any] | None = None):
        super().__init__(
            code='VALIDATION_ERROR',
            message=message,
            status_code=422,
            details=details,
        )


class BusinessException(AppException):
    """通用业务异常"""

    def __init__(self, code: str, message: str, details: dict[str, Any] | None = None):
        super().__init__(code=code, message=message, status_code=400, details=details)


class TenantIsolationException(AppException):
    """租户隔离违规"""

    def __init__(self, message: str = '租户数据访问违规'):
        super().__init__(
            code='TENANT_ISOLATION_ERROR',
            message=message,
            status_code=403,
        )


class AiQuotaExceededException(AppException):
    """AI 配额超限"""

    def __init__(self, message: str = 'AI 调用配额已超限'):
        super().__init__(
            code='AI_QUOTA_EXCEEDED',
            message=message,
            status_code=429,
        )
