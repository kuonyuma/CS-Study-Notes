import sys
import time
import redis

if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")

r = redis.Redis(host="127.0.0.1", port=6380, decode_responses=True)

def heartbeat(user_id: str, timeout_seconds: int = 3):
    """客户端定期上报心跳：重置 TTL 保活"""
    key = f"online:{user_id}"
    # 只要发送心跳，就向 Redis 刷新 key 并重设 TTL
    r.set(key, "1", ex=timeout_seconds)  # 👈 核心机制：心跳持续通过 TTL 续命

def check_status(user_id: str):
    """查询用户当前在线状态"""
    key = f"online:{user_id}"
    ttl = r.ttl(key)
    if ttl > 0:
        print(f"[在线 🟢] 用户 {user_id} 正在活跃，心跳倒计时还剩: {ttl}s")
    else:
        print(f"[离线 🔴] 用户 {user_id} 已断开连接 (Key 已随 TTL 自动销毁)")

if __name__ == "__main__":
    user = "alice_007"
    r.delete(f"online:{user}")

    print("=== 1. Alice 上线，客户端发送第 1 次心跳 (TTL 设为 3 秒) ===")
    heartbeat(user, timeout_seconds=3)
    check_status(user)

    print("\n=== 2. 过了 1.5 秒，客户端发送第 2 次心跳续命 ===")
    time.sleep(1.5)
    heartbeat(user, timeout_seconds=3)
    check_status(user)

    print("\n=== 3. 模拟 Alice 突发断网/进程闪退（不再发送心跳），等待 3.5 秒 ===")
    time.sleep(3.5)
    check_status(user)
