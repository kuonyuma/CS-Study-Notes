"""
工具：写入文件

当 LLM 决定要创建或修改一个文件时，就会调用这个工具。
"""
import sys
import os
from pathlib import Path

# 将项目根目录添加到 sys.path
project_root = str(Path(__file__).resolve().parent.parent.parent)
if project_root not in sys.path:
    sys.path.insert(0, project_root)

from src.config.logging import logger


def write(params: dict) -> str:
    """将内容写入指定文件（创建或覆盖）"""
    path = params.get("path", "")
    content = params.get("content", "")

    if not path:
        return "错误：缺少 path 参数"
    if not content:
        return "错误：缺少 content 参数"

    try:
        parent_dir = os.path.dirname(path)
        if parent_dir and not os.path.exists(parent_dir):
            os.makedirs(parent_dir)
            logger.info(f"自动创建目录: {parent_dir}")

        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)

        line_count = content.count('\n') + 1
        logger.info(f"成功写入文件: {path} ({line_count} 行)")
        return f"成功写入文件: {path} (共 {line_count} 行)"

    except Exception as e:
        logger.error(f"写入文件出错: {e}")
        return f"错误：{e}"
