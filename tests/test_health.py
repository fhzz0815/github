"""
健康检查接口测试
"""

import pytest
from httpx import AsyncClient


@pytest.mark.asyncio
async def test_health_check(async_client: AsyncClient):
    """测试健康检查接口"""
    response = await async_client.get('/health')
    assert response.status_code == 200
    data = response.json()
    assert data['status'] == 'ok'
    assert 'version' in data


@pytest.mark.asyncio
async def test_health_check_method():
    """验证方法存在"""
    from app.main import health_check

    assert callable(health_check)
