from typing import Generic, TypeVar, Sequence

T = TypeVar("T")


class Box(Generic[T]):
    def __init__(self, value: T):
        self.value = value

    def get(self) -> T:
        return self.value


class MyList(Generic[T]):
    def __init__(self, items: Sequence[T]):
        self.items = items

    def get(self) -> T:
        return self.items[0]


def test_box():
    int_box = Box[int](42)
    str_box = Box[str]("Hello, World!")

    print(int_box.get())  # 输出: 42
    print(str_box.get())  # 输出: Hello, World!


def test_mylist():
    int_list = MyList[int]([1, 2, 3])
    str_list = MyList[str](["apple", "banana", "cherry"])

    print(int_list.get())  # 输出: 1
    print(str_list.get())  # 输出: apple
