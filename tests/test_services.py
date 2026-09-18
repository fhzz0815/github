"""
服务层单元测试
"""

import pytest

from app.services.ai_service import AiService


@pytest.mark.asyncio
class TestAiService:
    """AI 服务测试"""

    async def test_chat_returns_response(self):
        """测试对话接口返回正确格式"""
        service = AiService()
        result = await service.chat(
            messages=[{'role': 'user', 'content': '你好'}],
            tenant_id=1,
            user_id=1,
        )
        assert 'session_id' in result
        assert 'message' in result
        assert result['message']['role'] == 'assistant'
