"""
综合练习：异步倒计时
实现三个并发倒计时：
A：3 秒
B：2 秒
C：1 秒
要求：
- 每个倒计时每隔一秒打印一次剩余时间；
- 使用 asyncio.sleep() 等待；
- 使用 get_running_loop().time() 统计总耗时；
- 使用 asyncio.run() 启动程序；
- 再写一个 Runner 版本；
- 总耗时应接近三秒，而不是六秒。
"""

import asyncio 

async def count_down(time:int,name):
    tmp = time
    while tmp > 0:
        print(f"任务{name}剩余时间为{tmp}")
        tmp -= 1
        await asyncio.sleep(1)

async def run_count_down():
    loop = asyncio.get_running_loop()
    begin = loop.time()

    await asyncio.gather(
        count_down(3,"a"),
        count_down(2,"b"),
        count_down(1,'c'),
    )
    end = loop.time()

    print(f"总耗时{end - begin}")

def main_with_runner():
    with asyncio.Runner() as runner:
        runner.run(run_count_down())

async def main_with_run():
    await run_count_down()

if __name__ == "__main__":
    main_with_runner()
    asyncio.run(main_with_run())
