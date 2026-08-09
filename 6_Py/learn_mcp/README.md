# learn_mcp

用最小可运行代码理解 MCP（Model Context Protocol）概念：一个 Gemini Agent + 一个 MCP Server。

## 架构

```
+---------------------------------+         +-----------------------------+
|  agent.py (Client 角色)         |  stdio  |  server.py (Server 角色)    |
|                                 |<=======>|                             |
|  1. 拉起 server.py 子进程        |  JSON   |  FastMCP: add / get_time /  |
|  2. initialize() 握手           |  协议    |  todo 增查 (内存状态)       |
|  3. list_tools() 发现工具        |         |                             |
|  4. 把工具声明转成 FunctionCall  |         |                             |
|  5. Gemini 决定调哪个工具        |         |                             |
|  6. session.call_tool() 转发     |         |                             |
|  7. 结果回传给 Gemini 生成答案   |         |                             |
+---------------------------------+         +-----------------------------+
```

**角色划分**：Agent 是"大脑+客户端"，负责理解意图、决策工具；Server 是"工具提供方"，只负责干活，不知道也不关心谁在调用。两者通过 MCP 协议在 stdio 上以 JSON 消息通信，互不依赖。

## 运行

```powershell
# 依赖（本机若已装 mcp / google-genai 可跳过）
pip install -r requirements.txt

# 方式一：手动工具循环版（能看到每一步，推荐先跑这个）
python agent.py

# 方式二：官方自动版（tools=[session] 一行接入，SDK 全自动）
python agent_auto.py
```

需要 `GEMINI_API_KEY` 环境变量。启动后输入问题，`exit` 退出。

默认模型 `gemini-3.5-flash`，可通过 `GEMINI_MODEL` 环境变量覆盖（`gemini-2.5-flash` 已对新用户停用，故不设它）。

推荐问题（能逼 Gemini 去调用工具）：

```
Use the add tool to compute 1234567 + 7654321
Use the get_current_time tool to tell me the current time
Add a todo item "buy milk", then list all todos
```

## 两个 Agent 的对比

| | agent.py | agent_auto.py |
|---|---|---|
| 工具发现 | 手动 `session.list_tools()` + 自己转 `FunctionDeclaration` | SDK 内部完成 |
| 调用循环 | 手写：拦截 function_call → call_tool → function_response 回传 | SDK 自动函数调用（AFC） |
| 可见性 | 每一步都打印 `[MCP] 调用工具/返回结果` | 只看到最终答案 |
| 适合 | 理解 MCP 工作原理 | 日常开发直接用 |

## MCP 核心概念对照代码

| 概念 | 出现在哪里 |
|---|---|
| Server / Tool | `server.py` 的 `@mcp.tool()` 装饰器 |
| 协议握手 / 会话 | `agent.py` 的 `ClientSession.initialize()` |
| 工具发现 (list_tools) | `agent.py` 的 `session.list_tools()` |
| 工具调用 (call_tool) | `agent.py` 的 `session.call_tool(name, arguments)` |
| 服务器状态保持 | `server.py` 的 `_todos` 列表：跨多次调用仍存在 |
| 传输层 (stdio) | `stdio_client(StdioServerParameters(...))` |

## 踩过的坑

- **genai 2.14~2.17 的 `tools=[session]` 深拷贝 bug**：SDK 内部对 config 做 `model_copy(deep=True)`，而 `mcp.ClientSession` 内部含 asyncio Future 无法 pickle，直接传 session 会报 `TypeError: cannot pickle '_asyncio.Future' object`。`agent_auto.py` 里的 `DeepCopySafeClientSession` 子类（`__deepcopy__` 返回自身）绕开此问题，同时保留 SDK 的 isinstance 识别。
- **模型停用**：`gemini-2.5-flash` 已对新用户不可用，API 返回 404，需换 `gemini-3.5-flash`。
- **Windows 编码**：管道重定向输出时 Python 会用 GBK 编码导致中文乱码，代码里 `sys.stdout.reconfigure(encoding="utf-8")` 已处理。
