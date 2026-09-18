"""
统一异常处理

将业务异常映射到标准 HTTP 错误响应。
"""

from fastapi import Request
from fastapi.responses import JSONResponse
from structlog import get_logger

from app.core.exceptions import AppException

logger = get_logger()


async def app_exception_handler(request: Request, exc: AppException) -> JSONResponse:
    """处理业务异常，返回统一格式的错误响应"""
    logger.warning(
        '业务异常',
        code=exc.code,
        message=exc.message,
        path=str(request.url),
        status_code=exc.status_code,
    )
    return JSONResponse(
        status_code=exc.status_code,
        content={
            'code': exc.code,
            'message': exc.message,
            'details': exc.details,
        },
    )


async def unhandled_exception_handler(request: Request, exc: Exception) -> JSONResponse:
    """处理未预期异常，返回 500"""
    logger.error(
        '未预期异常',
        error=str(exc),
        path=str(request.url),
        exc_info=True,
    )
    return JSONResponse(
        status_code=500,
        content={
            'code': 'INTERNAL_ERROR',
            'message': '服务器内部错误',
        },
    )
