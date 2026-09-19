import redis

# 1. 连接本地 Redis 服务
r = redis.Redis(host='127.0.0.1', port=6380, decode_responses=True)

def record_view(path: str) -> int:
    """模拟访问页面并递增计数"""
    key = f"page:view:{path}"
    # INCR 是原子自增操作：如果 key 不存在，Redis 会先初始化为 0 再自增为 1
    views = r.incr(key)  # 👈 核心机制：执行 INCR 原语，原子性自增计数器
    return views

def get_views(path: str) -> int:
    """获取当前页面累计访问量"""
    key = f"page:view:{path}"
    val = r.get(key)
    return int(val) if val else 0

if __name__ == "__main__":
    path = "/home"

    # 清理旧测试数据，保证每次演示结果直观一致
    r.delete(f"page:view:{path}")

    # 模拟用户连续访问 3 次
    print(f"[访问] 用户 A 访问 {path} -> 实时浏览量: {record_view(path)}")
    print(f"[访问] 用户 B 访问 {path} -> 实时浏览量: {record_view(path)}")
    print(f"[访问] 用户 C 访问 {path} -> 实时浏览量: {record_view(path)}")

    # 查询最终计数
    print(f"[查询] {path} 最终累计访问次数: {get_views(path)}")
