import asyncio


async def main():
    try:
        async with asyncio.timeout(None) as timeout_context:
            print("刚进入上下文")
            print("当前截止时间:", timeout_context.when())
            # None，说明暂时没有超时时间

            loop = asyncio.get_running_loop()

            # 假设现在才知道需要限制 2 秒
            deadline = loop.time() + 2
            timeout_context.reschedule(deadline)

            print("新的截止时间:", timeout_context.when())

            await asyncio.sleep(5)

    except TimeoutError:
        print("操作超时")
        print("是否确实超过了期限:", timeout_context.expired())


asyncio.run(main())