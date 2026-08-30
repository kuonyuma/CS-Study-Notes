"""
练习 2：查看当前任务和全部任务：
1. 在 worker() 中调用 asyncio.current_task()。
2. 输出当前任务的名称。
3. 在 main() 中创建三个任务。
4. 调用 asyncio.all_tasks()，输出所有未完成任务的：
task.get_name()
task.done()
5. 观察结果中是否包含执行 main() 的任务。
6. 等三个任务结束后，再次调用 all_tasks()。
"""

import asyncio


async def worker(name: str, delay: float) -> str:
    print(f"{name}任务开始")
    task = asyncio.current_task()
    task_name = task.get_name()  if task else "无名氏"
    print(task_name)
    await asyncio.sleep(delay)
    print(f"{name}任务结束")
    return f"{name}-result"

def print_all_tasks():
    tasks = asyncio.all_tasks()
    for i in tasks:
        print(f"任务名称{i.get_name()}，是否完成{i.done()}")

async def main():
    task_list = [
        asyncio.create_task(worker('a',1),name="worker-A"),
        asyncio.create_task(worker('b',2),name="worker-B"),
        asyncio.create_task(worker('c',3),name="worker-C")
    ]

    print_all_tasks()

    await asyncio.gather(*task_list)

    print_all_tasks()

asyncio.run(main())

