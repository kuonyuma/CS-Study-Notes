"""
练习 8：使用 as_completed() 显示实时进度
模拟下载五个文件：
downloads = {
    "a.zip": 2.0,
    "b.zip": 0.4,
    "c.zip": 1.2,
    "d.zip": 0.8,
    "e.zip": 3.0,
}
要求：
1. 为每个下载创建有名称的 Task。
2. 使用 asyncio.as_completed() 按完成顺序处理。
3. 每完成一个任务，输出：
进度：2/5，b.zip 下载完成
4. 设置整体超时为 1.5 秒。
5. 捕获 TimeoutError。
6. 取消所有未完成任务。
7. 等待取消清理结束。
8. 最终输出：
成功：3
失败：0
取消：2
实际数量应由程序计算，不能写死。
"""

import asyncio


async def download(name, time):
    print(f"文件{name}下载中...")
    await asyncio.sleep(time)
    return f"文件:{name}-response"


async def fail():
    print("服务器fail响应中...")
    await asyncio.sleep(0.5)
    raise ValueError("服务器返回 503")


async def main():
    downloads = [
        asyncio.create_task(download("a.zip", 2)),
        asyncio.create_task(download("b.zip", 4)),
        asyncio.create_task(download("c.zip", 1.2)),
        asyncio.create_task(download("d.zip", 0.8)),
        asyncio.create_task(download("e.zip", 3)),
    ]
    try:
        async for task in asyncio.as_completed(downloads,timeout=1.5):
            result = await task
            exc = task.exception()
            if exc:
                pass
            else:
                print(f"{task.get_name()}结果{result}")
    except Exception as e:
        pass
    
asyncio.run(main())
