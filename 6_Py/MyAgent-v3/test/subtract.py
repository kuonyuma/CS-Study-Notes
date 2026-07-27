def subtract(a, b):
    """返回两个数的差 (a - b)"""
    return a - b

if __name__ == "__main__":
    result = subtract(10, 4)
    print(f"10 - 4 = {result}")
    assert subtract(5, 3) == 2
    assert subtract(3, 5) == -2
    assert subtract(0, 0) == 0
    print("所有测试用例通过！")
