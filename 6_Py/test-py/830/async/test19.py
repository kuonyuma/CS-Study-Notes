"""
实现一个“超时可取消”的下载模拟器：

- 单任务超时；
- 整批任务总超时；
- 用户主动取消；
- 取消后执行资源清理；
- 一个关键清理任务使用 `shield()`；
- 验证程序退出时没有遗留任务。

"""

import asyncio


# 关键清理任务：模拟不可中断的状态刷盘/断点保存
async def critical_save_checkpoint(task_id: str):
    print(f"  [shield保护] {task_id}: 正在保存断点/持久化数据...")
    await asyncio.sleep(0.2)  # 模拟 IO 操作
    print(f"  [shield保护] {task_id}: 断点保存完成，数据完整！")


# 下载模拟器核心协程
async def download_file(task_id: str, duration: float, task_timeout: float|None = None):
    try:
        print(
            f"[{task_id}] 开始下载 (总耗时: {duration}s, 单任务超时: {task_timeout}s)"
        )

        async def _core_download():
            for i in range(1, int(duration * 10) + 1):
                await asyncio.sleep(0.1)
                print(f"[{task_id}] 下载中... {i * 10}%")
            return f"{task_id}-成功"

        # 👈 核心机制 1：单任务超时控制
        if task_timeout is not None:
            return await asyncio.wait_for(_core_download(), timeout=task_timeout)
        else:
            return await _core_download()

    except asyncio.TimeoutError:
        print(f"[{task_id}] [超时] 单任务已超时，触发清理...")
        # 👈 核心机制 2：关键清理任务使用 shield() 保护，防止清理时被外部强行中断
        await asyncio.shield(critical_save_checkpoint(task_id))
        raise
    except asyncio.CancelledError:
        print(f"[{task_id}] [取消] 收到取消信号，触发清理...")
        # 👈 核心机制 3：同样使用 shield() 保护取消清理链路
        await asyncio.shield(critical_save_checkpoint(task_id))
        raise
    finally:
        # 👈 核心机制 4：通用资源释放（连接关闭、缓冲区清空）
        print(f"[{task_id}] [清理] 释放常规网络连接")


async def main():
    # 场景 1：单任务超时
    print("=== 场景 1: 单任务超时演示 ===")
    try:
        await download_file("task-single-timeout", duration=1.0, task_timeout=0.3)
    except asyncio.TimeoutError:
        print("-> 捕获预期单任务超时\n")

    # 场景 2：用户主动取消
    print("=== 场景 2: 用户主动取消演示 ===")
    user_task = asyncio.create_task(download_file("task-user-cancel", duration=1.0))
    await asyncio.sleep(0.25)
    print("-> 用户点击了 [取消下载] 按钮...")
    user_task.cancel()  # 👈 核心机制 5：用户主动发出取消指令
    try:
        await user_task
    except asyncio.CancelledError:
        print("-> 捕获预期用户取消\n")

    # 场景 3：整批任务总超时
    print("=== 场景 3: 整批任务总超时演示 ===")
    batch_tasks = [
        asyncio.create_task(download_file("batch-task-1(快)", duration=0.2)),
        asyncio.create_task(download_file("batch-task-2(慢1)", duration=2.0)),
        asyncio.create_task(download_file("batch-task-3(慢2)", duration=2.0)),
    ]
    try:
        # 👈 核心机制 6：整批任务总超时控制
        await asyncio.wait_for(asyncio.gather(*batch_tasks), timeout=0.5)
    except asyncio.TimeoutError:
        print("-> 🚨 触发整批总超时！取消所有未完成任务...")
        for t in batch_tasks:
            if not t.done():
                t.cancel()
        # 等待取消和清理任务全部结束，避免遗留未决协程
        await asyncio.gather(*batch_tasks, return_exceptions=True)
        print()

    # 场景 4：退出验证
    print("=== 场景 4: 验证退出时无遗留任务 ===")
    # 👈 核心机制 7：排查 event loop 中除 main 以外未完成的存活任务
    current_task = asyncio.current_task()
    pending = [t for t in asyncio.all_tasks() if t is not current_task and not t.done()]
    print(f"当前存活未决任务列表: {pending}")
    assert len(pending) == 0, f"存在残留任务: {pending}"
    print("✅ 验证成功：所有任务与清理协程均已收敛，无任何孤儿任务遗留！")


if __name__ == "__main__":
    asyncio.run(main())
