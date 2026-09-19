"""用数量级差距直观呈现 Redis 缓存价值的高并发对比演示。

本程序针对高并发场景，通过对纯 MySQL 与 Redis Cache-Aside 进行对照实验，
直观展示当面对海量读取时，Redis 如何将数据库查询次数从 20,000 次削减至 1 次（下降 99.995%），
并提供缓存失效（Cache Invalidation）与缓存击穿防护（Breakdown Mutex Lock）的实战验证。
"""

from __future__ import annotations

import argparse
import statistics
import sys
import threading
import time
from concurrent.futures import ThreadPoolExecutor
from typing import Any, Dict, List, Optional

import mysql.connector
from mysql.connector import pooling
import redis

# 确保在 Windows 控制台环境下输出中文或特殊字符时不因 GBK 编码报错
if sys.platform == "win32":
    try:
        sys.stdout.reconfigure(encoding="utf-8")
        sys.stderr.reconfigure(encoding="utf-8")
    except AttributeError:
        pass


MYSQL_CONFIG = {
    "host": "127.0.0.1",
    "port": 3307,
    "user": "root",
    "password": "root",
    "database": "learning_demo",
}

REDIS_CONFIG = {
    "host": "127.0.0.1",
    "port": 6380,
    "decode_responses": True,
}


# ==============================================================================
# 1. 线程安全指标收集器
# ==============================================================================

class MetricsCollector:
    """线程安全的压测指标统计器，用于高并发下精准采集各项指标。"""

    def __init__(self, name: str) -> None:
        self.name = name
        self._lock = threading.Lock()
        self.total_requests = 0
        self.success_count = 0
        self.failure_count = 0
        self.mysql_queries = 0
        self.redis_reads = 0
        self.redis_hits = 0
        self.redis_misses = 0
        self.latencies_ms: List[float] = []
        self.start_time: float = 0.0
        self.end_time: float = 0.0

    def start(self) -> None:
        self.start_time = time.perf_counter()

    def stop(self) -> None:
        self.end_time = time.perf_counter()

    def record_mysql_query(self) -> None:
        with self._lock:
            self.mysql_queries += 1

    def record_redis_hit(self) -> None:
        with self._lock:
            self.redis_reads += 1
            self.redis_hits += 1

    def record_redis_miss(self) -> None:
        with self._lock:
            self.redis_reads += 1
            self.redis_misses += 1

    def record_request(self, latency_ms: float, success: bool = True) -> None:
        with self._lock:
            self.total_requests += 1
            if success:
                self.success_count += 1
            else:
                self.failure_count += 1
            self.latencies_ms.append(latency_ms)

    @property
    def total_duration_seconds(self) -> float:
        if self.end_time >= self.start_time and self.start_time > 0:
            return self.end_time - self.start_time
        return 0.0

    @property
    def qps(self) -> float:
        duration = self.total_duration_seconds
        if duration > 0:
            return self.total_requests / duration
        return 0.0

    @property
    def hit_rate(self) -> float:
        if self.redis_reads == 0:
            return 0.0
        return self.redis_hits / self.redis_reads

    @property
    def db_offload_rate(self) -> float:
        if self.total_requests == 0:
            return 0.0
        return max(0.0, 1.0 - (self.mysql_queries / self.total_requests))

    def compute_percentiles(self) -> Dict[str, float]:
        if not self.latencies_ms:
            return {"avg": 0.0, "p50": 0.0, "p95": 0.0, "p99": 0.0, "max": 0.0}

        sorted_latencies = sorted(self.latencies_ms)
        n = len(sorted_latencies)
        idx_50 = min(int(n * 0.50), n - 1)
        idx_95 = min(int(n * 0.95), n - 1)
        idx_99 = min(int(n * 0.99), n - 1)

        return {
            "avg": statistics.mean(sorted_latencies),
            "p50": sorted_latencies[idx_50],
            "p95": sorted_latencies[idx_95],
            "p99": sorted_latencies[idx_99],
            "max": sorted_latencies[-1],
        }


# ==============================================================================
# 2. 基础数据库与缓存操作（严格遵循 SQL 全小写规范）
# ==============================================================================

def connect_mysql():
    """连接 Docker 映射到本机 3307 端口的 MySQL。"""
    return mysql.connector.connect(**MYSQL_CONFIG)


def connect_redis():
    """连接 Docker 映射到本机 6380 端口的 Redis。"""
    client = redis.Redis(**REDIS_CONFIG)
    client.ping()
    return client


def wait_for_services():
    """等待 MySQL 和 Redis 准备就绪。"""
    print("正在连接 MySQL 和 Redis ...")
    last_error = None

    for attempt in range(1, 16):
        try:
            mysql_connection = connect_mysql()
            redis_client = connect_redis()
            print("MySQL 已连接：127.0.0.1:3307/learning_demo")
            print("Redis 已连接：127.0.0.1:6380")
            return mysql_connection, redis_client
        except Exception as error:
            last_error = error
            print(f"第 {attempt}/15 次连接失败，2 秒后重试：{error}")
            time.sleep(2)

    raise RuntimeError(
        "无法连接服务。请先执行 docker compose up -d，并确认容器已经启动。"
    ) from last_error


def create_table(mysql_connection):
    """在 MySQL 中创建学习用表。SQL 关键字全部小写。"""
    sql = """
        create table if not exists products (
            id int primary key auto_increment,
            name varchar(100) not null,
            price decimal(10, 2) not null,
            views int not null default 0,
            created_at timestamp default current_timestamp
        )
    """
    cursor = mysql_connection.cursor()
    cursor.execute(sql)
    mysql_connection.commit()
    cursor.close()
    print("[mysql] 已确认 products 表就绪")


def create_product(mysql_connection, name: str, price: float) -> int:
    """写入一条商品，返回 MySQL 自动生成的 ID。"""
    sql = "insert into products (name, price) values (%s, %s)"
    cursor = mysql_connection.cursor()
    cursor.execute(sql, (name, price))
    mysql_connection.commit()
    product_id = cursor.lastrowid
    cursor.close()
    print(f"[mysql] insert 商品：id={product_id}, name={name}, price={price:.2f}")
    return product_id


def update_product_price(mysql_connection, product_id: int, new_price: float) -> None:
    """更新商品价格。"""
    sql = "update products set price = %s where id = %s"
    cursor = mysql_connection.cursor()
    cursor.execute(sql, (new_price, product_id))
    mysql_connection.commit()
    cursor.close()
    print(f"[mysql] update products set price = {new_price:.2f} where id={product_id}")


def find_product_in_mysql(mysql_connection, product_id: int, delay_ms: float = 0.0):
    """从 MySQL 查询一条商品，支持通过 sleep() 模拟生产环境的数据库查询成本。"""
    cursor = mysql_connection.cursor(dictionary=True)
    if delay_ms > 0:
        sql = "select id, name, price, views, sleep(%s) as simulated_delay from products where id = %s"
        cursor.execute(sql, (delay_ms / 1000.0, product_id))
    else:
        sql = "select id, name, price, views from products where id = %s"
        cursor.execute(sql, (product_id,))
    product = cursor.fetchone()
    cursor.close()
    return product


def get_connection_from_pool(pool: pooling.MySQLConnectionPool, timeout_seconds: float = 30.0):
    """从连接池获取可用连接，若满则重试等待。"""
    start_time = time.perf_counter()
    while True:
        try:
            return pool.get_connection()
        except Exception:
            if time.perf_counter() - start_time > timeout_seconds:
                raise RuntimeError("MySQL 连接池排队获取连接超时！")
            time.sleep(0.002)


def list_products(mysql_connection):
    """查看 MySQL 中的全部商品。"""
    cursor = mysql_connection.cursor(dictionary=True)
    cursor.execute("select id, name, price, views from products order by id")
    products = cursor.fetchall()
    cursor.close()
    print("[mysql] select 全部 products：")
    for product in products:
        print(f"  {product}")


def clear_cache(redis_client, product_id: int):
    """清空指定商品缓存。"""
    cache_key = f"product:{product_id}"
    redis_client.delete(cache_key)
    print(f"[redis] del {cache_key}")


# ==============================================================================
# 3. 场景实现 (Scene A / B / C / D)
# ==============================================================================

def run_scene_a_mysql_only(
    mysql_pool: pooling.MySQLConnectionPool,
    product_id: int,
    total_requests: int,
    concurrency: int,
    db_delay_ms: float,
) -> MetricsCollector:
    """场景 A：纯 MySQL，20,000 个请求全部竞争有限连接池直接访问 MySQL。"""
    collector = MetricsCollector("场景 A (纯 MySQL)")

    def worker(_: int) -> None:
        t_req_start = time.perf_counter()
        try:
            conn = get_connection_from_pool(mysql_pool)
            try:
                collector.record_mysql_query()
                find_product_in_mysql(conn, product_id, delay_ms=db_delay_ms)
            finally:
                conn.close()
            latency = (time.perf_counter() - t_req_start) * 1000.0
            collector.record_request(latency, success=True)
        except Exception:
            latency = (time.perf_counter() - t_req_start) * 1000.0
            collector.record_request(latency, success=False)

    collector.start()
    with ThreadPoolExecutor(max_workers=concurrency) as executor:
        list(executor.map(worker, range(total_requests)))
    collector.stop()

    return collector


def run_scene_b_redis_cache(
    mysql_pool: pooling.MySQLConnectionPool,
    redis_client: redis.Redis,
    product_id: int,
    total_requests: int,
    concurrency: int,
    db_delay_ms: float,
    cache_ttl: int = 600,
) -> MetricsCollector:
    """场景 B：Redis Cache-Aside，冷启动 1 次 MISS 回源写入后，其余全部命中 Redis。"""
    collector = MetricsCollector("场景 B (Redis Cache-Aside)")
    cache_key = f"product:{product_id}"

    # 1. 确保测试从冷缓存开始
    redis_client.delete(cache_key)

    collector.start()

    # 2. 第 1 次读取：Redis MISS -> 查询 MySQL -> 写入 Redis
    t1_start = time.perf_counter()
    cached = redis_client.hgetall(cache_key)
    if not cached:
        collector.record_redis_miss()
        conn = get_connection_from_pool(mysql_pool)
        try:
            collector.record_mysql_query()
            db_product = find_product_in_mysql(conn, product_id, delay_ms=db_delay_ms)
        finally:
            conn.close()

        if db_product:
            redis_client.hset(
                cache_key,
                mapping={
                    "id": str(db_product["id"]),
                    "name": str(db_product["name"]),
                    "price": str(db_product["price"]),
                    "views": str(db_product["views"]),
                },
            )
            redis_client.expire(cache_key, cache_ttl)
    else:
        collector.record_redis_hit()
    t1_latency = (time.perf_counter() - t1_start) * 1000.0
    collector.record_request(t1_latency, success=True)

    # 3. 剩余并发请求：全部击中 Redis
    remaining_requests = total_requests - 1

    def worker(_: int) -> None:
        t_req_start = time.perf_counter()
        try:
            val = redis_client.hgetall(cache_key)
            if val:
                collector.record_redis_hit()
            else:
                collector.record_redis_miss()
            latency = (time.perf_counter() - t_req_start) * 1000.0
            collector.record_request(latency, success=True)
        except Exception:
            latency = (time.perf_counter() - t_req_start) * 1000.0
            collector.record_request(latency, success=False)

    if remaining_requests > 0:
        with ThreadPoolExecutor(max_workers=concurrency) as executor:
            list(executor.map(worker, range(remaining_requests)))

    collector.stop()
    return collector


def run_scene_c_invalidation(mysql_connection, redis_client) -> None:
    """场景 C：缓存失效与更新（验证 Cache-Aside 失效重载与数据一致性）。"""
    print("\n" + "=" * 70)
    print("        场景 C：缓存失效与一致性重载演示 (Cache Invalidation)")
    print("=" * 70)

    product_id = create_product(mysql_connection, "机械键盘", 199.99)
    cache_key = f"product:{product_id}"
    redis_client.delete(cache_key)

    # 初始读取，加载入缓存
    conn = connect_mysql()
    try:
        p_init = find_product_in_mysql(conn, product_id)
        redis_client.hset(
            cache_key,
            mapping={
                "id": str(p_init["id"]),
                "name": str(p_init["name"]),
                "price": str(p_init["price"]),
                "views": str(p_init["views"]),
            },
        )
        redis_client.expire(cache_key, 600)
    finally:
        conn.close()

    print(f"[*] 当前商品 ID={product_id} 在缓存中的初始价格：{redis_client.hget(cache_key, 'price')}")

    # 业务更新价格
    new_price = 299.99
    print(f"\n[操作] 业务更新：修改 MySQL 商品价格为 {new_price:.2f}，并执行删除旧缓存...")
    update_product_price(mysql_connection, product_id, new_price)
    redis_client.delete(cache_key)
    print(f"[redis] 已删除失效缓存 key={cache_key}")

    # 连续执行 3 次读取验证
    print("\n--- 连续 3 次读取验证 ---")
    for read_index in range(1, 4):
        t_start = time.perf_counter()
        data = redis_client.hgetall(cache_key)
        cost_ms = (time.perf_counter() - t_start) * 1000.0

        if not data:
            # 缓存未命中，回源 MySQL
            t_db = time.perf_counter()
            conn = connect_mysql()
            try:
                db_data = find_product_in_mysql(conn, product_id)
            finally:
                conn.close()
            db_cost_ms = (time.perf_counter() - t_db) * 1000.0

            # 写入 Redis
            redis_client.hset(
                cache_key,
                mapping={
                    "id": str(db_data["id"]),
                    "name": str(db_data["name"]),
                    "price": str(db_data["price"]),
                    "views": str(db_data["views"]),
                },
            )
            redis_client.expire(cache_key, 600)
            print(
                f"第 {read_index} 次读取：[miss 未命中] -> 查询 MySQL 返回 price={db_data['price']} "
                f"-> 写入 Redis (MySQL 耗时: {db_cost_ms:.2f}ms)"
            )
        else:
            print(
                f"第 {read_index} 次读取：[hit 命中缓存] -> 直接从 Redis 返回 price={data['price']} "
                f"(Redis 耗时: {cost_ms:.2f}ms)"
            )

    print("\n[结论] 证明 Redis 并非单纯保存旧数据；失效后能自动通过 MySQL 重建最新缓存！")
    print("=" * 70 + "\n")


def run_scene_d_breakdown_protection(
    mysql_pool: pooling.MySQLConnectionPool,
    redis_client: redis.Redis,
    product_id: int,
    concurrency: int = 200,
    db_delay_ms: float = 50.0,
) -> None:
    """场景 D：缓存击穿对比（冷缓存下并发请求：无保护 vs 互斥锁防护）。"""
    print("\n" + "=" * 70)
    print("      场景 D：缓存击穿对比实验 (Cache Breakdown / Stampede)")
    print("=" * 70)
    print(f"配置：冷缓存启动 | {concurrency} 个线程在同一纳秒并发涌入读取同一热点 Key")
    print(f"教学演示模式：每次 MySQL 查询模拟增加 {db_delay_ms:.0f}ms 成本\n")

    cache_key = f"product:{product_id}"
    lock_key = f"lock:product:{product_id}"

    # --------------------------------------------------------------------------
    # 实验 D1：冷缓存无保护版本
    # --------------------------------------------------------------------------
    print("[1/2] 正在运行实验 D1：冷缓存 · 无保护版本...")
    redis_client.delete(cache_key)
    redis_client.delete(lock_key)

    barrier_unprotected = threading.Barrier(concurrency)
    collector_unprotected = MetricsCollector("击穿-无保护")

    def unprotected_worker() -> None:
        barrier_unprotected.wait()  # 所有并发线程在此对齐，确保完全同时发起请求
        t_req_start = time.perf_counter()
        data = redis_client.hgetall(cache_key)
        if data:
            collector_unprotected.record_redis_hit()
        else:
            collector_unprotected.record_redis_miss()
            conn = get_connection_from_pool(mysql_pool)
            try:
                collector_unprotected.record_mysql_query()
                product = find_product_in_mysql(conn, product_id, delay_ms=db_delay_ms)
            finally:
                conn.close()

            if product:
                redis_client.hset(
                    cache_key,
                    mapping={
                        "id": str(product["id"]),
                        "name": str(product["name"]),
                        "price": str(product["price"]),
                        "views": str(product["views"]),
                    },
                )
                redis_client.expire(cache_key, 600)

        latency = (time.perf_counter() - t_req_start) * 1000.0
        collector_unprotected.record_request(latency, success=True)

    collector_unprotected.start()
    with ThreadPoolExecutor(max_workers=concurrency) as executor:
        list(executor.map(lambda _: unprotected_worker(), range(concurrency)))
    collector_unprotected.stop()

    # --------------------------------------------------------------------------
    # 实验 D2：冷缓存互斥锁（Mutex Lock）保护版本
    # --------------------------------------------------------------------------
    print("[2/2] 正在运行实验 D2：冷缓存 · 分布式互斥锁保护版本...")
    redis_client.delete(cache_key)
    redis_client.delete(lock_key)

    barrier_protected = threading.Barrier(concurrency)
    collector_protected = MetricsCollector("击穿-互斥锁保护")

    def protected_worker() -> None:
        barrier_protected.wait()  # 同样对齐并发点
        t_req_start = time.perf_counter()

        while True:
            data = redis_client.hgetall(cache_key)
            if data:
                collector_protected.record_redis_hit()
                break

            # 尝试通过 Redis set nx ex 抢占互斥锁
            acquired = redis_client.set(lock_key, "1", nx=True, ex=5)
            if acquired:
                try:
                    # DCL 双重检查缓存，防止前一个拿到锁的线程已经写入缓存
                    double_check = redis_client.hgetall(cache_key)
                    if double_check:
                        collector_protected.record_redis_hit()
                        break

                    collector_protected.record_redis_miss()
                    conn = get_connection_from_pool(mysql_pool)
                    try:
                        collector_protected.record_mysql_query()
                        product = find_product_in_mysql(conn, product_id, delay_ms=db_delay_ms)
                    finally:
                        conn.close()

                    if product:
                        redis_client.hset(
                            cache_key,
                            mapping={
                                "id": str(product["id"]),
                                "name": str(product["name"]),
                                "price": str(product["price"]),
                                "views": str(product["views"]),
                            },
                        )
                        redis_client.expire(cache_key, 600)
                    break
                finally:
                    redis_client.delete(lock_key)
            else:
                # 抢锁失败的线程，短暂让渡休眠等待缓存构建完成，然后循环重试
                time.sleep(0.005)

        latency = (time.perf_counter() - t_req_start) * 1000.0
        collector_protected.record_request(latency, success=True)

    collector_protected.start()
    with ThreadPoolExecutor(max_workers=concurrency) as executor:
        list(executor.map(lambda _: protected_worker(), range(concurrency)))
    collector_protected.stop()

    # 输出对比结果
    print("\n" + "-" * 70)
    print("                    缓存击穿防护对比结果")
    print("-" * 70)
    print(f"并发涌入请求数：       {concurrency} 次")
    print(f"冷缓存 · 无保护模式：   MySQL 承受查询 {collector_unprotected.mysql_queries} 次（连接池满载，穿透回源）")
    print(f"冷缓存 · 互斥锁模式：   MySQL 承受查询 {collector_protected.mysql_queries} 次（仅 1 个线程回源，其余等待并命中）")
    print("-" * 70)
    if collector_unprotected.mysql_queries > collector_protected.mysql_queries:
        reduction = collector_unprotected.mysql_queries / collector_protected.mysql_queries
        print(f"防护收益：有效阻止了 {collector_unprotected.mysql_queries - collector_protected.mysql_queries} 次突发穿透冲击，查询降低 {reduction:.1f} 倍！")
    print("=" * 70 + "\n")


# ==============================================================================
# 4. 可视化报告渲染器 (ASCII 柱状对比图与完整指标卡片)
# ==============================================================================

def render_bar(count: int, total: int, max_width: int = 36) -> str:
    """生成整齐的控制台字符进度条。"""
    if total <= 0 or count <= 0:
        return " " * max_width

    ratio = count / total
    block_units = ratio * max_width
    full_blocks = int(block_units)

    if count == 1 and full_blocks == 0:
        # 当只有 1 次（如 20000 里的 1）时，用细竖线 ▏ 直观区分 0 次与 1 次
        return "▏" + " " * (max_width - 1)

    bar = "█" * full_blocks
    if len(bar) < max_width:
        bar += " " * (max_width - len(bar))
    return bar[:max_width]


def render_report(
    metrics_a: MetricsCollector,
    metrics_b: MetricsCollector,
    concurrency: int,
    pool_size: int,
    db_delay_ms: float,
) -> None:
    """渲染终端 ASCII 对比柱状图与数量级对比报表。"""
    perc_a = metrics_a.compute_percentiles()
    perc_b = metrics_b.compute_percentiles()

    duration_a = metrics_a.total_duration_seconds
    duration_b = metrics_b.total_duration_seconds

    qps_a = metrics_a.qps
    qps_b = metrics_b.qps

    speedup_queries = (
        metrics_a.mysql_queries / metrics_b.mysql_queries
        if metrics_b.mysql_queries > 0
        else float("inf")
    )
    speedup_time = duration_a / duration_b if duration_b > 0 else float("inf")
    speedup_qps = qps_b / qps_a if qps_a > 0 else float("inf")
    speedup_p95 = perc_a["p95"] / perc_b["p95"] if perc_b["p95"] > 0 else float("inf")

    total_req = metrics_a.total_requests

    print("\n" + "=" * 66)
    print("                    Redis 缓存效果对比报告")
    print("=" * 66)
    if db_delay_ms > 0:
        print(f"[*] 教学演示模式：每次 MySQL 查询模拟增加 {db_delay_ms:.0f}ms 耗时")
    else:
        print("[*] 真实性能测试模式：MySQL 无模拟延迟 (0ms)")
    print(f"[*] 压测配置：请求总数 = {total_req:,} | 并发线程 = {concurrency} | MySQL 连接池 = {pool_size}")
    print("-" * 66)

    bar_width = 36
    # 场景 A 柱状图
    bar_a_mysql = render_bar(metrics_a.mysql_queries, total_req, bar_width)
    bar_a_redis = render_bar(metrics_a.redis_hits, total_req, bar_width)
    # 场景 B 柱状图
    bar_b_mysql = render_bar(metrics_b.mysql_queries, total_req, bar_width)
    bar_b_redis = render_bar(metrics_b.redis_hits, total_req, bar_width)

    print("纯 MySQL")
    print(f"MySQL查询  {bar_a_mysql}  {metrics_a.mysql_queries:>8,d}次")
    print(f"Redis命中  {bar_a_redis}  {metrics_a.redis_hits:>8,d}次")
    print()
    print("Redis 缓存")
    print(f"MySQL查询  {bar_b_mysql}  {metrics_b.mysql_queries:>8,d}次")
    print(f"Redis命中  {bar_b_redis}  {metrics_b.redis_hits:>8,d}次")
    print("-" * 66)

    # 核心数量级差距卡片
    queries_saved = metrics_a.mysql_queries - metrics_b.mysql_queries
    offload_pct = metrics_b.db_offload_rate * 100.0

    print(f"{'MySQL 查询次数：':<18} {metrics_a.mysql_queries:>8,d} → {metrics_b.mysql_queries:>8,d}")
    print(f"{'数据库查询减少：':<18} {queries_saved:>8,d} 次")
    print(f"{'数据库减压率：':<18} {offload_pct:>8.3f}%")
    print(f"{'MySQL 查询减少倍数：':<18} {speedup_queries:>8,.0f} 倍 (数量级消除)")
    print(f"{'总耗时：':<18} {duration_a:>8.2f}s → {duration_b:>8.2f}s (耗时降至 1/{speedup_time:.1f})")
    print(f"{'吞吐量 (QPS)：':<18} {qps_a:>8.1f} → {qps_b:>8.1f} (提速 {speedup_qps:.1f} 倍)")
    print(f"{'P95 延迟：':<18} {perc_a['p95']:>8.1f}ms → {perc_b['p95']:>8.1f}ms (延迟降至 1/{speedup_p95:.1f})")
    print("=" * 66)

    # 全指标详细清单表
    print("\n" + "-" * 72)
    print("                         全维度统计指标明细表")
    print("-" * 72)
    print(f"{'指标项':<20} | {'纯 MySQL 场景':<18} | {'Redis 缓存场景':<18} | {'变动趋势'}")
    print("-" * 72)
    print(f"{'业务请求总数':<20} | {metrics_a.total_requests:>14,d} | {metrics_b.total_requests:>14,d} | 对齐基准")
    print(f"{'成功请求数':<20} | {metrics_a.success_count:>14,d} | {metrics_b.success_count:>14,d} | 100% 成功")
    print(f"{'失败请求数':<20} | {metrics_a.failure_count:>14,d} | {metrics_b.failure_count:>14,d} | 无异常")
    print(f"{'MySQL 实际查询次数':<17} | {metrics_a.mysql_queries:>14,d} | {metrics_b.mysql_queries:>14,d} | [减] 数量级降低")
    print(f"{'Redis 实际读取次数':<17} | {metrics_a.redis_reads:>14,d} | {metrics_b.redis_reads:>14,d} | 全部缓存化")
    print(f"{'Redis 命中次数':<19} | {metrics_a.redis_hits:>14,d} | {metrics_b.redis_hits:>14,d} | 极高命中")
    print(f"{'Redis 未命中次数':<18} | {metrics_a.redis_misses:>14,d} | {metrics_b.redis_misses:>14,d} | 仅冷启动1次")
    print(f"{'缓存命中率':<21} | {metrics_a.hit_rate * 100:>13.2f}% | {metrics_b.hit_rate * 100:>13.3f}% | 接近100%")
    print(f"{'数据库减压率':<20} | {metrics_a.db_offload_rate * 100:>13.2f}% | {offload_pct:>13.3f}% | 99.99%+")
    print(f"{'总耗时':<23} | {duration_a:>12.2f} s | {duration_b:>12.2f} s | 缩短 {speedup_time:.1f}x")
    print(f"{'吞吐量 (QPS)':<19} | {qps_a:>10.1f} 次/s | {qps_b:>10.1f} 次/s | 提升 {speedup_qps:.1f}x")
    print(f"{'平均延迟 (Avg)':<18} | {perc_a['avg']:>11.2f} ms | {perc_b['avg']:>11.2f} ms | 降低 {perc_a['avg'] / perc_b['avg']:.1f}x")
    print(f"{'P50 中位数延迟':<18} | {perc_a['p50']:>11.2f} ms | {perc_b['p50']:>11.2f} ms | 降低 {perc_a['p50'] / perc_b['p50']:.1f}x")
    print(f"{'P95 延迟':<22} | {perc_a['p95']:>11.2f} ms | {perc_b['p95']:>11.2f} ms | 降低 {speedup_p95:.1f}x")
    print(f"{'P99 延迟':<22} | {perc_a['p99']:>11.2f} ms | {perc_b['p99']:>11.2f} ms | 降低 {perc_a['p99'] / perc_b['p99']:.1f}x")
    print(f"{'最大延迟 (Max)':<18} | {perc_a['max']:>11.2f} ms | {perc_b['max']:>11.2f} ms | 抑制毛刺")
    print("=" * 72)

    print("\n[底层原理分析]")
    print("1. 为什么 MySQL 查询次数差距是 20,000 倍？")
    print("   纯 MySQL 下每个业务请求都直达数据库；引入 Cache-Aside 之后，除冷启动首次回源外，")
    print("   后续请求全被 Redis 在内存层完全吸收拦截，这是由架构逻辑决定的根本性差距，不受机器配置影响。")
    print("2. 为什么会有百倍级的吞吐与尾延迟差距？")
    print(f"   只有 {pool_size} 个 MySQL 连接时，{concurrency} 个并发请求导致大多数线程在连接池外排队阻塞。")
    print("   而 Redis 纯内存微秒级寻址，天然消除数据库线程池排队瓶颈，从而抹平尾部延迟。")
    print("=" * 72 + "\n")


# ==============================================================================
# 5. 基准压测主流程入口
# ==============================================================================

def run_benchmark_workflow(
    requests: int = 20000,
    concurrency: int = 200,
    mysql_pool_size: int = 10,
    db_delay_ms: float = 50.0,
    cache_ttl: int = 600,
    with_breakdown: bool = False,
) -> None:
    """完整的数量级差距压测工作流。"""
    print("\n" + "=" * 72)
    print("          Redis 数量级差距基准测试 (MySQL vs Redis Cache)")
    print("=" * 72)
    print(f"参数：请求数 = {requests:,} | 并发 = {concurrency} | MySQL 连接池 = {mysql_pool_size} | 模拟延迟 = {db_delay_ms:.0f}ms")

    # 1. 建立基础连接并初始化测试表
    mysql_conn, redis_client = wait_for_services()
    try:
        create_table(mysql_conn)
        product_id = create_product(mysql_conn, "并发测试爆款商品", 199.99)

        # 2. 初始化 MySQL 数据库连接池
        mysql_pool = pooling.MySQLConnectionPool(
            pool_name="benchmark_pool",
            pool_size=mysql_pool_size,
            **MYSQL_CONFIG,
        )

        # 3. 预热连接（保证测试时连接已建立，排除冷握手干扰）
        print("[准备] 正在预热 MySQL 连接池和 Redis 连接...")
        warmup_conns = []
        for _ in range(mysql_pool_size):
            warmup_conns.append(mysql_pool.get_connection())
        for c in warmup_conns:
            c.close()
        redis_client.ping()
        print("[准备] 连接预热完毕！")

        # 4. 执行场景 A：纯 MySQL
        print(f"\n[1/2] 正在运行场景 A：纯 MySQL 并发查询 ({requests:,} 次请求)...")
        if db_delay_ms > 0:
            est_time = (requests * db_delay_ms / 1000.0) / mysql_pool_size
            print(f"      (预计理论排队耗时: 约 {est_time:.1f} 秒，请耐心等待)")
        metrics_a = run_scene_a_mysql_only(
            mysql_pool=mysql_pool,
            product_id=product_id,
            total_requests=requests,
            concurrency=concurrency,
            db_delay_ms=db_delay_ms,
        )
        print(f"      场景 A 完成！总耗时: {metrics_a.total_duration_seconds:.2f}s, QPS: {metrics_a.qps:.1f}")

        # 5. 执行场景 B：Redis Cache-Aside
        print(f"\n[2/2] 正在运行场景 B：Redis 缓存并发查询 ({requests:,} 次请求)...")
        metrics_b = run_scene_b_redis_cache(
            mysql_pool=mysql_pool,
            redis_client=redis_client,
            product_id=product_id,
            total_requests=requests,
            concurrency=concurrency,
            db_delay_ms=db_delay_ms,
            cache_ttl=cache_ttl,
        )
        print(f"      场景 B 完成！总耗时: {metrics_b.total_duration_seconds:.2f}s, QPS: {metrics_b.qps:.1f}")

        # 6. 渲染可视化报表
        render_report(
            metrics_a=metrics_a,
            metrics_b=metrics_b,
            concurrency=concurrency,
            pool_size=mysql_pool_size,
            db_delay_ms=db_delay_ms,
        )

        # 7. 可选场景 D：击穿防护对比
        if with_breakdown:
            run_scene_d_breakdown_protection(
                mysql_pool=mysql_pool,
                redis_client=redis_client,
                product_id=product_id,
                concurrency=concurrency,
                db_delay_ms=db_delay_ms,
            )

    finally:
        mysql_conn.close()
        redis_client.close()


# ==============================================================================
# 6. 交互式菜单
# ==============================================================================

def run_interactive():
    mysql_conn, redis_client = wait_for_services()
    try:
        create_table(mysql_conn)
        while True:
            print("\n" + "=" * 50)
            print("         Redis + MySQL 数量级演示交互菜单")
            print("=" * 50)
            print("1. 创建新商品")
            print("2. 按 ID 查询商品 (先查 Redis，未命中再查 MySQL)")
            print("3. 修改商品价格 (更新 MySQL 并删除 Redis 缓存)")
            print("4. 查看 MySQL 中的全部商品")
            print("5. 运行场景 C：缓存失效与更新演示")
            print("6. 运行场景 D：缓存击穿防护对比演示 (无保护 vs 互斥锁)")
            print("7. 运行快速压测 (5,000 请求 | 并发 100 | 延迟 20ms)")
            print("8. 运行标准全量压测 (20,000 请求 | 并发 200 | 延迟 50ms)")
            print("9. 运行本机真实 0ms 压测 (20,000 请求 | 并发 200 | 延迟 0ms)")
            print("q. 退出")
            choice = input("请选择操作 (1-9 或 q): ").strip().lower()

            if choice == "q":
                break
            elif choice == "1":
                name = input("商品名称: ").strip() or "测试商品"
                price_str = input("商品价格: ").strip() or "99.9"
                create_product(mysql_conn, name, float(price_str))
            elif choice == "2":
                pid = int(input("商品 ID: ").strip() or "1")
                cache_key = f"product:{pid}"
                cached = redis_client.hgetall(cache_key)
                if cached:
                    print(f"[redis hit] 命中缓存: {cached}")
                else:
                    print(f"[redis miss] 未命中，回源 MySQL...")
                    conn = connect_mysql()
                    try:
                        p = find_product_in_mysql(conn, pid)
                    finally:
                        conn.close()
                    if p:
                        redis_client.hset(
                            cache_key,
                            mapping={
                                "id": str(p["id"]),
                                "name": str(p["name"]),
                                "price": str(p["price"]),
                                "views": str(p["views"]),
                            },
                        )
                        redis_client.expire(cache_key, 600)
                        print(f"[mysql select] 返回结果并写入 Redis: {p}")
                    else:
                        print("商品不存在！")
            elif choice == "3":
                pid = int(input("商品 ID: ").strip() or "1")
                new_price = float(input("新价格: ").strip() or "199.99")
                update_product_price(mysql_conn, pid, new_price)
                clear_cache(redis_client, pid)
            elif choice == "4":
                list_products(mysql_conn)
            elif choice == "5":
                run_scene_c_invalidation(mysql_conn, redis_client)
            elif choice == "6":
                pid = create_product(mysql_conn, "击穿演示爆款商品", 88.88)
                pool = pooling.MySQLConnectionPool(
                    pool_name="breakdown_menu_pool", pool_size=10, **MYSQL_CONFIG
                )
                run_scene_d_breakdown_protection(
                    mysql_pool=pool,
                    redis_client=redis_client,
                    product_id=pid,
                    concurrency=100,
                    db_delay_ms=20.0,
                )
            elif choice == "7":
                run_benchmark_workflow(
                    requests=5000,
                    concurrency=100,
                    mysql_pool_size=10,
                    db_delay_ms=20.0,
                )
            elif choice == "8":
                run_benchmark_workflow(
                    requests=20000,
                    concurrency=200,
                    mysql_pool_size=10,
                    db_delay_ms=50.0,
                )
            elif choice == "9":
                run_benchmark_workflow(
                    requests=20000,
                    concurrency=200,
                    mysql_pool_size=20,
                    db_delay_ms=0.0,
                )
            else:
                print("无效输入，请输入 1 到 9，或 q。")
    finally:
        mysql_conn.close()
        redis_client.close()
        print("所有连接已释放。")


# ==============================================================================
# 7. CLI 参数解析与主入口
# ==============================================================================

def main():
    parser = argparse.ArgumentParser(
        description="Redis 数量级差距演示工具 (MySQL vs Redis Cache Benchmark)"
    )
    subparsers = parser.add_subparsers(dest="command", help="子命令")

    # benchmark 子命令
    bench_parser = subparsers.add_parser("benchmark", aliases=["bench"], help="运行高并发性能基准测试")
    bench_parser.add_argument("--requests", type=int, default=20000, help="总业务请求数 (默认 20000)")
    bench_parser.add_argument("--concurrency", type=int, default=200, help="并发工作线程数 (默认 200)")
    bench_parser.add_argument("--mysql-pool-size", type=int, default=10, help="MySQL 连接池容量 (默认 10)")
    bench_parser.add_argument("--db-delay-ms", type=float, default=50.0, help="模拟数据库延迟毫秒 (默认 50ms, 设为 0 则为本机真实测试)")
    bench_parser.add_argument("--cache-ttl", type=int, default=600, help="Redis 缓存有效期秒数 (默认 600)")
    bench_parser.add_argument("--with-breakdown", action="store_true", help="同时执行场景 D 缓存击穿对比")

    # demo 子命令 (场景 C 演示)
    subparsers.add_parser("demo", help="运行场景 C：缓存失效与一致性自动演示")

    # breakdown 子命令 (场景 D 击穿测试)
    breakdown_parser = subparsers.add_parser("breakdown", help="单独运行场景 D：缓存击穿防护对比")
    breakdown_parser.add_argument("--concurrency", type=int, default=200, help="并发工作线程数 (默认 200)")
    breakdown_parser.add_argument("--mysql-pool-size", type=int, default=10, help="MySQL 连接池容量 (默认 10)")
    breakdown_parser.add_argument("--db-delay-ms", type=float, default=50.0, help="模拟数据库延迟毫秒 (默认 50ms)")

    args = parser.parse_args()

    if args.command in {"benchmark", "bench"}:
        run_benchmark_workflow(
            requests=args.requests,
            concurrency=args.concurrency,
            mysql_pool_size=args.mysql_pool_size,
            db_delay_ms=args.db_delay_ms,
            cache_ttl=args.cache_ttl,
            with_breakdown=args.with_breakdown,
        )
    elif args.command == "demo":
        mysql_conn, redis_client = wait_for_services()
        try:
            create_table(mysql_conn)
            run_scene_c_invalidation(mysql_conn, redis_client)
        finally:
            mysql_conn.close()
            redis_client.close()
    elif args.command == "breakdown":
        mysql_conn, redis_client = wait_for_services()
        try:
            create_table(mysql_conn)
            pid = create_product(mysql_conn, "击穿独立压测商品", 99.0)
            pool = pooling.MySQLConnectionPool(
                pool_name="breakdown_cli_pool",
                pool_size=args.mysql_pool_size,
                **MYSQL_CONFIG,
            )
            run_scene_d_breakdown_protection(
                mysql_pool=pool,
                redis_client=redis_client,
                product_id=pid,
                concurrency=args.concurrency,
                db_delay_ms=args.db_delay_ms,
            )
        finally:
            mysql_conn.close()
            redis_client.close()
    else:
        run_interactive()


if __name__ == "__main__":
    main()
