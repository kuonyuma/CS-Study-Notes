import asyncio


async def main():
    try:
        async with asyncio.timeout(2):
            try:
                await asyncio.sleep(5)
            except TimeoutError:
                print("超时异常捕获失败")
            except asyncio.CancelledError:
                print("捕获到取消异常")
    except TimeoutError:
        print("操作超时")


asyncio.run(main())
