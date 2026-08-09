from datetime import datetime

from mcp.server.fastmcp import FastMCP

mcp = FastMCP("demo-server")

_todos: list[str] = []


@mcp.tool()
def add(a: int, b: int) -> int:
    """两数相加，返回它们的和"""
    return a + b


@mcp.tool()
def get_current_time() -> str:
    """获取当前的本地时间"""
    return datetime.now().astimezone().isoformat()


@mcp.tool()
def add_todo(item: str) -> str:
    """新增一条待办事项，保存在服务器内存中"""
    _todos.append(item)
    return f"已添加待办: {item}，当前共 {len(_todos)} 条"


@mcp.tool()
def list_todos() -> list[str]:
    """列出服务器内存中保存的所有待办事项"""
    return list(_todos)


if __name__ == "__main__":
    mcp.run()
