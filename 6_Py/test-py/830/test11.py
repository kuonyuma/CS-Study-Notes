"""
练习 7：第一个完成的任务获胜
使用 asyncio.wait() 和：
return_when=asyncio.FIRST_COMPLETED
模拟同时请求三个服务器：
server-A：1.5 秒返回
server-B：0.4 秒返回
server-C：2 秒返回
要求：
1. 获取最先完成的任务。
2. 输出获胜服务器的名称和结果。
3. 取消其他任务。
4. 等待其他任务完成取消清理。
5. 不能假定 done 中永远只有一个任务。
6. 如果多个任务在同一轮事件循环中完成，需要处理所有 done 任务。
进阶要求：
- 将某个服务器改成抛出异常。
- 只有正常完成的任务才能成为获胜者。
- 如果先完成的任务失败，应继续等待其他任务。
"""

import asyncio


async def server(name, time):
    print(f"服务器{name}响应中...")
    await asyncio.sleep(time)
    return f"服务器:{name}-response"


async def fail():
    print("服务器fail响应中...")
    await asyncio.sleep(0.5)
    raise ValueError("服务器返回 503")


async def main():

    pending = [
        asyncio.create_task(server("a", 1), name="A"),
        asyncio.create_task(server("b", 2), name="B"),
        asyncio.create_task(server("c", 3), name="C"),
        asyncio.create_task(fail(), name="fail"),
    ]
    winner = None
    while pending:
        done, pending = await asyncio.wait(
            pending,
            return_when=asyncio.FIRST_COMPLETED,
        )

        for task in done:
            exc = task.exception()
            if exc:
                pass
            else:
                winner = task
                name = task.get_name()
                result = task.result()
                print(f"最先相应的服务器是{name}.结果为{result}")
                break
        if winner != None:
            break
    for task in pending:
        task.cancel()
    await asyncio.gather(
        *pending,
        return_exceptions=True,
    )


asyncio.run(main())
