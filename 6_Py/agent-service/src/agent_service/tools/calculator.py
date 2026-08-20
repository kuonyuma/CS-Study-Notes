"""基础计算器模块。

该模块只负责计算，不负责命令行参数解析或环境变量读取，这样更容易
单独测试，也更容易被其他模块复用。
"""

from __future__ import annotations

from numbers import Real
from typing import Callable, Union

Number = Union[int, float]
Operation = Callable[[Number, Number], Number]

SUPPORTED_OPERATORS = ("+", "-", "*", "/", "^")


def _validate_number(value: Number, name: str) -> None:
    """拒绝非数字输入，同时允许整数和浮点数。"""

    if not isinstance(value, Real) or isinstance(value, bool):
        raise TypeError(f"{name} 必须是数字")


def add(left: Number, right: Number) -> Number:
    """返回两个数字之和。"""

    _validate_number(left, "left")
    _validate_number(right, "right")
    return left + right


def subtract(left: Number, right: Number) -> Number:
    """返回两个数字之差。"""

    _validate_number(left, "left")
    _validate_number(right, "right")
    return left - right


def multiply(left: Number, right: Number) -> Number:
    """返回两个数字的乘积。"""

    _validate_number(left, "left")
    _validate_number(right, "right")
    return left * right


def divide(left: Number, right: Number) -> float:
    """返回两个数字相除的结果。"""

    _validate_number(left, "left")
    _validate_number(right, "right")
    if right == 0:
        raise ZeroDivisionError("除数不能为 0")
    return left / right


def power(left: Number, right: Number) -> Number:
    """返回 ``left`` 的 ``right`` 次方。"""

    _validate_number(left, "left")
    _validate_number(right, "right")
    return left**right


_OPERATIONS: dict[str, Operation] = {
    "+": add,
    "-": subtract,
    "*": multiply,
    "/": divide,
    "^": power,
}


def calculate(left: Number, operator: str, right: Number) -> Number:
    """根据运算符计算结果。

    使用字典保存运算符到函数的映射，展示模块内部实现可以保持私有，
    而 ``calculate`` 作为对外提供的统一入口。
    """

    try:
        operation = _OPERATIONS[operator]
    except KeyError as exc:
        supported = ", ".join(SUPPORTED_OPERATORS)
        raise ValueError(f"不支持的运算符 {operator!r}，可用运算符：{supported}") from exc
    return operation(left, right)
