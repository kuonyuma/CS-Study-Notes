"""
文件 I/O 工具函数

作用：提供读写文件、加载 YAML 的基础函数。
这些函数被 Agent 内部的日志追踪和配置加载使用。

注意：这是「内部工具函数」，不要和 src/tools/ 下的「Agent 工具」搞混。
- src/utils/io.py      -> 给项目内部代码用的工具函数
- src/tools/read_file.py -> 给 Agent（LLM）调用的工具
"""
import sys
import yaml
from pathlib import Path

# 将项目根目录添加到 sys.path
project_root = str(Path(__file__).resolve().parent.parent.parent)
if project_root not in sys.path:
    sys.path.insert(0, project_root)

from src.config.logging import logger


def read_file(path: str) -> str:
    """读取文件内容，返回字符串"""
    try:
        with open(path, 'r', encoding='utf-8') as f:
            return f.read()
    except FileNotFoundError:
        logger.error(f"文件不存在: {path}")
        return ""
    except Exception as e:
        logger.error(f"读取文件出错: {e}")
        return ""


def write_to_file(path: str, content: str) -> None:
    """追加写入内容到文件（用于写日志/trace）"""
    try:
        with open(path, 'a', encoding='utf-8') as f:
            f.write(content)
    except Exception as e:
        logger.error(f"写入文件出错: {e}")


def load_yaml(path: str) -> dict:
    """加载 YAML 文件，返回字典"""
    try:
        with open(path, 'r', encoding='utf-8') as f:
            return yaml.safe_load(f)
    except Exception as e:
        logger.error(f"加载 YAML 出错: {e}")
        return {}
