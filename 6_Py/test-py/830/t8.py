import json

data = {
    "name": "张三",
    "age": 18,
}

with open("config.json", "w", encoding="utf-8") as f:
    json.dump(data,f,ensure_ascii=False,indent=3)

with open("config.json","r",encoding="utf-8") as f:
    text = json.load(f)
    
print(type(text))
print(text)
