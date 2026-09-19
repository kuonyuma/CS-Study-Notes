import sys
import time
import threading
import redis

# 确保 Windows 终端中文输出不乱码
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")

# 连接本地 Redis 服务（端口 6380）
r = redis.Redis(host="127.0.0.1", port=6380, decode_responses=True)
CHANNEL_NAME = "channel:news"


def subscriber(name: str):
    """订阅者：监听频道并接收广播消息"""
    pubsub = r.pubsub()
    pubsub.subscribe(CHANNEL_NAME)  # 👈 核心机制：SUBSCRIBE 订阅指定频道

    for item in pubsub.listen():  # 👈 核心机制：阻塞监听频道消息
        # 过滤订阅成功的系统确认消息，只处理真实业务消息
        if item["type"] == "message":
            msg = item["data"]
            if msg == "QUIT":
                print(f"  [{name}] 收到退出信号，停止监听。")
                break
            print(f"  [{name}] 收到广播消息: {msg}")

    pubsub.unsubscribe(CHANNEL_NAME)
    pubsub.close()


def publisher():
    """发布者：向指定频道发送广播消息"""
    time.sleep(0.2)  # 等待订阅者就绪（Pub/Sub 不存历史消息，需先订阅再发布）

    messages = ["第一条新闻：系统升级通知", "第二条新闻：活动倒计时开始", "QUIT"]
    for msg in messages:
        # 👈 核心机制：PUBLISH 发布消息到频道，返回值是当前收到该消息的订阅者数量
        receiver_count = r.publish(CHANNEL_NAME, msg)
        if msg != "QUIT":
            print(f"[发布者] 发送消息: '{msg}' (当前在线订阅数: {receiver_count})")
        time.sleep(0.3)


if __name__ == "__main__":
    print("=== 开始运行 Redis Pub/Sub 广播模型 (1 发布者 vs 2 订阅者) ===")

    # 1. 启动 2 个订阅者线程（模拟两个不同客户端/微服务）
    sub1 = threading.Thread(target=subscriber, args=("订阅者-A",), name="Sub-A")
    sub2 = threading.Thread(target=subscriber, args=("订阅者-B",), name="Sub-B")
    sub1.start()
    sub2.start()

    # 2. 启动 1 个发布者线程
    pub = threading.Thread(target=publisher, name="Publisher")
    pub.start()

    # 3. 等待所有线程执行完毕
    pub.join()
    sub1.join()
    sub2.join()

    print("=== Pub/Sub 演示结束 ===")
