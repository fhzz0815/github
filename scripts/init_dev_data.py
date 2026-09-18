#!/usr/bin/env python3
"""
开发环境数据初始化脚本

创建初始租户、管理员账号和示例数据。
"""
from app.core.config import settings
from app.core.logger import get_logger
from app.core.security import hash_password

logger = get_logger(__name__)


def init_dev_data():
    """初始化开发数据"""
    logger.info('开始初始化开发数据...')

    # TODO: 创建默认租户
    # TODO: 创建管理员账号
    # TODO: 创建示例社区、楼宇、房屋
    # TODO: 创建示例知识库文档

    logger.info('开发数据初始化完成')


if __name__ == '__main__':
    init_dev_data()
