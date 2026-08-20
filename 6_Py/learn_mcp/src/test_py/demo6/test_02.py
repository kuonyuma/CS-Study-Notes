from typing import Protocol


class counter(Protocol):
    def add(self, a: int, b: int) -> int: ...


class counter1:
    def add(self, a: int, b: int) -> int:
        return a + b


class counter2:
    def add(self, a: str, b: str) -> str:
        return a + b


def test_counter(counter: counter, a, b):
    return counter.add(a, b)


def main():
    c1 = counter1()
    c2 = counter2()

    result1 = test_counter(c1, 1, 2)
    result2 = test_counter(c2, "Hello, ", "World!")
    print(result1)
    print(result2)


if __name__ == "__main__":
    main()
