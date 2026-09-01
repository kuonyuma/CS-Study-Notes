import asyncio


async def worker():
    await asyncio.sleep(10)


async def main():
    task = asyncio.create_task(worker())

    task.cancel()

    print(task.cancelled())
    # 此时可能还没有真正处理取消请求，因此不应依赖这里立即为 True

    try:
        await task
    except asyncio.CancelledError:
        pass

    print(task.cancelled())
    # True


asyncio.run(main())
