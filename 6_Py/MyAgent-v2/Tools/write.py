import logging


def write(params: dict) -> str:
    try:
        out_path = params.get("path","")
        content = params.get("content","")
        with open(out_path,'w',encoding="utf-8") as f:
            f.write(content)
            return "写入成功"
    except Exception as e:
        logging.error(f"写文件时出错{e}")
        return "写入失败"
