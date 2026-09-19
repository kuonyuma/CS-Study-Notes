import time
import threading
import redis

# 1. 连接本地 Redis 服务（端口 6380）
r = redis.Redis(host="127.0.0.1", port=6380, decode_responses=True)
QUEUE_KEY = "queue:tasks"


def producer():
    """生产者：向队列尾部推入任务"""
    for i in range(1, 7):
        task = f"任务-{i}"
        r.rpush(QUEUE_KEY, task)  # 👈 核心机制：RPUSH 进队尾，构建 FIFO 队列
        print(f"[生产者] 生产了: {task}")
        time.sleep(0.2)  # 模拟生产间隔


def consumer(consumer_id: int):
    """消费者：从队列头部阻塞弹出任务处理"""
    while True:
        # timeout=2 表示若队列为空，最多阻塞挂起 2 秒；若超时仍无数据返回 None
        result = r.blpop(QUEUE_KEY, timeout=2)  # 👈 核心机制：BLPOP 阻塞式队头出队，原子消费，无多消费者并发竞争冲突
        if result is None:
            print(f"  [消费者-{consumer_id}] 等待超时，退出。")
            break

        _, task = result  # result 返回元组 (key, value)
        print(f"  [消费者-{consumer_id}] 成功消费: {task}")
        time.sleep(0.3)  # 模拟业务耗时


if __name__ == "__main__":
    # 清理旧数据，确保运行环境独立
    r.delete(QUEUE_KEY)

    print("=== 开始运行 Redis List 生产消费模型 (1 生产者 vs 3 消费者) ===")

    # 创建并启动 3 个消费者线程
    consumers = [
        threading.Thread(target=consumer, args=(i,), name=f"Consumer-{i}")
        for i in range(1, 4)
    ]
    for c in consumers:
        c.start()

    # 创建并启动 1 个生产者线程
    p = threading.Thread(target=producer, name="Producer")
    p.start()

    # 等待生产者与消费者线程全部完成
    p.join()
    for c in consumers:
        c.join()

    print("=== 所有生产与消费任务结束 ===")
