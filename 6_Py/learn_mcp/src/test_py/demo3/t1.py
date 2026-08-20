from src.test_py.demo7.demo1.counter import add
import sys

for path in sys.path:
    print(path)

print(add(1, 2))
