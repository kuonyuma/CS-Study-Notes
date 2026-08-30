"""
练习 4：验证 Runner 复用事件循环
编写两个异步函数，每个函数都：
- 调用 asyncio.get_running_loop()；
- 打印或返回 id(loop)。
使用同一个 Runner 分别运行这两个异步函数。
完成标准：
- 两次得到的事件循环 ID 相同；
- 离开 with asyncio.Runner() 后资源自动关闭。
"""

import asyncio


async def add(a, b):
    loop = asyncio.get_running_loop()
    print(f"函数add:{loop}")
    return a + b


async def sub(a, b):
    loop = asyncio.get_running_loop()
    print(f"函数sub:{loop}")
    return a - b

def main():

    with asyncio.Runner() as runner:
        runner.run(add(1,2))
        runner.run(sub(1,2))


if __name__ == "__main__":
    main()  