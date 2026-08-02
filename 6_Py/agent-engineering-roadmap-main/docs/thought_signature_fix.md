# Gemini 3.x thought_signature 丢失问题修复指南

> 适用仓库：agent-engineering-roadmap-main
> 适用范围：Lab 03（`lab03_first_tool`）、Lab 04（`lab04_agentic_loop`），以及所有基于
> `lab01_streaming_llm.stream_message` 做工具调用的代码。

## 1. 问题现象

使用 `gemini-3.6-flash` 等 Gemini 3.x 模型运行 Lab 03 时，第一轮模型正常返回工具调用，
工具也执行成功，但紧接着的第二轮请求直接报错：

```text
Error: 400 INVALID_ARGUMENT. {
  'message': 'Function call is missing a thought_signature in functionCall parts.
              This is required for tools to work correctly, and missing
              thought_signature may lead to degraded model performance. ...'
}
```

Lab 04 的 `smoke.py`（会真实调用 API 并执行完整工具循环）同样会在第二轮报这个错。

## 2. 原因

从 Gemini 3 开始，Google 强制要求**每个函数调用都带 `thoughtSignature`**：

- 模型第一次返回 `functionCall` 时，响应里同时带一个 `thoughtSignature`（位于 Part 层，
  与 `functionCall` 平级），它把这次工具调用与模型的思考过程绑定在一起；
- 客户端把工具结果发回给模型时，**必须在对话历史里原样回传这个签名**，否则 API 拒绝请求。

实测原始响应长这样（`thoughtSignature` 是 `functionCall` 的兄弟字段）：

```json
{
  "functionCall": {"name": "list_files", "args": {"path": "."}, "id": "..."},
  "thoughtSignature": "Es8CCswCARFN..."
}
```

google-genai SDK（当前 `.venv` 安装的 2.14.0 已验证）会把它解析并保留在
`types.Part.thought_signature` 字段里。**问题出在仓库代码自己把签名丢了**：

1. `stream_message()` 只把 `part.function_call` 抽出来放进 `result.function_calls`，
   没有保留承载签名的完整 `Part`；
2. Lab 03 的 `main.py` 把模型回复写回历史时，用 `types.Part(function_call=fc)`
   重新构造 Part，只拷贝了函数调用，签名被丢弃；
3. Lab 04 的 `core/agentic_loop.py` 用 `types.Part.from_function_call(...)` 重建，
   同样丢弃签名（还顺带丢了 `id`）；
4. 第二轮请求里历史中的 `functionCall` part 没有 `thoughtSignature` → 400。

一句话：**签名不是 SDK 丢的，是应用层重建模型消息时丢的。**

## 3. 修改方案

### 方案 A（推荐）：保留完整原始 Part，不再重建

让 `StreamResult` 除了 `function_calls`（供判断/展示用）之外，再带一份**完整的原始
Part 列表**。上层回写历史时直接使用原始 Part，签名（以及 `id`）自然原样保留。

#### 3.1 修改 `py_labs/lab01_streaming_llm/stream_message.py`

给 `StreamResult` 增加字段：

```python
@dataclass
class StreamResult:
    content: str
    stop_reason: str
    usage: dict
    function_calls: list = field(default_factory=list)
    function_call_parts: list = field(default_factory=list)  # 新增：完整原始 Part
```

流式循环里收集 Part：

```python
    full_text = ""
    function_calls = []
    function_call_parts = []  # 新增
    finish_reason = "STOP"
```

```python
                    if part.function_call:
                        fc = part.function_call
                        function_calls.append(fc)
                        function_call_parts.append(part)  # 新增：保留完整 Part（含 thought_signature）
                        yield StreamEvent(
                            type="tool_use_start",
                            id=getattr(fc, "id", fc.name),
                            name=fc.name,
                        )
```

构造结果时带上新字段：

```python
    result = StreamResult(
        content=full_text,
        stop_reason="tool_use" if function_calls else finish_reason,
        usage={"input_tokens": input_tokens, "output_tokens": output_tokens},
        function_calls=function_calls,
        function_call_parts=function_call_parts,  # 新增
    )
```

#### 3.2 修改 `py_labs/lab03_first_tool/main.py`

把「逐个重建 Part」换成「直接扩展原始 Part」：

```python
            # Handle Tool Response if model requested tools
            if result and result.function_calls:
                parts = []
                if full_text:
                    parts.append(types.Part.from_text(text=full_text))
                parts.extend(result.function_call_parts)  # 保留完整 Part（含 thought_signature）
                contents.append(types.Content(role="model", parts=parts))
```

#### 3.3 修改 `py_labs/lab04_agentic_loop/core/agentic_loop.py`

同样替换重建逻辑：

```python
        # 2. Append assistant response into history
        parts = []
        if full_text:
            parts.append(types.Part.from_text(text=full_text))
        parts.extend(api_result.function_call_parts)  # 保留完整 Part（含 thought_signature）
        history_contents.append(types.Content(role="model", parts=parts))
```

### 方案 B（备选）：重建 Part 时手动带上签名

如果不想改 `StreamResult` 的数据结构，可以加一个平行的签名列表：

```python
function_call_parts: list = field(default_factory=list)
function_call_signatures: list = field(default_factory=list)
...
if part.function_call:
    function_calls.append(fc)
    function_call_parts.append(part)
    function_call_signatures.append(part.thought_signature)  # bytes 或 None
```

上层重建时：

```python
for fc, sig in zip(result.function_calls, result.function_call_signatures):
    parts.append(types.Part(function_call=fc, thought_signature=sig))
```

方案 B 也能通过验证，但多一份平行列表，且要求两列长度始终一致；**优先用方案 A**。

## 4. 可选改进：FunctionResponse 回填 `id`

修复后模型回复里 `functionCall.id` 会保留下来。规范做法是让对应的 `FunctionResponse`
也带上同一个 `id`，便于 API 精确匹配（尤其多工具并行时）。这不是本 bug 的必需修改。

`py_labs/lab03_first_tool/execute_tools.py`：

```python
        part = types.Part(
            function_response=types.FunctionResponse(
                id=getattr(fc, "id", None),
                name=name,
                response={"result": result_content},
            )
        )
```

`py_labs/lab04_agentic_loop/core/agentic_loop.py` 中同样把已有的 `call_id` 填进
`FunctionResponse.id`。

## 5. 验证方法

### 5.1 最快验证：Lab 04 smoke（真实 API 往返）

```powershell
python py_labs/lab04_agentic_loop/smoke.py
```

- 修改前：第二轮触发工具结果回传时报 400，`SMOKE FAIL`；
- 修改后：完成工具调用并输出最终答复，最后打印 `SMOKE OK`。

### 5.2 Lab 03 交互验证

```powershell
python py_labs/lab03_first_tool/main.py
```

输入会触发工具调用的指令（例如让模型读取 `test/demo1.py`）：

```text
> 你好，请阅读A:\Root_Code\local-project\agent-engineering-roadmap-main\test\demo1.py下的内容
```

修改前：`[Tool Call: read_file]` → `Executing tool...` → 400 报错；
修改后：工具执行完，模型能正常总结文件内容，无报错。

### 5.3 注意：Lab 03 的 smoke 覆盖不到本 bug

`py_labs/lab03_first_tool/smoke.py` 只直接调用 `ListFilesTool` / `ReadFileTool`，
不经过 API 往返，所以它修改前后都会 `SMOKE OK`，不能用来验证本修复。

## 6. 注意事项

- **SDK 版本**：本修复依赖 `google.genai.types.Part.thought_signature` 字段，当前
  `.venv` 安装的 google-genai 2.14.0 已验证支持。仓库 `requirements.txt` 写的是
  `google-genai>=0.1.1`，如果某个环境装的是很旧的版本，请先升级：

  ```powershell
  pip install -U google-genai
  ```

  升级后可用一行命令确认字段存在：

  ```powershell
  python -c "from google.genai import types; print(hasattr(types.Part, 'thought_signature'))"
  ```

  输出应为 `True`。

- **签名不能复用/跨会话缓存**：每个 `thoughtSignature` 都绑定它对应的那一次
  `functionCall` 和当前对话。必须在该轮对话历史里原样回传；不要把它挪到别的
  函数调用上，也不要跨对话保存复用。

- **不要只拷贝 `function_calls` 里的对象**：`FunctionCall` 对象本身没有
  `thought_signature` 字段，签名在 `Part` 上。任何「从 `function_call` 重建 Part」
  的写法都会再次丢签名。

- **Lab 01 / Lab 02 不受影响**：它们不涉及工具调用，无需修改。
