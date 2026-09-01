"""
练习 6：使用 wait() 实现超时控制
创建三个 Task：
A：0.5 秒完成
B：2 秒完成
C：3 秒完成
然后执行：
done, pending = await asyncio.wait(
    tasks,
    timeout=1,
)
要求：
1. 输出 done 和 pending 的数量。
2. 输出已完成任务的结果。
3. 输出未完成任务的名称。
4. 取消所有 pending 任务。
5. 等待取消过程真正结束。
6. 最终验证每个 Task 的：
task.done()
task.cancelled()
限制：
- 不能在调用 cancel() 后立即结束程序。
- 处理取消结果时，不能让 CancelledError 导致程序异常退出。
"""

import asyncio


async def worker(name, time):
    await asyncio.sleep(time)
    print(f"{name}执行完毕")
    return f"{name}-reuslt"


async def main():
    task_list = {
        asyncio.create_task(worker("A", 0.5)),
        asyncio.create_task(worker("B", 2)),
        asyncio.create_task(worker("c", 3)),
        asyncio.create_task(worker("d", 4)),
        asyncio.create_task(worker("e", 5)),
    }

    done,pending = await asyncio.wait(
        task_list,
        timeout=3,
    )

    for task in done:
        print(task.result)
    for task in pending:
        print(task.cancel)

asyncio.run(main())