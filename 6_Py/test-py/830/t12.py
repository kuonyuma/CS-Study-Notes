from collections.abc import Mapping
from typing import Any

def show_user(user: Mapping[str, object]) -> None:
    print(user["name"])

bean = {"name":"alice"}

# show_user(bean)


config = {
    "host": "localhost",
    "port": 8080
}


# print(config["port"]  == config.get("port"))

data = {
    "gemini_api_key":"sk....",
    "gpt_api_key":None
}

print(data.get("claude",False))

print(len(data))
print("gemini_api_key" in data)

for key in data:
    print(key)

print(data.keys())
print(data.values())

for key,value in data.items():
    print(key,value)

keys = data.keys()
data_list = list(data.keys())
print(data_list)
print(keys)
data["debug"] = True
print(keys)
print(data_list)


def read_config(mapping:object):

    if not isinstance(mapping,Mapping):
        print("对象必须是Mapping")
    return mapping.get("gemini_api_key")

result = read_config(data)
print(result)

print()
print()
print()
print()
print()

def add(a,b):
    return a+b

print(type(add))
print(add.__call__(1,2))

class Dog:
    def __call__(self) -> Any:
        print("狗叫中...")

dog = Dog()
dog()
from collections.abc import Callable
print(isinstance(dog,Callable))

print(callable(dog))