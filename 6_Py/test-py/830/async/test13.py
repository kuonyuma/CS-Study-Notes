"""
综合题：实现一个并发任务调度器
实现：
async def run_jobs(
    jobs: list[tuple[str, float]],
    timeout: float,
) -> dict[str, object]:
    ...
每个元素包含任务名称和运行时间：
jobs = [
    ("job-A", 0.5),
    ("job-B", 2.0),
    ("job-C", 1.0),
]
要求：
1. 使用 create_task() 创建任务。
2. 每个 Task 使用对应任务名称。
3. 使用 as_completed() 按完成顺序收集结果。
4. 超过整体超时时，取消未完成任务。
5. 返回：
{
    "success": {
        "job-A": "job-A-result",
        "job-C": "job-C-result",
    },
    "failed": {
        # 任务名称: 异常对象
    },
    "cancelled": [
        "job-B",
    ],
}
6. 单个任务失败不能导致其他任务停止。
7. 所有已创建任务最终都必须处于完成状态。
8. 不允许出现：
Task exception was never retrieved
验收重点：
- 是否正确保存 Task 引用；
- 是否区分成功、失败和取消；
- 是否正确处理整体超时；
- 是否等待取消清理完成；
- 是否真正按完成顺序处理结果。
"""

import asyncio
from typing import Any


async def _worker(name: str, duration: float) -> str:
    """模拟具体任务的业务执行逻辑"""
    if duration < 0:
        raise ValueError(f"Task '{name}' has invalid duration: {duration}")
    await asyncio.sleep(duration)
    return f"{name}-result"


async def _wrap_task(
    task: asyncio.Task,
) -> tuple[asyncio.Task, Any, Exception | None, bool]:
    """
    包装单个 Task，统一返回 (task对象, 结果, 异常对象, 是否被取消)
    - 作用 1：隔离单任务异常，防止单个任务抛出异常直接中断 as_completed 迭代
    - 作用 2：精确绑定 Task 对象与返回结果，便于获取任务名及更新状态
    """
    try:
        res = await task
        return task, res, None, False
    except asyncio.CancelledError:
        return task, None, None, True
    except Exception as exc:
        return task, None, exc, False


async def run_jobs(
    jobs: list[tuple[str, float]],
    timeout: float,
) -> dict[str, Any]:
    """并发任务调度器主函数"""
    result: dict[str, Any] = {
        "success": {},
        "failed": {},
        "cancelled": [],
    }

    if not jobs:
        return result

    # 1. 显式创建并保存 Task 强引用，并命名对应任务名称 (满足要求 1, 2)
    tasks: list[asyncio.Task] = [
        asyncio.create_task(_worker(name, duration), name=name)
        for name, duration in jobs
    ]

    # 用于追踪当前尚未处理完成的 Task 集合
    pending_tasks = set(tasks)

    try:
        # 2. 使用 as_completed 按完成顺序收集结果，并设置整体超时 (满足要求 3)
        wrapped_coros = [_wrap_task(t) for t in tasks]
        for fut in asyncio.as_completed(wrapped_coros, timeout=timeout):
            try:
                task, res, exc, is_cancelled = await fut
                pending_tasks.discard(task)

                if is_cancelled:
                    result["cancelled"].append(task.get_name())
                elif exc is not None:
                    # 3. 单任务异常记入 failed，不影响其他任务继续执行 (满足要求 5, 6)
                    result["failed"][task.get_name()] = exc
                else:
                    # 正常成功结果
                    result["success"][task.get_name()] = res
            except (asyncio.TimeoutError, TimeoutError):
                # as_completed 触发整体超时，跳出迭代
                break
    except (asyncio.TimeoutError, TimeoutError):
        pass
    finally:
        # 4. 超过整体超时或退出时，取消所有未完成的任务并等待清理完成 (满足要求 4, 7, 8)
        if pending_tasks:
            uncompleted = [t for t in tasks if t in pending_tasks]
            for t in uncompleted:
                t.cancel()
                if t.get_name() not in result["cancelled"]:
                    result["cancelled"].append(t.get_name())

            # 关键：必须等待所有被取消的任务真正执行完取消清理逻辑
            # return_exceptions=True 确保吞掉 CancelledError，防止异常未检出警告
            await asyncio.gather(*uncompleted, return_exceptions=True)

    return result


import asyncio


async def run_tests():
    # 测试用例 1：官方示例（部分成功，部分超时）
    jobs1 = [
        ("job-A", 0.5),
        ("job-B", 2.0),
        ("job-C", 1.0),
    ]
    res1 = await run_jobs(jobs1, timeout=1.5)
    print("Test 1 Result:", res1)
    assert res1["success"] == {
        "job-A": "job-A-result",
        "job-C": "job-C-result",
    }
    assert res1["cancelled"] == ["job-B"]
    assert res1["failed"] == {}

    # 测试用例 2：综合覆盖（成功 + 单任务异常 + 超时取消）
    jobs2 = [
        ("job-fast", 0.2),
        ("job-error", -1.0),  # 抛出 ValueError
        ("job-slow", 1.5),  # 超过 0.8s 被取消
    ]
    res2 = await run_jobs(jobs2, timeout=0.8)
    print("Test 2 Result:", res2)
    assert res2["success"] == {"job-fast": "job-fast-result"}
    assert "job-error" in res2["failed"]
    assert isinstance(res2["failed"]["job-error"], ValueError)
    assert res2["cancelled"] == ["job-slow"]

    print("All tests passed!")


if __name__ == "__main__":
    asyncio.run(run_tests())
