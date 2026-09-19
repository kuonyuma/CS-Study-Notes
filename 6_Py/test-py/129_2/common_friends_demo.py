import redis

# 1. 连接本地 Redis 服务（端口 6380）
r = redis.Redis(host='127.0.0.1', port=6380, decode_responses=True)

def add_friend(user: str, *friends: str) -> None:
    """给用户添加好友"""
    key = f"friend:{user}"
    # SADD：向集合批量添加成员，Set 自动去重（重复添加同一好友无效）
    r.sadd(key, *friends)  # 👈 核心机制：SADD 写入集合，天然去重
    print(f"[添加] {user} 的好友: {sorted(r.smembers(key))}")

def common_friends(user_a: str, user_b: str) -> set:
    """求两个用户的共同好友"""
    key_a, key_b = f"friend:{user_a}", f"friend:{user_b}"
    # SINTER：求多个集合的交集，即双方都认识的人
    result = r.sinter(key_a, key_b)  # 👈 核心机制：SINTER 交集 = 共同好友
    return set(result)

def maybe_know_friends(user_a: str, user_b: str) -> set:
    """求 B 可能认识的人（B 好友中 A 不认识的人）"""
    key_a, key_b = f"friend:{user_a}", f"friend:{user_b}"
    # SDIFF：求差集，B 有而 A 没有的好友，可用于好友推荐
    result = r.sdiff(key_b, key_a)  # 👈 核心机制：SDIFF 差集 = 可能认识的人
    return set(result)

def all_friends(user_a: str, user_b: str) -> set:
    """求双方好友的总和（去重）"""
    key_a, key_b = f"friend:{user_a}", f"friend:{user_b}"
    # SUNION：求并集，Set 会自动去掉两人共同认识的那部分，不重复
    result = r.sunion(key_a, key_b)  # 👈 核心机制：SUNION 并集 = 全部好友去重
    return set(result)

def is_friend(user: str, target: str) -> bool:
    """判断两人是否为好友"""
    key = f"friend:{user}"
    # SISMEMBER：O(1) 判断成员是否在集合中，比列表遍历快得多
    return bool(r.sismember(key, target))  # 👈 核心机制：SISMEMBER O(1) 判断

if __name__ == "__main__":
    user_a, user_b = "alice", "bob"

    # 清理旧测试数据，保证每次演示结果直观一致
    r.delete(f"friend:{user_a}", f"friend:{user_b}")

    # --- 准备数据：两人各自的好友列表 ---
    add_friend(user_a, "tom", "jerry", "lily", "jack")
    add_friend(user_b, "jerry", "lily", "lucy", "nancy")

    # --- 场景 1：共同好友（交集）---
    common = common_friends(user_a, user_b)
    print(f"[共同好友] {user_a} 和 {user_b} 都认识: {sorted(common)}，共 {len(common)} 人")

    # --- 场景 2：可能认识的人（差集，用于好友推荐）---
    maybe = maybe_know_friends(user_a, user_b)
    print(f"[好友推荐] {user_b} 可能认识（{user_a} 不认识）: {sorted(maybe)}")

    # --- 场景 3：双方全部好友（并集，自动去重）---
    everyone = all_friends(user_a, user_b)
    print(f"[全部好友] 两人好友合并去重后共 {len(everyone)} 人: {sorted(everyone)}")

    # --- 场景 4：判断好友关系 ---
    print(f"[判断] {user_a} 是否认识 tom: {is_friend(user_a, 'tom')}")
    print(f"[判断] {user_a} 是否认识 lucy: {is_friend(user_a, 'lucy')}")
