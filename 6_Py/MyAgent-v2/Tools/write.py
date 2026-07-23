import logging
from venv import logger


def write(out_path:str,content:str) -> str:
    try:
        with open(out_path,'a',encoding="utf-8") as f:
            f.write(content)
    except Exception as e:
        logger.error(f"写文件时出错{e}")
