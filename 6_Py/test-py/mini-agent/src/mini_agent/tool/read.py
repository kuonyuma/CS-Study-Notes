from __future__ import annotations

import os
from pathlib import Path
from typing import Any, ClassVar

from mini_agent.tool.base_tool import Tool, Result


class ReadFileTool(Tool):
    name: ClassVar[str] = "read_file"
    description: ClassVar[str] = "读取文件内容"
    input_schema: ClassVar[dict[str, Any]] = {
        "type": "OBJECT",
        "properties": {"path": {"type": "STRING", "description": "读取文件的路径"}},
        "required": ["path"],
    }
    read_only: ClassVar[bool] = True

    def __init__(self, base_dir: Path | str | None = None) -> None:
        self.base_dir = Path(base_dir).expanduser().resolve() if base_dir else None

    async def run(self, parameter: dict[str, Any]) -> Result:
        try:
            if not isinstance(parameter, dict):
                return Result(content="参数错误：参数必须是对象", error=True)

            raw_path = parameter.get("path")
            if raw_path is None:
                return Result(content="参数错误：未提供文件路径或路径为空", error=True)

            try:
                path_text = os.fspath(raw_path)
            except TypeError:
                return Result(content="参数错误：文件路径必须是字符串或路径对象", error=True)

            if isinstance(path_text, bytes):
                try:
                    path_text = os.fsdecode(path_text)
                except UnicodeDecodeError:
                    return Result(content="参数错误：文件路径不是有效的文本路径", error=True)

            if not path_text.strip():
                return Result(content="参数错误：未提供文件路径或路径为空", error=True)

            path = Path(path_text).expanduser()
            if self.base_dir and not path.is_absolute():
                path = self.base_dir / path

            # resolve() 也会解析符号链接，避免通过链接绕过 base_dir 限制。
            path = path.resolve()
            if self.base_dir and not path.is_relative_to(self.base_dir):
                return Result(content=f"文件路径 '{raw_path}' 超出允许的目录范围", error=True)

            if not path.exists():
                return Result(content=f"该路径 '{raw_path}' 不存在", error=True)

            if path.is_dir():
                return Result(content=f"该路径 '{raw_path}' 是一个目录，不是文件", error=True)

            if not path.is_file():
                return Result(content=f"该路径 '{raw_path}' 不是普通文件", error=True)

            content = self._read_text(path)
            return Result(content=content, error=False)
        except (OSError, RuntimeError, ValueError) as e:
            return Result(content=f"读取文件出错\n错误信息: {e}", error=True)

    @staticmethod
    def _read_text(path: Path) -> str:
        """按常见文本编码读取文件，无法确定编码时保留可读文本。"""
        data = path.read_bytes()
        for encoding in ("utf-8", "gb18030"):
            try:
                return data.decode(encoding)
            except UnicodeDecodeError:
                continue
        return data.decode("utf-8", errors="replace")
