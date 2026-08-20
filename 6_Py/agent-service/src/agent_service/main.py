"""命令行入口模块。

可以通过 ``python -m agent_service.main`` 运行，也可以使用
``pyproject.toml`` 中声明的 ``agent-service`` 命令运行。
"""

from __future__ import annotations

import argparse
from collections.abc import Sequence
from typing import Optional

from .config import Settings
from .tools.calculator import SUPPORTED_OPERATORS, calculate


def _build_parser(default_precision: int) -> argparse.ArgumentParser:
    """创建命令行参数解析器。"""

    parser = argparse.ArgumentParser(description="agent-service 教学计算器")
    parser.add_argument("left", type=float, help="左操作数")
    parser.add_argument("operator", choices=SUPPORTED_OPERATORS, help="运算符")
    parser.add_argument("right", type=float, help="右操作数")
    parser.add_argument(
        "--precision",
        type=int,
        default=default_precision,
        help=f"结果保留的小数位数，默认 {default_precision}",
    )
    return parser


def main(argv: Optional[Sequence[str]] = None) -> int:
    """运行命令行程序并返回进程退出码。"""

    settings = Settings.from_env()
    parser = _build_parser(settings.precision)
    args = parser.parse_args(argv)

    if args.precision < 0:
        parser.error("--precision 不能小于 0")

    result = calculate(args.left, args.operator, args.right)
    print(f"{args.left:g} {args.operator} {args.right:g} = {result:.{args.precision}f}")

    if settings.debug:
        print(f"[{settings.app_name}] debug=True")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
