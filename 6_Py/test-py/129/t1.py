import redis

# 1. 建立与 Redis 容器的连接
r = redis.Redis(
    host="127.0.0.1",
    port=6380,
    decode_responses=True,  # 关键：自动把 Redis 返回的 bytes 二进制解码成 Python 字符串
)

# 2. 存入手机短信验证码，且设置 300 秒（5分钟）自动销毁
r.set("verify_code:13800138000", "483921", ex=300)

# 3. 用户输入验证码后，程序去 Redis 取出核对
code = r.get("verify_code:13800138000")
print(code)  # 输出 "483921"，5分钟后再次查询就会变成 None