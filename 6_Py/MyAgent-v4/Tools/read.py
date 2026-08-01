def read(path: str) -> str:
    """读取文件内容
    :param path: 文件或目录路径
    :return:返回读取的内容
    :raises FileNotFoundError: 文件不存在时抛出
    :raises OSError: 读取失败时抛出
    """
    with open(path, "r", encoding="utf-8") as f:
        return f.read()
