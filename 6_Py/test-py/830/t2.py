from pathlib import Path

"""
info.json
info
.json
data/users
"""
path = Path("data/users/info.json")

print(path.name)
print(path.stem)
print(path.suffix)
print(path.parent)