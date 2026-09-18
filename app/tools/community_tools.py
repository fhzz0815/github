"""
社区管理相关工具

提供给智能体操作社区数据的业务工具。
"""

from app.tools.registry import ToolInfo, ToolRegistry


def register_community_tools(registry: ToolRegistry):
    """注册社区管理工具"""
    # TODO: 实现真实的业务逻辑

    async def query_community_info(community_id: int) -> dict:
        """
        查询社区基本信息
        - 根据社区ID查询社区名称、地址、物业公司等基础信息
        """
        # TODO: 从数据库查询
        return {'community_id': community_id, 'name': '示例社区'}

    registry.register(ToolInfo(
        name='query_community_info',
        description='查询社区基本信息，包括社区名称、地址、物业公司等',
        func=query_community_info,
        parameters={
            'type': 'object',
            'properties': {
                'community_id': {
                    'type': 'integer',
                    'description': '社区ID',
                },
            },
            'required': ['community_id'],
        },
        required_permissions=['community:query'],
    ))  # fmt: skip

    # TODO: 注册更多社区管理工具
