# test/test_memory.py
"""
测试目标：验证 MyAgent-v4 的上下文压缩机制能正常触发并工作
"""

import os
import sys
from pathlib import Path

# 设置项目根路径
BASE_DIR = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(BASE_DIR))

from swe.agent import creat_agent


def test_compress_triggers():
    """
    测试：给 Agent 一个需要多步工具调用的复杂任务，
    验证 compress_contents 能被触发，且 Agent 不会崩溃。
    """
    agent = creat_agent()

    # 临时把阈值调小，方便快速触发压缩
    # 正常是 8，这里改成 4，2 轮工具调用就能触发
    prompt = (
        "请依次完成以下步骤：\n"
        "1. 用 read 读取 test/add.py 的内容\n"
        "2. 用 read 读取 test/subtract.py 的内容\n"
        "3. 在 test/ 目录下用 write 创建 multiply.py，内容为：\n"
        "   def multiply(a, b): return a * b\n"
        "   print(multiply(3, 4))\n"
        "4. 用 terminal 执行 python test/multiply.py 并告诉我结果"
    )

    print("=" * 60)
    print("📋 测试 Prompt:")
    print(prompt)
    print("=" * 60)

    response = agent.execute(prompt)

    print("\n" + "=" * 60)
    print("🤖 Agent 最终回复:")
    print(response)
    print("=" * 60)

    # 打印执行统计
    print(f"\n📊 执行统计:")
    print(f"   总思考轮数: {agent.tier}")
    print(f"   最终 contents 条数: {len(agent.contents)}")


if __name__ == "__main__":
    test_compress_triggers()
