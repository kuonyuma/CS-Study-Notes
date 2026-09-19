import sys
import time
import redis

if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")

r = redis.Redis(host="127.0.0.1", port=6380, decode_responses=True)

SESSION_TTL = 3  # 会话闲置超时时间：3 秒

def login(token: str, user_info: str):
    """用户登录：创建 Session 并设置初始 TTL"""
    key = f"session:{token}"
    r.set(key, user_info, ex=SESSION_TTL)
    print(f"[登录] 创建 Session: {token}，初始 TTL = {SESSION_TTL}s")

def get_session_and_touch(token: str) -> str | None:
    """访问受保护资源：获取 Session 同时触发滑动续期"""
    key = f"session:{token}"
    
    # 利用 Redis 6.2+ 提供的 GETEX 命令：一次网络往返完成读取 + 刷新 TTL
    # 也可以用 r.get(key) + if data: r.expire(key, SESSION_TTL)
    data = r.getex(key, ex=SESSION_TTL)  # 👈 核心机制：GETEX 访问即自动滑动续期
    
    if data:
        print(f"[访问放行] 用户: {data}，已自动滑动续期！当前 TTL 重置回: {r.ttl(key)}s")
        return data
    else:
        print(f"[已过期 401] Token {token} 闲置超时已失效，请重新登录！")
        return None

if __name__ == "__main__":
    token = "token_abc123"
    r.delete(f"session:{token}")

    # 1. 登录
    login(token, "User_Bob")

    # 2. 模拟持续活跃：每隔 1.8 秒操作一次（小于 3 秒 TTL）
    # 连续操作 3 次，总耗时约 5.4 秒（已经超过了原本的 3 秒寿命，但因为一直活跃，永不掉线！）
    for i in range(1, 4):
        time.sleep(1.8)
        print(f"--- 经过 1.8 秒后第 {i} 次操作系统 ---")
        get_session_and_touch(token)

    # 3. 模拟用户离开电脑（闲置超时超过 3 秒）
    print("\n--- 用户去喝咖啡，闲置 3.5 秒不操作 ---")
    time.sleep(3.5)
    get_session_and_touch(token)
