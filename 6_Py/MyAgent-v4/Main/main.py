import sys
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent
if str(BASE_DIR) not in sys.path:
    sys.path.append(str(BASE_DIR))


from llm.client import get_client
from swe.agent import Agent
from Tools.read import read as read_file
from Tools.write import write as write_file
from Tools.terminal import terminal
import asyncio
from llm.gemini import generate


async def main(prompt: str):

    sys.stdout.write("llm响应中...\n")

    client = get_client()
    agent = Agent(client)

    agent.register(read_file)
    agent.register(write_file)
    agent.register(terminal)

    sys.stdout.write("agent完成注册...\n")
    try:
        async for event in agent.execute(prompt):
            if event.type == "text":
                sys.stdout.write(f"{event.text}")

            elif event.type == "tool_use":
                sys.stdout.write(f"agent即将使用工具: {event.name}")

            elif event.type == "error":
                sys.stdout.write("agent发生了严重错误")

            elif event.type == "end":
                sys.stdout.write("\n任务完成")
    except KeyboardInterrupt:
        print("\n\n🛑 用户手动终止了任务。")
    except Exception as e:
        print(f"\n\n💥 发生未预期的异常: {e}")


if __name__ == "__main__":
    # 使用 asyncio.run 来启动异步主函数
    sys.stdout.write("请输入:")
    prompt = input()
    asyncio.run(main(prompt=prompt))
