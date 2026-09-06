"""
project/data/users/001.json

"""

from pathlib import Path

base = Path("project")
print(base / "data" / "users" / "001.json")

"""
请写代码：
- 如果路径不存在，输出 "路径不存在"
- 如果是文件，输出 "这是文件"
- 如果是目录，输出 "这是目录"
"""

path = Path("data")
if not path.exists():
    print("路径不存在")
elif path.is_dir():
    print("这是目录")
else:
    print("这是文件")

"""data/users/images/avatar
"""
path1 = Path("data/users/images/avatar")
path1.mkdir(parents=True,exist_ok=True)

