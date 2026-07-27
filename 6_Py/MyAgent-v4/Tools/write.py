import logging


def write(path:str,content:str) -> str:
    """将内容写入指定路径的文件。
     :param path: 文件路径。
     :param content: 文件完整内容。
     :return: 提示字符串。
     :raises Exception: 写入出错。
     """
    try:
        with open(path,'w',encoding="utf-8") as f:
            f.write(content)
            return "写入成功"
    except Exception as e:
        logging.error(f"写文件时出错{e}")
        return f"错误: 写入文件失败 - {e}"
