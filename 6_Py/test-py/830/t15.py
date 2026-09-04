import asyncio

class AsyncDataFetcher:
    def __init__(self, count: int):
        self.count = count
        self.current = 0

    def __aiter__(self):
        # 返回自身，无需是 async 函数
        return self

    async def __anext__(self):
        # 必须是异步函数，允许内部使用 await
        if self.current >= self.count:
            # 耗尽时抛出 StopAsyncIteration，通知 async for 终止
            raise StopAsyncIteration

        # 模拟非阻塞的网络/磁盘 I/O 等待
        await asyncio.sleep(1)

        self.current += 1
        return self.current

async def main():

    iterator = AsyncDataFetcher(5).__aiter__()
    while True:
        try:
            item = await iterator.__anext__()
        except StopAsyncIteration:
            break
        print(item)

asyncio.run(main())

