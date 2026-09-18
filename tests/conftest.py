"""
pytest 共享配置

定义 fixtures 和全局测试配置。
"""

import asyncio
from collections.abc import AsyncGenerator, Generator

import pytest
from fastapi import FastAPI
from httpx import ASGITransport, AsyncClient


@pytest.fixture(scope='session')
def event_loop() -> Generator:
    """创建事件循环实例"""
    loop = asyncio.new_event_loop()
    yield loop
    loop.close()


@pytest.fixture
def app() -> FastAPI:
    """获取 FastAPI 应用实例"""
    from app.main import app

    return app


@pytest.fixture
async def async_client(app: FastAPI) -> AsyncGenerator:
    """创建异步 HTTP 测试客户端"""
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url='http://test') as client:
        yield client


@pytest.fixture
def sample_tenant_headers() -> dict:
    """模拟租户请求头"""
    return {
        'X-Tenant-Id': '1',
        'X-Community-Id': '1',
        'Authorization': 'Bearer test-token',
    }
