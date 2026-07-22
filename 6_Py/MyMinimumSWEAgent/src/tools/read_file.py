"""
工具：读取文件 / 列出目录

这是给 Agent（LLM）使用的工具。
当 LLM 决定要查看某个文件的内容时，就会调用这个工具。
"""
import sys
import os
from pathlib import Path

# 将项目根目录添加到 sys.path
project_root = str(Path(__file__).resolve().parent.parent.parent)
if project_root not in sys.path:
    sys.path.insert(0, project_root)

from src.config.logging import logger


def read(params: dict) -> str:
    """读取文件内容，返回带行号的文本"""
    path = params.get("path", "")
    if not path:
        return "错误：缺少 path 参数"

    try:
        if os.path.isdir(path):
            return list_dir(path)

        with open(path, 'r', encoding='utf-8') as f:
            lines = f.readlines()

        numbered = []
        for i, line in enumerate(lines, 1):
            numbered.append(f"{i}: {line.rstrip()}")

        result = f"文件: {path} (共 {len(lines)} 行)\n"
        result += "\n".join(numbered)
        return result

    except FileNotFoundError:
        return f"错误：文件不存在 -> {path}"
    except Exception as e:
        logger.error(f"读取文件出错: {e}")
        return f"错误：{e}"


def list_dir(path: str) -> str:
    """列出目录下的所有文件和子目录"""
    try:
        entries = os.listdir(path)
        if not entries:
            return f"目录 {path} 是空的"

        result = f"目录: {path}\n"
        for entry in sorted(entries):
            full = os.path.join(path, entry)
            if os.path.isdir(full):
                result += f"  📁 {entry}/\n"
            else:
                size = os.path.getsize(full)
                result += f"  📄 {entry} ({size} bytes)\n"
        return result

    except FileNotFoundError:
        return f"错误：目录不存在 -> {path}"
    except Exception as e:
        logger.error(f"列出目录出错: {e}")
        return f"错误：{e}"
