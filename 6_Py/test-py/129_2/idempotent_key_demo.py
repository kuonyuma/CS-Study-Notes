import time
import redis

# 连接 Redis (端口 6380)
r = redis.Redis(host="127.0.0.1", port=6380, decode_responses=True)

def process_payment(order_id: str) -> str:
    """处理扣款请求：基于幂等 Key 与 TTL"""
    key = f"idempotent:pay:{order_id}"
    
    # 核心操作：SET NX EX 原子加锁并设置防重窗口
    # nx=True：仅首次未处理时写入成功；ex=3：保留 3 秒幂等窗口，防止无休止占用内存
    is_first_time = r.set(key, "SUCCESS", nx=True, ex=3)  # 👈 核心机制：SET NX EX 原子写入与窗口期

    if not is_first_time:
        return f"[拦截] 订单 {order_id} 正在处理或已扣款，拒绝重复提交！"
    
    return f"[扣款成功] 订单 {order_id} 处理完毕，幂等凭据已写入 Redis。"

if __name__ == "__main__":
    order_id = "ORD_20260915_001"
    r.delete(f"idempotent:pay:{order_id}")  # 清理测试数据

    print("--- 1. 模拟短时间内连续点击/重试 ---")
    print(process_payment(order_id))  # 首次请求
    print(process_payment(order_id))  # 重复点击，触发幂等拦截

    print("\n--- 2. 等待 4 秒，使幂等 Key 达到 TTL 自然过期 ---")
    time.sleep(4)
    print(f"当前 Key 剩余 TTL: {r.ttl(f'idempotent:pay:{order_id}')} (已过期被自动清理)")
    print(process_payment(order_id))  # 过期后允许新业务窗口再次提交
