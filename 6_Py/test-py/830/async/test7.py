"""
练习 3：使用 gather() 收集结果
实现三个协程：
worker("A", 2)
worker("B", 0.5)
worker("C", 1)
使用 asyncio.gather() 并发运行。
要求：
1. 每个任务完成时输出自己的名称。
2. 每个任务返回自己的名称。
3. 输出 gather() 的最终结果列表。
4. 记录实际完成顺序。
5. 比较实际完成顺序和结果列表顺序。
预期观察：
完成顺序：B、C、A
结果顺序：A、B、C
需要用自己的代码验证，而不是直接写死输出。
"""

import asyncio

async def worker(name: str, delay: float) -> str:
    await asyncio.sleep(delay)
    print(f"{name}执行完毕")
    return f"{name}-result"

async def main():

    result = await asyncio.gather(
        worker('a',4),
        worker('b',3),
        worker('c',2),
        worker('d',1),
    )
    print(result)
asyncio.run(main())


