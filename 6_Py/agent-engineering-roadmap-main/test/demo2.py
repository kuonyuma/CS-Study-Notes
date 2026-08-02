import asyncio
from prompt_toolkit import PromptSession
from prompt_toolkit.patch_stdout import patch_stdout


async def background_task():
    """这是一个模拟后台每隔 1.5 秒突然打印日志的任务"""
    for i in range(1, 4):
        await asyncio.sleep(1.5)
        print(f"\n[后台提醒] 滴滴滴！收到第 {i} 条新消息！")


async def test_without_patch():
    print("\n" + "=" * 50)
    print("【测试 1：不使用 patch_stdout】")
    print("请在这 5 秒内随便打字（比如输入 abcdefg...但别急着按回车）")
    print("你会发现：后台打印弹出来时，你的输入框被切断、搞乱了。")
    print("=" * 50)

    # 启动后台打印任务
    bg_task = asyncio.create_task(background_task())
    session = PromptSession()

    # 故意不加 with patch_stdout()
    try:
        ans = await session.prompt_async("请输入 > ")
        print(f"你输入了: {ans}")
    except:
        pass
    finally:
        bg_task.cancel()


async def test_with_patch():
    print("\n" + "=" * 50)
    print("【测试 2：使用 patch_stdout】")
    print("请再次随便打字（别急着按回车）")
    print("你会发现：不管后台怎么打印，你的输入框永远完好无损地悬浮在最下面。")
    print("=" * 50)

    bg_task = asyncio.create_task(background_task())
    session = PromptSession()

    # 这里加了上下文管理器保护
    try:
        with patch_stdout():
            ans = await session.prompt_async("请输入 > ")
        print(f"你输入了: {ans}")
    except:
        pass
    finally:
        bg_task.cancel()


async def main():
    await test_without_patch()
    await asyncio.sleep(1)
    await test_with_patch()
    print("\n测试结束！这就是 patch_stdout 的保护作用。")


if __name__ == "__main__":
    asyncio.run(main())
