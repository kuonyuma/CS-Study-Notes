"""
练习 5：比较 TaskGroup 和 gather()
实现三个任务：
- A：1 秒后正常结束；
- B：0.5 秒后抛出 RuntimeError；
- C：等待 5 秒，在 finally 中输出 C 正在清理资源。
使用 TaskGroup 运行：
async with asyncio.TaskGroup() as tg:
    ...
要求：
1. 为三个任务设置名称。
2. 捕获 RuntimeError 对应的异常组。
3. 使用 except* RuntimeError，不能使用普通的 except RuntimeError。
4. 观察 C 是否执行资源清理。
5. 输出异常组中每一个异常。
思考：
- B 失败后，A 和 C 分别可能处于什么状态？
- 为什么 TaskGroup 更适合管理生命周期相关的任务？
"""

import asyncio


async def task_a():
    await asyncio.sleep(1)
    print("A 正常结束")
    return "A-result"


async def task_b():
    await asyncio.sleep(0.5)
    raise RuntimeError("B 抛出运行时错误")


async def task_c():
    try:
        await asyncio.sleep(5)
        print("C 正常结束")
    finally:
        # 观察要求 4：即使被取消，finally 中的清理代码仍会执行
        print("C 正在清理资源")


async def main():
    try:
        async with asyncio.TaskGroup() as tg:
            # 1. 为三个任务设置名称
            t1 = tg.create_task(task_a(), name="Task-A")
            t2 = tg.create_task(task_b(), name="Task-B")
            t3 = tg.create_task(task_c(), name="Task-C")

    # 2 & 3. 捕获 RuntimeError 对应的异常组，使用 except* 语法
    except* RuntimeError as eg:
        # 5. 输出异常组中每一个异常
        print(f"捕获到异常组: {eg!r}")
        for idx, exc in enumerate(eg.exceptions, 1):
            print(f"  └─ 异常 {idx}: [{type(exc).__name__}] {exc}")

    # 检查各任务最终状态
    print("\n--- 任务状态统计 ---")
    for t in [t1, t2, t3]:
        status = "已取消" if t.cancelled() else ("异常" if t.exception() else "完成")
        print(f"任务 [{t.get_name()}]: done={t.done()}, status={status}")


if __name__ == "__main__":
    asyncio.run(main())