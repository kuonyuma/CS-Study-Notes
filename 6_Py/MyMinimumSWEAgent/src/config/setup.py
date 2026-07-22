"""
配置加载模块

作用：从 config/config.yml 读取配置项，提供给其他模块使用。
采用「单例模式」—— 全局只会加载一次配置文件。

用法：
    from src.config.setup import config
    print(config.MODEL_NAME)  # -> "gemini-2.5-flash"
"""
import sys
import yaml
from pathlib import Path

# 将项目根目录添加到 sys.path
project_root = str(Path(__file__).resolve().parent.parent.parent)
if project_root not in sys.path:
    sys.path.insert(0, project_root)

from src.config.logging import logger


class Config:
    """
    单例配置类

    为什么用单例？
    - 配置文件只需要读一次，后续所有模块共享同一份配置
    - 避免重复 I/O
    """
    _instance = None

    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super(Config, cls).__new__(cls)
            cls._instance._initialized = False
        return cls._instance

    def __init__(self, config_path: str = "./config/config.yml"):
        if self._initialized:
            return
        self._initialized = True

        self._config = self._load_config(config_path)
        self.MODEL_NAME = self._config.get('model_name', 'gemini-2.5-flash')

    @staticmethod
    def _load_config(config_path: str) -> dict:
        try:
            with open(config_path, 'r', encoding='utf-8') as file:
                return yaml.safe_load(file)
        except Exception as e:
            logger.error(f"加载配置文件失败: {e}")
            return {}


config = Config()
