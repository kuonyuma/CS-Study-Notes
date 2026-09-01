import asyncio


async def save_data():
    await asyncio.sleep(5)
    print("数据保存完成")
    return "saved"


async def main():
    task = asyncio.create_task(save_data())

    try:
        result = await asyncio.shield(task)
        print(result)
    except asyncio.CancelledError:
        print("外层任务被取消")
        print("save_data 任务仍然可以继续运行")


asyncio.run(main())