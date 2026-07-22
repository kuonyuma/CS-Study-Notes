"""
工具：执行终端命令

当 LLM 决定要运行某个命令时，就会调用这个工具。
"""
import sys
import subprocess
from pathlib import Path

# 将项目根目录添加到 sys.path
project_root = str(Path(__file__).resolve().parent.parent.parent)
if project_root not in sys.path:
    sys.path.insert(0, project_root)

from src.config.logging import logger


def run(params: dict) -> str:
    """执行终端命令，返回输出结果"""
    command = params.get("command", "")
    timeout = params.get("timeout", 30)

    if not command:
        return "错误：缺少 command 参数"

    try:
        logger.info(f"执行命令: {command}")

        result = subprocess.run(
            command,
            shell=True,
            capture_output=True,
            text=True,
            timeout=timeout,
            encoding='utf-8',
            errors='replace'
        )

        output = f"退出码: {result.returncode}\n"
        if result.stdout:
            output += f"\n--- stdout ---\n{result.stdout}"
        if result.stderr:
            output += f"\n--- stderr ---\n{result.stderr}"
        if not result.stdout and not result.stderr:
            output += "\n（无输出）"

        return output

    except subprocess.TimeoutExpired:
        logger.warning(f"命令超时（{timeout}秒）: {command}")
        return f"错误：命令执行超时（{timeout}秒）"
    except Exception as e:
        logger.error(f"执行命令出错: {e}")
        return f"错误：{e}"
