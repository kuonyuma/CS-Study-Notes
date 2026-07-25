import logging

def read(path:str) -> str:
    """读写文件内容
    :param path: 文件或目录路径
    :return:返回读取的内容
    :raises
        FileNotFoundError: 文件不存在时抛出异常，返回f"错误: 文件未找到:{path}"
        Exception： 读取时遇到的其他异常，返回f"错误: 读取文件失败 - {e}"
    """
    try:
        with open(path,'r',encoding="utf-8") as f:
            return f.read()
    except FileNotFoundError as e:
        logging.error(f"未找到文件:{e}")
        return f"错误: 文件未找到:{path}"
    except Exception as e:
        logging.error(f"读取文件时出错{e}")
        return f"错误: 读取文件失败 - {e}"
