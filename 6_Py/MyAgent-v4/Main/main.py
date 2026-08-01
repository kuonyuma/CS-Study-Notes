import sys
import asyncio
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent
if str(BASE_DIR) not in sys.path:
    sys.path.append(str(BASE_DIR))


from llm.client import get_client, get_model_name
from swe.agent import Agent
from Tools.read import read as read_file
from Tools.write import write as write_file
from Tools.terminal import terminal


async def main(prompt: str):

    sys.stdout.write("llm响应中...\n")
    sys.stdout.flush()

    client = get_client()
    agent = Agent(client, model_name=get_model_name())

    agent.register(read_file)
    agent.register(write_file)
    agent.register(terminal)

    sys.stdout.write("agent完成注册...\n")
    sys.stdout.flush()
    try:
        async for event in agent.execute(prompt):
            if event.type == "text":
                sys.stdout.write(f"{event.text}")

            elif event.type == "tool_use":
                sys.stdout.write(f"\n[工具] agent即将使用: {event.name}\n")

            elif event.type == "error":
                sys.stdout.write(f"\n[错误] {event.text or 'agent发生了严重错误'}\n")

            elif event.type == "end":
                sys.stdout.write("\n任务完成")
                if event.result:
                    used = event.result.used
                    sys.stdout.write(
                        f" (tokens: {used.get('input_tokens', '?')} in / "
                        f"{used.get('output_tokens', '?')} out, "
                        f"stop: {event.result.stop_reason})"
                    )
                sys.stdout.write("\n")
            sys.stdout.flush()
    except KeyboardInterrupt:
        print("\n\n🛑 用户手动终止了任务。")
    except Exception as e:
        print(f"\n\n💥 发生未预期的异常: {e}")


if __name__ == "__main__":
    # 使用 asyncio.run 来启动异步主函数
    sys.stdout.write("请输入:")
    sys.stdout.flush()
    prompt = input()
    asyncio.run(main(prompt=prompt))
