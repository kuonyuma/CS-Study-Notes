"""应用配置模块。

这个模块演示如何把环境变量读取逻辑集中在一个地方，避免入口模块
和业务模块到处直接读取环境变量。
"""

from __future__ import annotations

import os
from dataclasses import dataclass
from typing import Optional


def _as_bool(value: Optional[str], default: bool = False) -> bool:
    """把常见的环境变量布尔值转换为 ``bool``。"""

    if value is None:
        return default
    return value.strip().lower() in {"1", "true", "yes", "on"}


@dataclass(frozen=True)
class Settings:
    """应用运行时配置。

    配置对象是不可变的，创建后不会被其他模块意外修改。
    """

    app_name: str = "agent-service"
    debug: bool = False
    precision: int = 2

    @classmethod
    def from_env(cls) -> "Settings":
        """从环境变量创建配置对象。"""

        precision_text = os.getenv("AGENT_SERVICE_PRECISION", "2")
        try:
            precision = int(precision_text)
        except ValueError as exc:
            raise ValueError("AGENT_SERVICE_PRECISION 必须是整数") from exc

        if precision < 0:
            raise ValueError("AGENT_SERVICE_PRECISION 不能小于 0")

        return cls(
            app_name=os.getenv("AGENT_SERVICE_NAME", "agent-service"),
            debug=_as_bool(os.getenv("AGENT_SERVICE_DEBUG")),
            precision=precision,
        )
