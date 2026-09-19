# Docker Redis + MySQL 数量级差距演示 Demo

本项目通过直观的对照实验展示 Redis 缓存的本质价值——不只是“快一点”，而是在高并发热点读取下带来 **4 个数量级** 的数据库减压差距与性能飞跃：

> 处理 20,000 次热点读取时，纯 MySQL 执行约 20,000 次查询；引入 Redis 后，MySQL **仅执行 1 次查询**，其余 19,999 次全部由 Redis 内存承载。

| 核心指标 | 纯 MySQL (场景 A) | Redis 缓存 (场景 B) | 对比效果 |
|---|---:|---:|---|
| **业务请求** | 20,000 次 | 20,000 次 | 对齐基准 |
| **MySQL 实际查询** | 20,000 次 | **1 次** | **消除 19,999 次查询** |
| **数据库减压率** | 0.00% | **99.995%** | **几乎完全卸载数据库压力** |
| **MySQL 查询减少倍数** | — | **20,000 倍** | **数量级削减（硬件无关）** |
| **吞吐量 (QPS)** | ~190 次/s | 数千 ~ 上万 次/s | **提升 10 ~ 100+ 倍** |
| **P95 延迟** | > 1,000 ms | < 20 ms | **尾延迟大幅抹平** |

> **提示**：数据库实际查询次数的差距（20,000 → 1）属于确定性逻辑指标，不受机器性能与 Docker 虚拟化波动影响，最具说服力。

---

## 1. 启动 Docker 中的 Redis 和 MySQL

在本目录执行：

```powershell
docker compose up -d
docker compose ps
```

MySQL 映射到本机 `3307` 端口，Redis 映射到本机 `6380` 端口。

如果第一次启动 MySQL 遇到权限问题，可执行下面命令进行授权（所有 SQL 关键字已小写）：

```powershell
docker compose exec -T mysql mysql -uroot -proot -e "create user if not exists 'root'@'%' identified by 'root'; grant all privileges on *.* to 'root'@'%' with grant option; flush privileges;"
```

---

## 2. 安装 Python 依赖

进入 Windows PowerShell 环境，若使用项目内置虚拟环境：

```powershell
python -m venv .venv
.\.venv\bin\python.exe -m pip install -r requirements.txt
```

---

## 3. 四大演示场景

### 场景 A：纯 MySQL 并发查询
20,000 个业务请求由 200 个并发线程发起，竞争容量为 10 的 MySQL 连接池。
- 连接池排队严重（190 个请求等待连接）。
- 每次查询执行 `select ... sleep(0.05) as simulated_delay` 模拟生产环境 50ms 网络/磁盘成本。
- 理论耗时：`20,000 × 50ms ÷ 10 = 100 秒`。

### 场景 B：Redis Cache-Aside 高并发
冷启动删除缓存，首个请求触发 Redis MISS，回源 MySQL 查询并写入 Redis；后续 19,999 次请求全量命中 Redis 缓存。
- MySQL 查询次数精确为 1。
- 数据库减压率达 99.995%。
- 总耗时降至 1~3 秒，QPS 达到数千至上万。

### 场景 C：缓存失效与重载 (Cache Invalidation)
更新 MySQL 中的商品价格后主动删除 Redis 缓存，随后连续发起 3 次读取：
1. **第 1 次读取**：MISS → 查询 MySQL 获得最新价格 → 写入 Redis 缓存。
2. **第 2 次读取**：HIT → 直接从 Redis 返回最新价格。
3. **第 3 次读取**：HIT → 直接从 Redis 返回最新价格。
证明缓存不是静态保存旧数据，而是具备失效后自动重载最新数据的生命周期。

### 场景 D：缓存击穿防护对比 (Breakdown / Mutex Lock)
当热点 Key 突然失效或冷启动瞬间，200 个并发线程在同一微秒涌入：
- **无保护模式**：200 个请求全部判定 MISS，同时穿透至 MySQL 连接池打满。
- **分布式互斥锁保护模式**：线程争抢 `set lock:product:1 1 nx ex 5`，仅 1 个线程获准查询 MySQL 重建缓存，其余 199 个线程等待让渡并重试命中缓存，实现 MySQL 查询精准收敛至 1 次。

---

## 4. 运行命令指南

### 4.1 标准全量基准压测 (推荐完整复现)

执行完整 20,000 请求、200 并发、50ms 模拟延迟的基准压测：

```powershell
.\.venv\bin\python.exe demo.py benchmark `
  --requests 20000 `
  --concurrency 200 `
  --mysql-pool-size 10 `
  --db-delay-ms 50
```

可附带 `--with-breakdown` 参数连同场景 D 击穿防护一起测试。

### 4.2 快速验证档 (10 秒快速查看效果)

执行 5,000 请求、100 并发、20ms 模拟延迟快速验证：

```powershell
.\.venv\bin\python.exe demo.py benchmark `
  --requests 5000 `
  --concurrency 100 `
  --mysql-pool-size 10 `
  --db-delay-ms 20
```

### 4.3 本机真实 0ms 测试 (无模拟延迟)

测试本机纯网络与单表主键读取的真实吞吐差距：

```powershell
.\.venv\bin\python.exe demo.py benchmark `
  --requests 20000 `
  --concurrency 200 `
  --mysql-pool-size 20 `
  --db-delay-ms 0
```

### 4.4 缓存失效与更新演示 (场景 C)

```powershell
.\.venv\bin\python.exe demo.py demo
```

### 4.5 缓存击穿防护对比测试 (场景 D)

```powershell
.\.venv\bin\python.exe demo.py breakdown `
  --concurrency 200 `
  --mysql-pool-size 10 `
  --db-delay-ms 50
```

### 4.6 交互式控制台菜单

直接运行脚本进入多功能操作菜单：

```powershell
.\.venv\bin\python.exe demo.py
```

---

## 5. 输出效果示例

```text
==================================================================
                    Redis 缓存效果对比报告
==================================================================
[*] 教学演示模式：每次 MySQL 查询模拟增加 50ms 耗时
[*] 压测配置：请求总数 = 20,000 | 并发线程 = 200 | MySQL 连接池 = 10
------------------------------------------------------------------
纯 MySQL
MySQL查询  ████████████████████████████████████    20,000次
Redis命中                                               0次

Redis 缓存
MySQL查询  ▏                                            1次
Redis命中  ████████████████████████████████████    19,999次
------------------------------------------------------------------
MySQL 查询次数：          20,000 →        1
数据库查询减少：              19,999 次
数据库减压率：                99.995%
MySQL 查询减少倍数：          20,000 倍 (数量级消除)
总耗时：                 104.20s →     2.10s (耗时降至 1/49.6)
吞吐量 (QPS)：             191.9 →    9523.8 (提速 49.6 倍)
P95 延迟：              1023.0ms →    18.0ms (延迟降至 1/56.8)
==================================================================
```

---

## 6. 底层原理剖析

1. **数据库连接是珍贵的有限资源**：
   - 在生产中，MySQL 连接是重量级线程资源，通常连接池配置在 10~50 左右。
   - 当 200 个并发请求涌入时，未持有连接的 190 个请求必须在连接池外等待锁，导致线程堆积和尾延迟飙升。
2. **Redis 纯内存寻址 vs 关系数据库执行计划**：
   - MySQL 每次查询必须经过 SQL 词法解析、优化器选择执行计划、InnoDB 锁检查与 Buffer Pool 查找。
   - Redis 基于内存数据结构与高效率单线程事件循环（Epoll/I/O 多路复用），O(1) 毫秒/微秒级响应。
3. **Cache-Aside 数量级减压**：
   - 热点数据一旦载入缓存，后续海量并发读全被内存层完全截胡，保护后端 MySQL 不被瞬间打垮。

---

## 7. 查看和停止容器

```powershell
docker compose logs mysql
docker compose logs redis
docker compose down
```

若要连同 MySQL 数据卷一起清除：

```powershell
docker compose down -v
```
