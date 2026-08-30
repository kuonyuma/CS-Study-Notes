import asyncio


async def calculate(number):
    await asyncio.sleep(1)
    return number * 2


async def main():
    result = await calculate(21)
    print(result)


if __name__ == "__main__":
    asyncio.run(main())
