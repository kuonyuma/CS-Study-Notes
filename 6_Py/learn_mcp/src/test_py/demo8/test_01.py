# 写一个简单的装饰器
from functools import wraps


def my_decorator(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        print("增强func前的功能")
        result = func(*args, **kwargs)
        print("增强func后的功能")
        return result

    return wrapper


@my_decorator
def say_hello():
    print("Hello, World!")


@my_decorator
def add(x, y):
    return x + y


result = say_hello()
print(result)  # 输出: None，因为say_hello没有返回值

result = add(3, 5)
print(result)  # 输出: 8

print(add.__name__)  # 输出: add
