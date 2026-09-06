
# 迭代器管理协议

class countDown:
    def __init__(self,number:int) -> None:
        self.number = number
        self.cur = 0

    def __iter__(self):
        return self

    def __next__(self):

        if self.cur <= self.number:
            tmp = self.cur
            self.cur += 1
            return tmp
        raise StopIteration




for i in countDown(20):
    print(i)
