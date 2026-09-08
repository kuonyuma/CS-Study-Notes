import asyncio


class AsyncDatabaseConnection:
    # 1. 进入代码块前执行（相当于扫码验证开锁）
    async def __aenter__(self):
        print("正在连接远程数据库（耗时等待）...")
        await asyncio.sleep(1)  # 模拟网络握手
        print("连接成功！")
        return "db_session_obj"  # 这个返回值会赋给 as 后面的变量

    # 2. 离开代码块时执行（无论正常结束还是抛出异常，都会执行）
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        print("正在断开连接并通知服务器关闭会话...")
        await asyncio.sleep(0.5)  # 模拟网络断开
        print("会话已安全释放！")


async def main():
    # 使用 async with 语法调用
    async with AsyncDatabaseConnection() as session:
        print(f"正在执行业务操作，当前会话: {session}")


# 启动异步任务
asyncio.run(main())
