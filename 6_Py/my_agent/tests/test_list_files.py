"""ListFiles 工具冒烟测试

测试场景:
  1. 正常列出当前目录内容
  2. 不存在的路径应返回错误
  3. 默认路径 (不传 path) 应列出当前目录
  4. 工具元数据验证
"""

from pathlib import Path
import sys
import asyncio

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from tools.list_files import ListFiles


async def test_list_current_dir():
    """测试: 列出项目根目录，应包含 tools/ 等已知子目录"""
    tool = ListFiles()
    project_root = str(Path(__file__).resolve().parents[1])
    result = await tool.run({"path": project_root})

    assert not result.is_error, f"列目录不应报错, 但得到: {result.content}"
    assert "tools/" in result.content, (
        f"应包含 'tools/' 子目录, 实际: {result.content}"
    )
    print("[PASS] test_list_current_dir")


async def test_nonexistent_path():
    """测试: 传入不存在的路径，应返回 is_error=True"""
    tool = ListFiles()
    result = await tool.run({"path": "/this/path/does/not/exist/at_all"})

    assert result.is_error, "不存在的路径应返回 is_error=True"
    print("[PASS] test_nonexistent_path")


async def test_default_path():
    """测试: 不传 path 参数，应默认列出当前目录且不报错"""
    tool = ListFiles()
    result = await tool.run({})

    assert not result.is_error, f"默认路径不应报错, 但得到: {result.content}"
    print("[PASS] test_default_path")


async def test_tool_metadata():
    """测试: 工具元数据应正确配置"""
    tool = ListFiles()

    assert tool.name == "ListFiles", f"name 应为 'ListFiles', 实际: {tool.name}"
    assert tool.read_only is True, "ListFiles 应是只读工具"

    declaration = tool.get_tool_message()
    assert declaration.name == "ListFiles"
    print("[PASS] test_tool_metadata")


async def main():
    print("=" * 40)
    print("ListFiles 工具冒烟测试")
    print("=" * 40)

    tests = [
        test_list_current_dir,
        test_nonexistent_path,
        test_default_path,
        test_tool_metadata,
    ]

    passed = 0
    failed = 0
    for test in tests:
        try:
            await test()
            passed += 1
        except AssertionError as e:
            print(f"[FAIL] {test.__name__}: {e}")
            failed += 1
        except Exception as e:
            print(f"[ERROR] {test.__name__}: {type(e).__name__}: {e}")
            failed += 1

    print("=" * 40)
    print(f"结果: {passed} 通过, {failed} 失败, 共 {len(tests)} 项")

    if failed > 0:
        sys.exit(1)


if __name__ == "__main__":
    asyncio.run(main())
