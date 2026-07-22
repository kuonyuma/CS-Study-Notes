"""
日志配置模块

作用：统一管理整个项目的日志输出。
- 同时输出到 控制台(stdout) 和 日志文件(logs/app.log)
- 这样你既能在终端看到日志，也能事后去文件里查看

用法（其他模块直接导入 logger 即可）：
    from src.config.logging import logger
    logger.info("这是一条日志")
"""
import logging
import os
import sys


def setup_logger(log_filename="app.log", log_dir="logs"):
    """创建并配置 logger"""

    # 确保 stdout 使用 utf-8 编码（Windows 下防止中文乱码）
    if hasattr(sys.stdout, 'reconfigure'):
        try:
            sys.stdout.reconfigure(encoding='utf-8', errors='replace')
        except Exception:
            pass

    # 创建日志目录
    if not os.path.exists(log_dir):
        os.makedirs(log_dir)

    log_filepath = os.path.join(log_dir, log_filename)

    # 配置日志格式和输出目标
    logging.basicConfig(
        level=logging.INFO,
        format="%(asctime)s [%(levelname)s] [%(module)s]: %(message)s",
        handlers=[
            logging.StreamHandler(sys.stdout),            # 输出到控制台
            logging.FileHandler(log_filepath, encoding='utf-8')  # 输出到文件
        ]
    )

    return logging.getLogger()


# ★ 模块级别的 logger 实例 —— 整个项目共用这一个
logger = setup_logger()
