import logging


def read(path:str) -> str:
    try:
        with open(path,'r',encoding="utf-8") as f:
            return f.read()
    except FileNotFoundError as e:
        logger.error(f"未找到文件:{e}")
    except Exception as e:
        logger.error(f"读取文件时出错{e}")
