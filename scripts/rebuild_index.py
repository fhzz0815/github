#!/usr/bin/env python3
"""
知识库索引重建脚本

用法:
    python scripts/rebuild_index.py --tenant 1
    python scripts/rebuild_index.py --tenant all
"""
import argparse

from app.core.config import settings
from app.core.logger import get_logger

logger = get_logger(__name__)


def rebuild_index(tenant_id: str):
    """重建指定租户的知识库索引"""
    logger.info('开始重建知识库索引', tenant_id=tenant_id)
    # TODO: 遍历文档重新向量化
    logger.info('知识库索引重建完成', tenant_id=tenant_id)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='重建知识库索引')
    parser.add_argument('--tenant', type=str, required=True, help='租户ID 或 "all"')
    args = parser.parse_args()
    rebuild_index(args.tenant)
