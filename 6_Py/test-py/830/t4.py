import asyncio
from prompt_toolkit import PromptSession


async def background_worker():
    """模拟一个持续在后台运行的任务"""
    count = 1
    while True:
        await asyncio.sleep(3)
        # patch_stdout 会保证后台输出不会把用户正在打字的输入框格式打乱
        print(f"\n[后台心跳] 系统正常运行中 (第 {count} 次检测)")
        count += 1


async def main():
    session = PromptSession()

    # 启动后台任务
    asyncio.create_task(background_worker())

    while True:
        # prompt_async 等待用户输入，期间不会卡死后台协程
        user_input: str = await session.prompt_async("> ")

        if user_input.strip() == "exit":
            print("程序退出。")
            break

        print(f"收到用户指令: {user_input}")


if __name__ == "__main__":
    asyncio.run(main())
