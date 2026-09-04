import asyncio
import sys
import time


class Connectdb:
    def __init__(self, name: str) -> None:
        self.name = name

    # --- 同步协议 ---
    def __enter__(self):
        print(f"[{self.name}] 同步连接准备中...")
        time.sleep(1)
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        print(f"[{self.name}] 同步关闭资源")
        if exc_type is not None:
            print(f"检测到异常: {exc_val}")
            return False  # False 表示不压制异常，继续向外抛出

    # --- 异步协议 ---
    async def __aenter__(self):
        print(f"[{self.name}] 异步 aenter 启动...")
        await asyncio.sleep(1)
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        print(f"[{self.name}] 异步 aexit 准备关闭资源...")
        if exc_type is not None:
            print(f"aexit 捕获到异常: {exc_val}")
            return False  # 返回 False/None 会向外抛出异常；若返回 True 则会吞掉异常


# 1. 同步语法糖
def t1():
    with Connectdb("张三") as session:
        print(f"t1 业务执行: {session.name}")


# 2. 同步底层等价实现
def t2():
    bean = Connectdb("李四")
    session = bean.__enter__()  # 获取 as 绑定的资源
    try:
        print(f"t2 业务执行: {session.name}")
    except BaseException:
        exc_type, exc_val, exc_tb = sys.exc_info()
        # 注意：这里必须是 bean.__exit__，而不是 session.__exit__
        if not bean.__exit__(exc_type, exc_val, exc_tb):
            raise
    else:
        bean.__exit__(None, None, None)


# 3. 异步语法糖（标准写法）
async def t3_sugar():
    async with Connectdb("王五-语法糖") as session:
        print(f"t3_sugar 业务执行: {session.name}")


# 4. 异步底层等价实现（修复后）
async def t3_manual():
    bean = Connectdb("王五-底层实现")
    session = await bean.__aenter__()  # 获取 as 绑定的资源

    try:
        print(f"t3_manual 业务执行: {session.name}")
    except BaseException:
        exc_type, exc_val, exc_tb = sys.exc_info()
        # 注意：这里必须是 bean.__aexit__，且需要 await
        if not await bean.__aexit__(exc_type, exc_val, exc_tb):
            raise
    else:
        await bean.__aexit__(None, None, None)


if __name__ == "__main__":
    print("=== 1. 测试同步语法糖 ===")
    t1()

    print("\n=== 2. 测试同步底层实现 ===")
    t2()

    print("\n=== 3. 测试异步语法糖 ===")
    asyncio.run(t3_sugar())

    print("\n=== 4. 测试异步底层实现 ===")
    asyncio.run(t3_manual())