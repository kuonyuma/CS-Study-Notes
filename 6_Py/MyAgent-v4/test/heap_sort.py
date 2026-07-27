def heapify(arr, n, i):
    largest = i
    left = 2 * i + 1
    right = 2 * i + 2

    if left < n and arr[left] > arr[largest]:
        largest = left

    if right < n and arr[right] > arr[largest]:
        largest = right

    if largest != i:
        arr[i], arr[largest] = arr[largest], arr[i]
        heapify(arr, n, largest)


def heap_sort(arr):
    n = len(arr)

    # 构建大顶堆
    for i in range(n // 2 - 1, -1, -1):
        heapify(arr, n, i)

    # 逐个提取元素
    for i in range(n - 1, 0, -1):
        arr[i], arr[0] = arr[0], arr[i]
        heapify(arr, i, 0)

    return arr


def main():
    test_cases = [
        ("空列表", []),
        ("单元素", [42]),
        ("已排序列表", [1, 2, 3, 4, 5]),
        ("逆序列表", [5, 4, 3, 2, 1]),
        ("包含重复元素", [4, 10, 3, 5, 1, 4, 10]),
        ("包含负数", [-3, 0, -1, 5, 2, -10]),
        ("无序无重复列表", [12, 11, 13, 5, 6, 7]),
    ]

    print("=== 堆排序测试开始 ===")
    for name, arr in test_cases:
        arr_copy = list(arr)
        heap_sort(arr_copy)
        expected = sorted(arr)
        assert arr_copy == expected, f"测试失败 [{name}]: 期望 {expected}, 实际 {arr_copy}"
        print(f"[{name}] 测试通过: 原始={arr} -> 排序后={arr_copy}")

    print("\n所有测试用例全部通过！")


if __name__ == "__main__":
    main()
