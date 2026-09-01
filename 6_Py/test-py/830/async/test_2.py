"""
练习 2：验证异步等待不阻塞其他任务
编写两个异步任务：
- 任务 A 等待两秒；
- 任务 B 等待一秒；
- 两个任务同时开始；
- 使用 loop.time() 计算总耗时。
完成标准：
- B 先结束；
- A 后结束；
- 总耗时接近两秒，而不是三秒。
可以使用 asyncio.TaskGroup 组织两个任务。
"""

import asyncio


async def task(name: str, time: int):

    print(f"{name}开始运行")
    await asyncio.sleep(time)
    print(f"{name}开始结束")


async def main():
    loop = asyncio.get_running_loop()
    start = loop.time()
    task_a = asyncio.create_task(task("a", 2))
    task_b = asyncio.create_task(task("b", 1))
    await task_a
    await task_b
    end = loop.time() - start
    print(f"总耗时{end}")


if __name__ == "__main__":
    asyncio.run(main())
