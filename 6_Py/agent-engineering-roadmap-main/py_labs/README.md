# Agent 工程化 Roadmap — Python + Gemini 版

本目录包含由原 TypeScript Labs 改写的 **Python 3.10+ 原生 Agent 实战代码**。底层基于 **Google Gemini API (`google-genai` SDK)** 实现，无需第三方 Agent 框架（如 LangChain / CrewAI）。

## 准备工作

1. 安装依赖：
```bash
pip install -r py_labs/requirements.txt
```

2. 设置 Gemini API 密钥：
```bash
# Linux/macOS
export GEMINI_API_KEY=your_gemini_api_key_here

# Windows PowerShell
$env:GEMINI_API_KEY="your_gemini_api_key_here"
```

## 4 个 Lab 实验

| Lab | 内容 | 运行命令 |
|---|---|---|
| **`01_streaming_llm`** | Gemini 异步流式 API 连接与背压 | `python py_labs/01_streaming_llm/main.py` <br> `python py_labs/01_streaming_llm/smoke.py` |
| **`02_terminal_ui`** | Rich 驱动的响应式终端 UI | `python py_labs/02_terminal_ui/main.py` <br> `python py_labs/02_terminal_ui/smoke.py` |
| **`03_first_tool`** | Gemini Function Calling 工具系统 | `python py_labs/03_first_tool/main.py` <br> `python py_labs/03_first_tool/smoke.py` |
| **`04_agentic_loop`** | AsyncGenerator 原生 Agent 循环引擎 | `python py_labs/04_agentic_loop/main.py` <br> `python py_labs/04_agentic_loop/smoke.py` |
