
"""
练习 3：研究 sleep(0)
编写两个任务：
- 任务 A 循环打印 A-0 到 A-4；
- 任务 B 循环打印 B-0 到 B-4；
- 每次打印后执行 await asyncio.sleep(0)。
然后删除 await asyncio.sleep(0)，比较两次输出顺序，并解释原因。

"""

import asyncio


async def task_no_sleep(name: str):
    for i in range(5):
        print(f"{name}-{i}")
        

async def task(name: str):
    for i in range(5):
        print(f"{name}-{i}")
        await asyncio.sleep(0)


async def main():
    task_a = task_no_sleep("A")
    task_b = task_no_sleep("B")
    a = asyncio.create_task(task_a)
    b = asyncio.create_task(task_b)
    await a
    await b


if __name__  == "__main__":
    asyncio.run(main())


