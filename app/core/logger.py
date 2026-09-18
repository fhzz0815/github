"""
日志配置模块

统一使用 structlog 进行结构化日志记录。
开发环境输出可读格式，生产环境输出 JSON 格式。
"""

import logging
import sys

import structlog

from app.core.config import settings


def setup_logging():
    """初始化全局日志配置"""
    log_level = getattr(logging, settings.LOG_LEVEL.upper(), logging.DEBUG)

    # 配置 structlog
    structlog.configure(
        processors=[
            structlog.stdlib.filter_by_level,
            structlog.stdlib.add_logger_name,
            structlog.stdlib.add_log_level,
            structlog.stdlib.PositionalArgumentsFormatter(),
            structlog.processors.TimeStamper(fmt='iso'),
            structlog.processors.StackInfoRenderer(),
            structlog.processors.format_exc_info,
            structlog.processors.UnicodeDecoder(),
            # 生产环境输出 JSON，开发环境输出彩色可读格式
            structlog.dev.ConsoleRenderer()
            if settings.APP_ENV == 'dev'
            else structlog.processors.JSONRenderer(),
        ],
        wrapper_class=structlog.stdlib.BoundLogger,
        context_class=dict,
        logger_factory=structlog.stdlib.LoggerFactory(),
        cache_logger_on_first_use=True,
    )

    # 设置 root logger 级别
    logging.basicConfig(
        format='%(message)s',
        stream=sys.stdout,
        level=log_level,
    )


# 导出手动创建 logger 的函数，方便各模块使用
def get_logger(name: str | None = None):
    """获取结构化日志器"""
    return structlog.get_logger(name or __name__)
