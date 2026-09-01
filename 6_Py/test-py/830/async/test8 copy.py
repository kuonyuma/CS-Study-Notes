"""
练习 4：观察 gather() 的异常行为
实现：
async def success(name: str, delay: float):
    ...

async def failure(delay: float):
    ...
其中 failure() 等待后抛出：
raise ValueError("任务执行失败")
第一轮使用：
await asyncio.gather(
    success("A", 2),
    failure(0.5),
    success("B", 3),
)
要求：
1. 捕获并输出 ValueError。
2. 验证 A、B 是否会继续执行。
3. 保证程序不会在捕获异常后立即退出，以便观察其他任务。
第二轮使用：
return_exceptions=True
要求：
1. 输出完整结果列表。
2. 分别识别正常结果和异常对象。
3. 不能仅通过字符串判断异常，必须使用类型判断。
思考：
- 为什么第一轮中 gather() 抛出异常后，其他任务仍可能继续运行？
"""

import asyncio


async def fail():
    await asyncio.sleep(1)
    raise ValueError("失败")


async def worker(name):
    await asyncio.sleep(2)
    print(f"{name}执行完毕")
    return f"{name}-reuslt"


async def main():

    try:
        result = await asyncio.gather(
            fail(),
            worker("a"),
            worker("b"),
            return_exceptions=True
        )

        print(result)
    except Exception as e:
        print(e)

asyncio.run(main())
