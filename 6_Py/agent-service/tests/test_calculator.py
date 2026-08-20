"""calculator 模块的测试。

测试模块位于源码包之外，通过安装配置或 pytest 的导入路径使用包 API。
"""

import pytest

from agent_service import calculate
from agent_service.tools.calculator import add, divide, multiply, subtract


def test_basic_operations() -> None:
    """四个基础函数分别对应一个可导入的模块 API。"""

    assert add(2, 3) == 5
    assert subtract(5, 3) == 2
    assert multiply(4, 3) == 12
    assert divide(9, 3) == 3


@pytest.mark.parametrize(
    ("left", "operator", "right", "expected"),
    [
        (2, "+", 3, 5),
        (7, "-", 4, 3),
        (2, "*", 4, 8),
        (7, "/", 2, 3.5),
        (2, "^", 3, 8),
    ],
)
def test_calculate_dispatches_to_operation(
    left: int, operator: str, right: int, expected: float
) -> None:
    """包级别导出的 calculate 可以分派所有支持的运算。"""

    assert calculate(left, operator, right) == expected


def test_divide_by_zero_raises() -> None:
    with pytest.raises(ZeroDivisionError, match="除数不能为 0"):
        divide(1, 0)


def test_unknown_operator_raises() -> None:
    with pytest.raises(ValueError, match="不支持的运算符"):
        calculate(1, "%", 2)


def test_non_number_raises() -> None:
    with pytest.raises(TypeError, match="必须是数字"):
        add("1", 2)  # type: ignore[arg-type]
