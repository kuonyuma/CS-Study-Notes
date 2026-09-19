import sys
import time
import redis

# 确保 Windows 终端中文输出不乱码
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")

r = redis.Redis(host="127.0.0.1", port=6380, decode_responses=True)

def access_api(user_id: str, max_limit: int = 3, window_seconds: int = 4) -> bool:
    """极简固定窗口限流器：利用 INCR + EXPIRE (TTL)"""
    key = f"ratelimit:{user_id}"

    # 1. 计数原子递增
    count = r.incr(key)  # 👈 核心机制：INCR 自增记录请求次数

    # 2. 如果是当前窗口的第 1 次访问，设置 TTL 定义时间窗口
    if count == 1:
        r.expire(key, window_seconds)  # 👈 核心机制：首次触发时绑定 TTL 窗口

    # 3. 校验是否超限
    if count > max_limit:
        ttl = r.ttl(key)
        print(f"[429 限流] 用户 {user_id} 访问过于频繁！当前次数: {count}/{max_limit}，窗口剩余重置时间: {ttl}s")
        return False

    print(f"[200 放行] 用户 {user_id} 访问成功。当前次数: {count}/{max_limit}")
    return True

if __name__ == "__main__":
    user = "user_9527"
    r.delete(f"ratelimit:{user}")  # 重置测试数据

    print("=== 1. 模拟在 4 秒内快速连发 4 次请求（限额 3 次） ===")
    for i in range(1, 5):
        access_api(user, max_limit=3, window_seconds=4)

    print("\n=== 2. 等待 4 秒窗口自然结束 (TTL 归零) ===")
    time.sleep(4.5)

    print("=== 3. 窗口重置后再次发起请求 ===")
    access_api(user, max_limit=3, window_seconds=4)
