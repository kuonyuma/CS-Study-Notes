import sys
import time
import redis

# 确保 Windows 终端中文输出不乱码
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")

r = redis.Redis(host="127.0.0.1", port=6380, decode_responses=True)

def demo_basic_usage():
    print("=== 1. Pipeline 基础读写与批量获取结果 ===")
    
    # 1. 创建 pipeline 对象（缓冲命令在客户端内存，暂不发送）
    pipe = r.pipeline(transaction=False)  # 👈 核心机制：创建管道，transaction=False 纯网络打包

    # 2. 连续写入多个不同类型的 Redis 命令（命令暂存在客户端缓冲区）
    pipe.set("player:101:name", "Alice")       # 👈 暂未发送网络请求
    pipe.set("player:101:level", 10)          # 👈 暂未发送网络请求
    pipe.incrby("player:101:level", 5)        # 👈 暂未发送网络请求
    pipe.get("player:101:name")               # 👈 暂未发送网络请求
    pipe.get("player:101:level")              # 👈 暂未发送网络请求

    # 3. 一次性打包发送到服务端并执行
    results = pipe.execute()  # 👈 核心机制：1 次网络往返（RTT），一次性取回全部命令的执行结果
    
    print(f"Pipeline 批量执行返回值列表: {results}")
    print(f"按顺序解析 -> 玩家名字: {results[3]}, 最新等级: {results[4]}")


def demo_performance_comparison():
    print("\n=== 2. 性能对比：逐条发送 vs Pipeline 打包 (1000 次操作) ===")
    total_ops = 1000

    # 方式 A：普通循环逐条发送（1000 次网络请求，1000 次 RTT）
    start = time.perf_counter()
    for i in range(total_ops):
        r.set(f"bench:{i}", i)  # 👈 每次都需要等网络发送与响应
    time_normal = (time.perf_counter() - start) * 1000

    # 方式 B：Pipeline 打包发送（1000 条命令打包为 1 次网络请求，1 次 RTT）
    start = time.perf_counter()
    pipe = r.pipeline(transaction=False)
    for i in range(total_ops):
        pipe.set(f"bench:{i}", i)  # 👈 仅在客户端追加缓冲
    pipe.execute()                 # 👈 一次性发往服务端并取回结果
    time_pipe = (time.perf_counter() - start) * 1000

    print(f"逐条执行耗时 : {time_normal:.2f} ms (经历 {total_ops} 次网络往返)")
    print(f"Pipeline 耗时: {time_pipe:.2f} ms (仅 1 次网络往返)")
    print(f"🚀 性能提速   : 提升约 {time_normal / time_pipe:.1f} 倍！")

    # 清理基准测试数据
    keys = [f"bench:{i}" for i in range(total_ops)]
    r.delete(*keys)
    r.delete("player:101:name", "player:101:level")


if __name__ == "__main__":
    demo_basic_usage()
    demo_performance_comparison()
