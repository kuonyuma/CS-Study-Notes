"""创建文件,写入,读取"""
from pathlib import Path

path3 = Path("hello.text")

path3.touch(exist_ok=True)

path3.write_text("你好呀",encoding="utf-8")

config = path3.read_text(encoding="utf-8")
print(config)
print(path3.resolve())