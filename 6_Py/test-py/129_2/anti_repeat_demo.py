import uuid
import redis

# 1. 连接本地 Redis 服务（端口 6380）
r = redis.Redis(host='127.0.0.1', port=6380, decode_responses=True)

def handle_request(request_id: str) -> bool:
    """处理客户端请求，防止重复提交"""
    key = f"request:{request_id}"
    
    # 利用 Redis 的 SET NX EX 原子操作：
    # nx=True：仅当 key 不存在时才写入成功（防并发/重复）
    # ex=10：设置 10 秒防重窗口期，过期后自动释放
    is_success = r.set(key, "1", ex=10, nx=True)  # 👈 核心机制：SET NX EX 原子加锁/防重

    if not is_success:
        print(f"[拒绝] request_id={request_id} 请求已处理中或提交过，拒绝重复请求！")
        return False

    print(f"[成功] request_id={request_id} 首次提交，正常处理业务逻辑。")
    return True

if __name__ == "__main__":
    # 生成客户端唯一的 request_id (UUID)
    req_id = str(uuid.uuid4())

    # 清理旧数据保证测试一致
    r.delete(f"request:{req_id}")

    print("--- 模拟客户端使用同一个 request_id 连续请求两次 ---")
    # 第 1 次请求：key 不存在，SET NX 返回成功
    handle_request(req_id)

    # 第 2 次重复请求：key 已存在，SET NX 返回 None，被拒绝
    handle_request(req_id)
