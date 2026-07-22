"""
这是一个测试用的 hello 脚本。
用来验证 SWE Agent 是否能正确读取和理解代码。
"""


def say_hello(name: str) -> str:
    """向指定的人打招呼"""
    return f"Hello, {name}! 欢迎使用 SWE Agent!"


if __name__ == "__main__":
    message = say_hello("World")
    print(message)
