import json

text = '{"name":"张三"}'

text_bean = json.loads(text)
print(type(text_bean))
print(text_bean)
print(text_bean["name"])

text = json.dumps(text_bean,ensure_ascii=False)
print(type(text))
print(text)