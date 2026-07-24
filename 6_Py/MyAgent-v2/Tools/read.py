import logging

def read(params: dict) -> str:
    try:
        path = params.get("path","")
        with open(path,'r',encoding="utf-8") as f:
            return f.read()
    except FileNotFoundError as e:
        logging.error(f"未找到文件:{e}")
        return f"错误: 文件未找到:{path}"
    except Exception as e:
        logging.error(f"读取文件时出错{e}")
        return f"错误: 读取文件失败 - {e}"
