# 图书管理学习 Demo

Python 3.11+，FastAPI + SQLAlchemy 2.x（异步）+ MySQL + Redis，无业务前端。
目标：通过图书增删查改，观察**应用生命周期**和**请求会话生命周期**。

## 运行（PowerShell）

先启动 Docker Desktop，并使用 Linux containers。当前目录执行：

```powershell
docker compose up -d --wait
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.txt
.\.venv\Scripts\python.exe -X utf8 -m uvicorn main:app --reload
```

打开 http://127.0.0.1:8000/docs 可交互调用接口。
MySQL 使用本机 3307，Redis 使用 6380，降低与常见默认端口冲突的概率。
Compose 中的账号密码只用于本地演示，端口仅绑定本机。

可选环境变量（在启动 uvicorn 前设置）：

```powershell
$env:SQL_ECHO = '1' # 输出 SQL，便于观察 SELECT / COMMIT / ROLLBACK
$env:DATABASE_URL = 'mysql+aiomysql://demo:demo_pass@127.0.0.1:3307/library?charset=utf8mb4'
$env:REDIS_URL = 'redis://127.0.0.1:6380/0'
```

## 调用示例

另开一个 PowerShell：

```powershell
$base = 'http://127.0.0.1:8000'
$body = @{ title = 'Python 入门'; author = '示例作者' } | ConvertTo-Json
$book = Invoke-RestMethod "$base/books" -Method Post -ContentType 'application/json; charset=utf-8' -Body ([System.Text.Encoding]::UTF8.GetBytes($body))
$id = $book.id
Invoke-RestMethod "$base/books"

# 第一次 MISS，从 MySQL 读取并写入 Redis，60 秒后过期。
(Invoke-WebRequest "$base/books/$id").Headers['X-Cache']
# 第二次 HIT，直接从 Redis 读取。
(Invoke-WebRequest "$base/books/$id").Headers['X-Cache']

$body = @{ title = 'Python 进阶'; author = '示例作者' } | ConvertTo-Json
Invoke-RestMethod "$base/books/$id" -Method Put -ContentType 'application/json; charset=utf-8' -Body ([System.Text.Encoding]::UTF8.GetBytes($body))
# 修改成功后清理缓存，下次详情查询重新 MISS。
(Invoke-WebRequest "$base/books/$id").Headers['X-Cache']
Invoke-RestMethod "$base/books/$id" -Method Delete
```

`PUT` 要求完整传入书名和作者。空字符串返回 422，操作不存在的图书返回 404，删除成功返回 204（无响应体）。

## 阅读 main.py：两种 yield

建议依次阅读 `lifespan` → `get_session` → CRUD 接口。

### 1. 应用级 lifespan

`@asynccontextmanager` 把异步生成器转换为异步上下文管理器。
FastAPI 进入该上下文，执行 `yield` 之前的初始化代码，然后开始接收请求；关闭时退出上下文，执行 `finally`。

```text
启动进程
  → 创建 Engine（持有 MySQL 连接池）和 Redis 客户端
  → 连接 MySQL、建表，PING Redis
  → 把会话工厂和 Redis 客户端放到 app.state
  → yield：应用接收并处理多个请求
  → 关闭：redis.aclose() → engine.dispose()
```

构造 Engine 或 Redis 对象本身不保证服务连通，所以启动阶段执行实际命令。
启动失败也会进入清理逻辑；这里选择任一服务不可用就不启动。
`create_all` 只便于 demo 自动创建缺失的表，不负责已有表结构迁移。
`--reload` 重载会重新启动应用；多 worker 时，每个进程都有自己的资源，并非所有进程共用一个池。

### 2. 请求级 get_session

`Depends(get_session)` 让 FastAPI 在调用接口前运行依赖，到 `yield session` 时把 Session 传给接口。
请求结束后退出 `async with`，关闭 Session、回滚未提交的事务并归还借用的连接。

```text
请求到达 → 工厂创建独立 Session → yield session
  → 接口查询 / 修改 → 写操作显式 commit
  → 依赖清理 → Session 关闭，连接归还连接池
```

`Engine` 管连接池；`async_sessionmaker` 是创建 Session 的工厂；`AsyncSession` 管本次数据库操作和事务。
Session 不等于独占一条物理连接：通常在执行 SQL 时借用连接，事务结束后归还。
共享工厂可以，共享一个可变 Session 给多个请求或并发任务不可以。
`expire_on_commit=False` 避免提交后访问返回字段时触发自动重新加载。
即使详情缓存命中，依赖也会创建 Session 对象，但没有执行 SQL 就通常不需要借出数据库连接。

### 3. MySQL 与 Redis 的分工

MySQL 保存图书事实数据；Redis 只缓存详情 JSON，键为 `book:{id}`，TTL 为 60 秒。
新增直接写 MySQL；详情先读 Redis；修改和删除先提交 MySQL，再删除缓存；列表直接查 MySQL。
响应头 `X-Cache` 是观察缓存路径的入口。试着把 `ex=60` 改成 `ex=5`，等待 5 秒后再次查询，预期重新 MISS。

## 验证与停止

冒烟脚本使用真实 MySQL / Redis，并通过 TestClient 上下文触发 lifespan，无需运行 uvicorn：

```powershell
.\.venv\Scripts\python.exe -m pip install httpx2
.\.venv\Scripts\python.exe -X utf8 smoke.py
```

脚本检查 CRUD、MISS/HIT、修改后缓存失效、删除后的 404、非法输入，并删除本次创建的图书。
终端会打印 STARTUP、SESSION、SHUTDOWN；成功时最后输出 PASS。

uvicorn 终端按 Ctrl+C，观察资源关闭日志。停止数据库容器（保留 MySQL 数据卷）：

```powershell
docker compose down
```

## 演示边界

省略鉴权、分页、迁移工具、缓存故障降级和并发一致性处理。Redis 故障时请求可能失败；
MySQL 提交和 Redis 删缓存不是原子操作，删缓存失败时数据可能已更新，缓存可能陈旧；并发读写也可能重新写入旧缓存。
TTL 只限制陈旧数据的存活时间。本例适合顺序操作学习，不作为生产实现。

## 官方参考

- [FastAPI Lifespan](https://fastapi.tiangolo.com/advanced/events/)
- [SQLAlchemy 异步 Engine、Session 与清理](https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html)
- [Redis asyncio 客户端与关闭](https://redis.io/docs/latest/develop/clients/redis-py/async/)
