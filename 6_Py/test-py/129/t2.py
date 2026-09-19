import redis


bean = redis.Redis(
    host="127.0.0.1",
    port=6380,
    decode_responses=True,
)

tmp = bean.set("kuonyuma","ryuke中文じゃぱあん",ex=100)
print(tmp)

result = bean.get("kuonyuma")
print(result)