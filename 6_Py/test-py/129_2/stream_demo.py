import sys
import redis

# 确保 Windows 终端中文输出不乱码
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")

# 连接本地 Redis 服务（端口 6380）
r = redis.Redis(host="127.0.0.1", port=6380, decode_responses=True)
STREAM_KEY = "stream:orders"
GROUP_NAME = "order_group"


def run_stream_demo():
    # 0. 环境清理
    r.delete(STREAM_KEY)

    print("=== 1. 生产者追加消息 (XADD) ===")
    orders = [
        {"order_id": "1001", "item": "键盘", "price": "299"},
        {"order_id": "1002", "item": "鼠标", "price": "99"},
        {"order_id": "1003", "item": "显示器", "price": "1299"},
    ]

    for order in orders:
        # 👈 核心机制：XADD 追加消息，'*' 表示由 Redis 自动生成全局唯一递增 ID (时间戳-序列号)
        msg_id = r.xadd(STREAM_KEY, order, id="*")
        print(f"[生产者] 写入成功 -> 消息ID: {msg_id} | 内容: {order['item']}(¥{order['price']})")

    print(f"\n=== 2. 创建消费组 (XGROUP CREATE) ===")
    # 👈 核心机制：创建消费组，id='0' 代表从 Stream 起始位置开始监听
    r.xgroup_create(STREAM_KEY, GROUP_NAME, id="0")
    print(f"消费组 '{GROUP_NAME}' 创建完成，起点设为 0。")

    print(f"\n=== 3. 消费组协同分发 (XREADGROUP + XACK) ===")
    # 模拟组内两个竞争消费者：Worker-A 和 Worker-B
    # 👈 核心机制：id='>' 表示读取 Stream 中尚未分配给该组其他消费者的“全新消息”
    # Worker-A 先读取 2 条
    res_a = r.xreadgroup(GROUP_NAME, "worker-A", {STREAM_KEY: ">"}, count=2)
    if res_a:
        for msg_id, data in res_a[0][1]:
            print(f"  [worker-A] 领取并处理消息 {msg_id}: {data['item']}")
            # 👈 核心机制：XACK 确认处理完毕，消息从待确认列表 (PEL) 移出
            r.xack(STREAM_KEY, GROUP_NAME, msg_id)

    # Worker-B 再读取 2 条（前 2 条已被 A 认领，B 自动分发到剩余的第 3 条，实现负载均衡）
    res_b = r.xreadgroup(GROUP_NAME, "worker-B", {STREAM_KEY: ">"}, count=2)
    if res_b:
        for msg_id, data in res_b[0][1]:
            print(f"  [worker-B] 领取并处理消息 {msg_id}: {data['item']}")
            r.xack(STREAM_KEY, GROUP_NAME, msg_id)

    print(f"\n=== 4. 验证消费状态 (XPENDING) ===")
    # 👈 核心机制：XPENDING 检查是否有处理中/未确认的消息
    pending_info = r.xpending(STREAM_KEY, GROUP_NAME)
    print(f"当前组内待确认(Pending)消息数: {pending_info['pending']} (0 表示全部成功 ACK)")

    # 清理演示数据
    r.delete(STREAM_KEY)


if __name__ == "__main__":
    run_stream_demo()
