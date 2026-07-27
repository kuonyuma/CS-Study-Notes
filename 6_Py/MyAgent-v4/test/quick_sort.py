def quick_sort(arr):
    """
    快速排序算法实现
    :param arr: 待排序的列表
    :return: 排序后的新列表
    """
    if len(arr) <= 1:
        return arr
    pivot = arr[len(arr) // 2]
    left = [x for x in arr if x < pivot]
    middle = [x for x in arr if x == pivot]
    right = [x for x in arr if x > pivot]
    return quick_sort(left) + middle + quick_sort(right)


def quick_sort_in_place(arr, low=0, high=None):
    """
    原地快速排序算法实现
    :param arr: 待排序的列表
    :param low: 起始索引
    :param high: 结束索引
    """
    if high is None:
        high = len(arr) - 1
    
    if low < high:
        pivot_index = partition(arr, low, high)
        quick_sort_in_place(arr, low, pivot_index - 1)
        quick_sort_in_place(arr, pivot_index + 1, high)
    return arr


def partition(arr, low, high):
    pivot = arr[high]
    i = low - 1
    for j in range(low, high):
        if arr[j] <= pivot:
            i += 1
            arr[i], arr[j] = arr[j], arr[i]
    arr[i + 1], arr[high] = arr[high], arr[i + 1]
    return i + 1


def main():
    test_cases = [
        ("无序无重复元素", [5, 3, 8, 4, 2, 7, 1, 10, 6, 9]),
        ("逆序数组", [10, 9, 8, 7, 6, 5, 4, 3, 2, 1]),
        ("已排序数组", [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]),
        ("包含重复元素", [4, 2, 5, 2, 3, 4, 1, 5, 3]),
        ("全相同元素", [7, 7, 7, 7, 7]),
        ("单元素数组", [42]),
        ("空数组", []),
        ("负数与正数混合", [-3, 10, -8, 0, 5, -1, 2])
    ]

    print("=== 测试快速排序 (quick_sort) ===")
    for name, arr in test_cases:
        sorted_arr = quick_sort(arr)
        expected = sorted(arr)
        assert sorted_arr == expected, f"测试失败 [{name}]: 期望 {expected}, 得到 {sorted_arr}"
        print(f"[PASS] {name}: 原数组 {arr} -> 排序后 {sorted_arr}")

    print("\n=== 测试原地快速排序 (quick_sort_in_place) ===")
    for name, arr in test_cases:
        arr_copy = arr.copy()
        quick_sort_in_place(arr_copy)
        expected = sorted(arr)
        assert arr_copy == expected, f"测试失败 [{name}]: 期望 {expected}, 得到 {arr_copy}"
        print(f"[PASS] {name}: 原数组 {arr} -> 排序后 {arr_copy}")

    print("\n所有测试用例通过！")


if __name__ == '__main__':
    main()
