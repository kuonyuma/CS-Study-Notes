import sys
import time
import json
import redis

# 确保 Windows 终端中文输出不乱码
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")

# 1. 初始化 Redis 连接
r = redis.Redis(host="127.0.0.1", port=6380, decode_responses=True)

# 2. 模拟真实数据库（只有两个合法用户，没有 9999）
fake_db = {
    1001: {"name": "alice", "age": 18},
    1002: {"name": "bob", "age": 20},
}

# 全局计数器：记录数据库实际被查询的次数
db_query_count = 0

def query_db(user_id: int):
    """模拟查询底层数据库：包含磁盘 I/O 和网络开销，每次耗时约 10ms (0.01s)"""
    global db_query_count
    db_query_count += 1
    time.sleep(0.01)  # 👈 核心模拟：真实 DB 查询是有耗时的（如 10ms）
    return fake_db.get(user_id)


# ==========================================
# 方案 A：不管（直穿 DB，引发缓存穿透）
# ==========================================
def get_user_without_null_cache(user_id: int):
    key = f"user:{user_id}"

    # 1. 查 Redis
    cached = r.get(key)
    if cached is not None:
        return json.loads(cached)

    # 2. 缓存未命中，穿透到 DB（耗时 10ms）
    user = query_db(user_id)

    # 3. 只有 DB 有数据才写缓存；查不到时啥也不干！
    if user is not None:
        r.set(key, json.dumps(user), ex=300)
    # 👈 核心漏洞：未缓存空值，导致每次请求都重新打到慢速 DB 上
    return user


# ==========================================
# 方案 B：Redis 缓存空值（解决缓存穿透）
# ==========================================
NULL_SENTINEL = "@@NULL@@"  # 空值哨兵标记

def get_user_with_null_cache(user_id: int):
    key = f"user_safe:{user_id}"

    # 1. 查 Redis（内存级高速查询，< 0.5ms）
    cached = r.get(key)
    if cached is not None:
        if cached == NULL_SENTINEL:  # 👈 核心机制：命中空值标记，高速短路返回！
            return None
        return json.loads(cached)

    # 2. 仅首次穿透到 DB（耗时 10ms）
    user = query_db(user_id)

    # 3. 回写 Redis
    if user is not None:
        r.set(key, json.dumps(user), ex=300)
    else:
        # 👈 核心机制：查无此人，写入空值标记并设短 TTL（如 60s），阻断后续请求
        r.set(key, NULL_SENTINEL, ex=60)

    return user


# ==========================================
# 对比测试：模拟 100 次大量请求查询不存在的 ID
# ==========================================
if __name__ == "__main__":
    target_id = 9999  # 数据库中不存在的非法 ID
    total_requests = 100

    print("=" * 65)
    print(f"🔥 测试开始：模拟 {total_requests} 次外部请求查询不存在的数据 (ID={target_id})")
    print("   (假设单次 DB 查询耗时 10ms，Redis 查询耗时 < 1ms)")
    print("=" * 65)

    # ---------- 测试 1：方案 A（不管） ----------
    r.delete(f"user:{target_id}")
    db_query_count = 0

    print("\n[测试 1] 方案 A：不管（不存空值）")
    start_time_a = time.perf_counter()
    for _ in range(total_requests):
        get_user_without_null_cache(target_id)
    cost_time_a = time.perf_counter() - start_time_a

    print(f"-> 请求总数: {total_requests} 次")
    print(f"-> 数据库被查询: {db_query_count} 次 (穿透率: {db_query_count / total_requests * 100:.0f}%)")
    print(f"-> ⏱️ 耗时统计: 总耗时 {cost_time_a * 1000:.2f} ms ({cost_time_a:.3f} 秒)，平均每次 {cost_time_a / total_requests * 1000:.2f} ms")
    print("-> 结果：每次都穿透打 DB，耗时长、数据库压力拉满！")

    # ---------- 测试 2：方案 B（缓存空值） ----------
    r.delete(f"user_safe:{target_id}")
    db_query_count = 0

    print("\n[测试 2] 方案 B：解决（Redis 缓存空值标记）")
    start_time_b = time.perf_counter()
    for _ in range(total_requests):
        get_user_with_null_cache(target_id)
    cost_time_b = time.perf_counter() - start_time_b

    print(f"-> 请求总数: {total_requests} 次")
    print(f"-> 数据库被查询: {db_query_count} 次 (穿透率: {db_query_count / total_requests * 100:.0f}%)")
    print(f"-> ⏱️ 耗时统计: 总耗时 {cost_time_b * 1000:.2f} ms ({cost_time_b:.3f} 秒)，平均每次 {cost_time_b / total_requests * 1000:.2f} ms")
    print("-> 结果：仅第 1 次查 DB，后面 99 次纯内存直出，耗时极低！")

    print("\n" + "=" * 65)
    speedup = cost_time_a / cost_time_b if cost_time_b > 0 else 0
    print(f"🚀 性能对比结论：方案 B 比 方案 A 快了 【{speedup:.1f} 倍】！")
    print(f"   方案 A 耗时: {cost_time_a * 1000:.1f} ms | 方案 B 耗时: {cost_time_b * 1000:.1f} ms")
    print("=" * 65)
