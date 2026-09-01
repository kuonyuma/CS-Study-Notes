import asyncio


async def worker():
    try:
        await asyncio.sleep(10)
        return "完成"
    except asyncio.CancelledError:
        print("worker 收到了取消请求")
        raise
    finally:
        print("执行清理工作")


async def main():
    task = asyncio.create_task(worker())

    await asyncio.sleep(0.1)

    accepted = task.cancel()
    print("是否成功发出取消请求:", accepted)

    try:
        print(f"任务是否已经停止{task.cancelled()}")
        await task
    except asyncio.CancelledError:
        print(f"任务已经被取消{task.cancelled()}")


asyncio.run(main())
