import asyncio
import sys
from contextlib import asynccontextmanager
from fastapi import FastAPI, Request
import httpx
import redis.asyncio as aioredis

# 保证 Windows 终端正常输出 UTF-8 字符
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")


# ============================================================
# 1. 核心机制：定义 lifespan 上下文管理器
# ============================================================
@asynccontextmanager
async def lifespan(app: FastAPI):
    # -------- 【启动阶段】FastAPI 应用启动时执行 --------
    print("\n🚀 [Lifespan 启动] 正在连接 Redis (端口 6380)...")
    # 👈 创建全局共享连接池（注意：Windows 本地连接优先用 127.0.0.1）
    redis_client = aioredis.Redis(
        host="127.0.0.1",
        port=6380,
        decode_responses=True,
    )

    # 验证连通性
    await redis_client.ping()
    print("✅ [Lifespan 启动] Redis 连接成功！挂载到 app.state.redis")

    # 👈 存入 app.state，供后续所有请求共享
    app.state.redis = redis_client

    # 👈 yield 是分水岭：yield 之前是【启动】，yield 之后是【关闭】
    # 程序运行期间会停在此处，持续对外提供 HTTP 服务
    yield

    # -------- 【关闭阶段】FastAPI 应用退出时执行 --------
    print("\n🛑 [Lifespan 关闭] 应用正在退出，准备安全关闭 Redis 连接...")
    await redis_client.aclose()
    print("👋 [Lifespan 关闭] Redis 连接已安全释放！\n")


# 👈 将 lifespan 挂载进 FastAPI 应用
app = FastAPI(lifespan=lifespan)


# ============================================================
# 2. 业务接口：共享复用同一个 Redis 连接
# ============================================================
@app.get("/hits")
async def count_hits(request: Request):
    # 👈 直接从 request.app.state 取出启动阶段创建好的 redis_client
    # 不需要每次发请求都重新建立 TCP 连接！
    redis_client: aioredis.Redis = request.app.state.redis

    # 自增访问计数器
    total_hits = await redis_client.incr("page_hits")
    return {"message": "访问成功", "total_hits": total_hits}


# ============================================================
# 3. 极简实战验证：模拟完整的启动与关闭生命周期
# ============================================================
async def main():
    print("=== 1. 准备启动 FastAPI 应用 ===")

    # 手动进入 lifespan 上下文，真实模拟整个服务器从启动到关闭的过程
    async with app.router.lifespan_context(app):
        print("\n=== 2. 服务器运行中，开始接收客户端 HTTP 请求 ===")
        transport = httpx.ASGITransport(app=app)
        async with httpx.AsyncClient(
            transport=transport, base_url="http://testserver"
        ) as client:
            print("👉 发送第 1 次请求：")
            res1 = await client.get("/hits")
            print("   响应:", res1.json())

            print("👉 发送第 2 次请求：")
            res2 = await client.get("/hits")
            print("   响应:", res2.json())

        print("\n=== 3. 收到关闭信号，准备退出应用 ===")

    print("=== 4. 应用生命周期完全结束 ===")


if __name__ == "__main__":
    asyncio.run(main())
