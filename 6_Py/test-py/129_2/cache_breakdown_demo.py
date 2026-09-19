import json
import sys
import threading
import time
from concurrent.futures import ThreadPoolExecutor
import redis

# 确保 Windows 终端中文输出不乱码
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")

# 1. 初始化 Redis 连接
r = redis.Redis(host="127.0.0.1", port=6380, decode_responses=True)

# 2. 模拟真实数据库（热点数据）
fake_db = {
    "item:1001": {"id": 1001, "name": "秒杀旗舰手机", "stock": 99}
}

# 模拟数据库连接池：真实数据库连接数非常有限（此处模拟最大 2 个并发连接）
# 击穿发生时，大量请求会因争抢连接而在队列中排队阻塞！
db_connection_pool = threading.Semaphore(2)

# 统计指标
db_query_count = 0
db_total_io_time = 0.0
stats_lock = threading.Lock()

def reset_db_stats():
    global db_query_count, db_total_io_time
    db_query_count = 0
    db_total_io_time = 0.0

def query_db(key: str):
    """模拟真实数据库查询：受连接池大小限制 + 固定的 IO 耗时"""
    global db_query_count, db_total_io_time
    # 模拟等待与获取数据库连接（连接池打满后在此处排队阻塞）
    with db_connection_pool:
        t0 = time.perf_counter()
        time.sleep(0.05)  # 模拟单次查询 50ms 的 SQL 执行与磁盘 IO 耗时
        io_duration = time.perf_counter() - t0

        with stats_lock:
            db_query_count += 1
            db_total_io_time += io_duration

    return fake_db.get(key)


# ==========================================
# 方案 A：不做处理（热点 key 过期，并发请求直接打垮连接池）
# ==========================================
def get_without_lock(key: str):
    # 1. 查缓存
    cached = r.get(key)
    if cached is not None:
        return json.loads(cached)

    # 2. 缓存失效：无并发控制，所有请求同时涌入争抢 DB 连接，产生排队耗时！
    data = query_db(key)

    # 3. 回写缓存
    if data is not None:
        r.set(key, json.dumps(data), ex=300)
    return data


# ==========================================
# 方案 B：互斥锁（只允许一个请求去重建 key）
# ==========================================
def get_with_mutex_lock(key: str, max_retries: int = 50):
    lock_key = f"lock:{key}"

    for _ in range(max_retries):
        # 1. 先查缓存
        cached = r.get(key)
        if cached is not None:
            return json.loads(cached)

        # 2. 尝试获取互斥锁（SET NX EX）
        # 👈 核心机制：SETNX 保证仅 1 个请求能进入查库，避免连接池被打满排队
        acquired = r.set(lock_key, "1", nx=True, ex=5)
        if acquired:
            try:
                # 2.1 Double-Check：避免拿到锁时前一个线程刚写完
                cached = r.get(key)
                if cached is not None:
                    return json.loads(cached)

                # 2.2 独占去查 DB 并回写缓存
                data = query_db(key)
                if data is not None:
                    r.set(key, json.dumps(data), ex=300)
                return data
            finally:
                # 2.3 释放互斥锁
                r.delete(lock_key)
        else:
            # 3. 没抢到锁的请求，短暂睡眠等待前序线程重建完毕
            time.sleep(0.01)

    # 兜底降级（重试超时仍未拿到）
    return query_db(key)


# ==========================================
# 辅助评测函数：记录每个请求耗时和总体指标
# ==========================================
def run_benchmark(test_name: str, target_func, key: str, total_threads: int):
    # 准备工作：清空 Redis 缓存与锁
    r.delete(key)
    r.delete(f"lock:{key}")
    reset_db_stats()

    barrier = threading.Barrier(total_threads)
    latencies = []
    latency_lock = threading.Lock()

    def timed_worker():
        barrier.wait()  # 在同一毫秒瞬间释放所有并发请求
        t_start = time.perf_counter()
        target_func(key)
        t_cost = (time.perf_counter() - t_start) * 1000  # 转为 ms
        with latency_lock:
            latencies.append(t_cost)

    total_start = time.perf_counter()
    with ThreadPoolExecutor(max_workers=total_threads) as executor:
        futures = [executor.submit(timed_worker) for _ in range(total_threads)]
        for f in futures:
            f.result()
    total_cost_ms = (time.perf_counter() - total_start) * 1000

    avg_latency = sum(latencies) / len(latencies)
    max_latency = max(latencies)
    min_latency = min(latencies)

    print(f"\n[{test_name}]")
    print(f"  ├─ 并发请求数:       {total_threads} 个")
    print(f"  ├─ 数据库被查次数:   {db_query_count} 次")
    print(f"  ├─ 数据库累计承受耗时: {db_total_io_time * 1000:.1f} ms")
    print(f"  ├─ 全部完成总耗时:   {total_cost_ms:.1f} ms  👈 (宏观耗时)")
    print(f"  ├─ 平均请求响应时间: {avg_latency:.1f} ms")
    print(f"  └─ 最慢请求响应时间: {max_latency:.1f} ms (最快: {min_latency:.1f} ms)")


# ==========================================
# 并发性能压测对比
# ==========================================
if __name__ == "__main__":
    target_key = "item:1001"
    concurrent_count = 20

    print("=" * 70)
    print(f"🔥 开始性能测试：模拟 {concurrent_count} 个并发请求在同一时刻访问过期的热点 Key")
    print(f"ℹ️  数据库环境模拟：单次查库耗时 50ms，连接池最大容量限制为 2 个连接")
    print("=" * 70)

    # 1. 运行方案 A：不做处理
    run_benchmark(
        test_name="方案 A：不做处理（发生击穿，DB 连接池排队雪崩）",
        target_func=get_without_lock,
        key=target_key,
        total_threads=concurrent_count
    )

    # 2. 运行方案 B：互斥锁
    run_benchmark(
        test_name="方案 B：互斥锁（仅 1 个重建，其余等待后读 Redis）",
        target_func=get_with_mutex_lock,
        key=target_key,
        total_threads=concurrent_count
    )

    print("\n" + "=" * 70)
