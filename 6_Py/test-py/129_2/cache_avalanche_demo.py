import random
import sys
import time
import redis

# 确保 Windows 终端中文输出不乱码
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")

# 1. 初始化 Redis 连接
r = redis.Redis(host="127.0.0.1", port=6380, decode_responses=True)

# 2. 模拟数据库与查询计数器
TOTAL_KEYS = 50
fake_db = {f"item:{i}": f"商品数据_{i}" for i in range(1, TOTAL_KEYS + 1)}
db_query_count = 0


def query_db(key: str):
    """模拟数据库慢查询"""
    global db_query_count
    db_query_count += 1
    return fake_db.get(key)


# ==========================================
# 方案 A：不做处理（所有 Key 设置相同的固定 TTL）
# ==========================================
def warm_up_fixed_ttl(base_ttl: int = 2):
    """预热数据：所有 key 设置完全一致的过期时间"""
    for key, val in fake_db.items():
        # 👈 核心漏洞：大量 Key 在同一时刻设置相同 TTL，必然在同一时间集中失效！
        r.set(f"fixed:{key}", val, ex=base_ttl)


def get_fixed_ttl(key: str):
    cached = r.get(f"fixed:{key}")
    if cached is not None:
        return cached
    # 缓存失效，回源查询 DB 并重建
    data = query_db(key)
    r.set(f"fixed:{key}", data, ex=2)
    return data


# ==========================================
# 方案 B：随机 TTL（基础 TTL + 随机时间抖动 Jitter）
# ==========================================
def warm_up_random_ttl(base_ttl: int = 2, jitter_range: tuple = (1, 3)):
    """预热数据：每个 key 的过期时间增加随机扰动，打散失效时间点"""
    for key, val in fake_db.items():
        # 👈 核心机制：base_ttl + random_jitter，将集中失效时间打散到不同时间段
        random_ttl = base_ttl + random.randint(jitter_range[0], jitter_range[1])
        r.set(f"random:{key}", val, ex=random_ttl)


def get_random_ttl(key: str):
    cached = r.get(f"random:{key}")
    if cached is not None:
        return cached
    # 缓存失效，回源查询 DB 并重建（同样加上随机 TTL）
    data = query_db(key)
    r.set(f"random:{key}", data, ex=2 + random.randint(1, 3))
    return data


# ==========================================
# 运行对比测试
# ==========================================
if __name__ == "__main__":
    print("=" * 68)
    print(f"🔥 缓存雪崩场景模拟：{TOTAL_KEYS} 个数据同时预热写入缓存")
    print("=" * 68)

    # ----------------------------------------------------
    # 测试 1：方案 A（固定 TTL，未做打散处理）
    # ----------------------------------------------------
    print("\n[测试 1] 方案 A：固定 TTL (全部固定 2 秒)")
    # 清理旧 key
    for k in fake_db:
        r.delete(f"fixed:{k}")
    db_query_count = 0

    # 预热写入缓存
    warm_up_fixed_ttl(base_ttl=2)
    print(f"-> 成功预热 {TOTAL_KEYS} 个 Key，TTL 均为 2 秒。")
    print("-> 等待 2.2 秒，让固定 TTL 到期...")
    time.sleep(2.2)

    # 发起全量访问
    print(f"-> 瞬间涌入 {TOTAL_KEYS} 个请求同时读取这些 Key...")
    for k in fake_db:
        get_fixed_ttl(k)

    print(f"-> 数据库被查询: {db_query_count} 次 / 共 {TOTAL_KEYS} 次请求")
    print(f"-> DB 瞬时击穿率: {db_query_count / TOTAL_KEYS * 100:.0f}%")
    print("-> 结果：💥 所有 Key 在同一秒集中失效，所有请求全部砸向 DB，引发【缓存雪崩】！")

    # ----------------------------------------------------
    # 测试 2：方案 B（随机 TTL：基础 2 秒 + 随机 1~3 秒）
    # ----------------------------------------------------
    print("\n" + "-" * 68)
    print("[测试 2] 方案 B：随机 TTL (基础 2 秒 + 随机 1~3 秒抖动，TTL 分布在 3~5 秒)")
    # 清理旧 key
    for k in fake_db:
        r.delete(f"random:{k}")
    db_query_count = 0

    # 预热写入缓存
    warm_up_random_ttl(base_ttl=2, jitter_range=(1, 3))

    # 统计实际 TTL 分布
    ttl_dist = {}
    for k in fake_db:
        t = r.ttl(f"random:{k}")
        ttl_dist[t] = ttl_dist.get(t, 0) + 1
    sorted_dist = sorted(ttl_dist.items())
    dist_str = ", ".join([f"{t}秒: {cnt}个" for t, cnt in sorted_dist])
    print(f"-> 成功预热 {TOTAL_KEYS} 个 Key，失效时间已被打散 -> [{dist_str}]")

    print("-> 同样等待 2.2 秒（此时固定方案早已全部失效）...")
    time.sleep(2.2)

    # 在第 2.2 秒时发起全量访问
    for k in fake_db:
        get_random_ttl(k)

    print(f"-> 第 2.2 秒瞬时访问：DB 被查询: {db_query_count} 次（穿透率: {db_query_count / TOTAL_KEYS * 100:.0f}%）")
    print("-> 结果：🛡️ 基础失效时刻到来时，所有 Key 依然存活，DB 冲击为 0！成功避开瞬间雪崩！")

    # 继续观察后续分批失效削峰效果
    print("-> 继续等待 1.1 秒（到达第 3.3 秒），观察分批失效削峰...")
    time.sleep(1.1)
    db_query_count = 0
    for k in fake_db:
        get_random_ttl(k)
    print(f"-> 第 3.3 秒再次访问：仅 TTL=3s 的少量 Key 失效，DB 被查询: {db_query_count} 次 (削峰打散！)")
    print("=" * 68)
