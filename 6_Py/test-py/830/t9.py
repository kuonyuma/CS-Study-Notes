import hashlib


text = "hello"
bean = hashlib.sha256()
print(type(bean))
bean.update(text.encode("utf-8"))
result = bean.hexdigest()
print(result)