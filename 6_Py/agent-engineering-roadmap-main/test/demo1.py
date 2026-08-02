from collections.abc import Iterator


def counter() -> Iterator[int]:
    num: int = 0

    while num <= 3:
        yield num
        num += 1


bean = counter()

print("开始调用函数")
for tmp in bean:
    print(tmp)


def echo():
    i = 0
    while i <= 3:
        tmp = yield
        print(tmp)
        i += 1


bean_2 = echo()
next(bean_2)
bean_2.send("你好")
bean_2.send("世界")
bean_2.send("呀~")
