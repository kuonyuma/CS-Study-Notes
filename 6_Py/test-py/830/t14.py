class Countdown:
    def __init__(self, start: int):
        self.current = start

    def __iter__(self):
        # 迭代器协议要求 __iter__ 返回迭代器自身
        return self

    def __next__(self):
        if self.current <= 0:
            # 数据耗尽，必须抛出 StopIteration
            raise StopIteration
        val = self.current
        self.current -= 1
        return val

# 使用 for 遍历
for num in Countdown(3):
    print(num)  # 输出: 3, 2, 1

# 1. 获取迭代器
iterator = Countdown(5).__iter__()  # 内部调用 [1, 2, 3].__iter__()

# 2. 循环获取下一个值
while True:
    try:
        item = iterator.__next__()  # 内部调用 iterator.__next__()
        print(item)
    except StopIteration:
        # 3. 捕获到 StopIteration，安全退出循环
        break