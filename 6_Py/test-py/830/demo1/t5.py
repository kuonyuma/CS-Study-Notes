import json

text = '{"name": "demo", "enabled": true, "count": 2}'
text_again = json.loads(text)
print(text_again['name'],text_again['enabled'])
print(type(text))
print(type(text_again))
text_again = json.dumps(text_again)
print(type(text_again))
print(text_again)