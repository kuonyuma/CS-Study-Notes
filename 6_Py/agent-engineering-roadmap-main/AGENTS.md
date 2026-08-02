# AGENTS.md

## Project Overview

This is the **Agent Engineering Roadmap** — an educational repository maintained by [AI Builder Club](https://www.aibuilderclub.com). It provides a structured, 8-stage learning path (Stage 0–7) for understanding and building production-grade AI Agent systems. The repository includes 4 runnable Python lab experiments and curated reading guides covering everything from basic LLM streaming to production evaluation.

**Core thesis**: All serious coding agents (Claude Code, Cursor, Codex CLI, Aider, Windsurf, Gemini CLI) converge on the same **5-layer architecture**:

| Layer | Responsibility |
|---|---|
| **Interaction** | Terminal UI or editor interface — input handling, rendering, keyboard events |
| **Orchestration** | Session engine — multi-turn state, token budgets, slash commands, model switching |
| **Core Loop** | Agentic loop — model inference → tool call → observe result → decide next step |
| **Tools** | Capability extensions — file read/write, command execution, code search |
| **Communication** | Streaming LLM connection — message formatting, retries, token accounting |

## Repository Structure

```
agent-engineering-roadmap-main/
├── AGENTS.md                  # This file — agent coding guide
├── README_CN.md               # Main roadmap document (Chinese), all 8 stages
├── CONTRIBUTING.md            # Contribution guidelines
├── LICENSE                    # MIT License, (c) 2026 AI Builder Club
├── .gitignore                 # Ignores: node_modules/, dist/, .env, .DS_Store, *.log
├── assets/
│   └── roadmap.png            # Roadmap overview diagram
└── py_labs/                   # Python + Gemini lab experiments
    ├── README.md              # Python labs setup & run instructions
    ├── requirements.txt       # google-genai, rich, prompt_toolkit
    ├── lab01_streaming_llm/   # Lab 1: Streaming LLM connection
    ├── lab02_terminal_ui/     # Lab 2: Rich terminal UI with conversation memory
    ├── lab03_first_tool/      # Lab 3: Function calling & tool system
    └── lab04_agentic_loop/    # Lab 4: Full autonomous agentic loop
```

## Tech Stack

| Component | Technology |
|---|---|
| Language | Python 3.10+ |
| LLM SDK | `google-genai` ≥ 0.1.1 (Gemini API, model: `gemini-2.5-flash`) |
| Terminal UI | `rich` ≥ 13.0.0 (Live display, Markdown rendering, Panels, Console) |
| CLI Prompt | `prompt_toolkit` ≥ 3.0.0 (async input, patch_stdout) |
| Frameworks | **None** — the entire agent is built from scratch with raw API calls |
| Tests | Each lab includes a `smoke.py` for quick validation |

## Environment Setup

1. Install dependencies:
   ```bash
   pip install -r py_labs/requirements.txt
   ```
2. Set the Gemini API key:
   ```bash
   # Linux/macOS
   export GEMINI_API_KEY=your_key_here

   # Windows PowerShell
   $env:GEMINI_API_KEY="your_key_here"
   ```

## Lab Architecture (Progressive Complexity)

The 4 labs build **incrementally** — each lab imports modules from earlier labs. They are designed to be completed in order.

---

### Lab 01: `lab01_streaming_llm` — Streaming LLM Connection

**Purpose**: Establish an async streaming connection to the Gemini API with backpressure support.

| File | Role |
|---|---|
| `client.py` | Singleton `get_client()` factory. Validates `GEMINI_API_KEY` env var; exits gracefully if missing. Exports `MODEL` and `DEFAULT_MAX_TOKENS` constants. |
| `stream_message.py` | Core streaming wrapper. Defines `StreamEvent` (types: `message_start`, `text`, `tool_use_start`, `message_done`) and `StreamResult` dataclass. The `stream_message()` async generator yields `StreamEvent` objects as chunks arrive from Gemini. |
| `main.py` | CLI entry point consuming `stream_message()` via `async for`, outputting text chunks to stdout in real-time. |
| `smoke.py` | Automated test: sends "What is 2+2?", verifies non-empty streaming output. |

**Key pattern**: `stream_message()` is an `AsyncGenerator[StreamEvent, None]` — the foundational primitive reused by all subsequent labs.

---

### Lab 02: `lab02_terminal_ui` — Rich Terminal UI

**Purpose**: Render streamed LLM output in a responsive terminal UI with multi-turn conversation memory.

| File | Role |
|---|---|
| `client.py` | Re-exports `get_client`, `MODEL`, `DEFAULT_MAX_TOKENS` from Lab 01 |
| `stream_message.py` | Re-exports `stream_message`, `StreamEvent`, `StreamResult` from Lab 01 |
| `app.py` | `App` class holding `self.contents: List[types.Content]` (full conversation state). Uses `Rich.Live` + `Markdown` for real-time rendering and `prompt_toolkit.PromptSession` for input. `run_turn()` appends user message, streams response, updates history, prints token usage. |
| `main.py` | Entry point: instantiates and runs `App` |
| `smoke.py` | Tests single-turn execution of `App.run_turn()` programmatically |

**Key pattern**: Stateful conversation accumulator — `self.contents` grows across turns, providing the model with full history.

---

### Lab 03: `lab03_first_tool` — Function Calling & Tool System

**Purpose**: Implement Gemini Function Calling with an abstract tool base class and manual dispatch loop.

| File | Role |
|---|---|
| `tools/base.py` | Abstract `Tool(ABC)` class with `name`, `description`, `input_schema`, `read_only` properties and `async run()` method. `ToolResult` dataclass with `content` and `is_error`. `to_gemini_declaration()` converts tool to SDK `FunctionDeclaration`. |
| `tools/list_files.py` | `ListFilesTool(Tool)` — lists directory contents with `/` suffix for directories, sorted alphabetically |
| `tools/read_file.py` | `ReadFileTool(Tool)` — reads file content with 1-based line numbers (`{:4d} | line` format) |
| `tools/index.py` | Tool registry: `ALL_TOOLS` list, `find_tool(name)` lookup, `get_gemini_tools()` SDK wrapper |
| `execute_tools.py` | `execute_tools(function_calls)` — dispatches calls to handlers, returns `types.Content(role="user", parts=[FunctionResponse...])` |
| `main.py` | CLI app with two-phase tool execution cycle: LLM outputs tool call → execute → LLM receives results → synthesize response |
| `smoke.py` | Tests `ListFilesTool` and `ReadFileTool` directly |

**Key pattern**: Abstract `Tool` base class with `read_only` flag — this flag becomes critical in Lab 04 for permission control.

---

### Lab 04: `lab04_agentic_loop` — Full Autonomous Agent Loop

**Purpose**: The complete agentic loop as an `AsyncGenerator` yielding structured events, with human-in-the-loop permission for write operations.

| File | Role |
|---|---|
| `core/agentic_loop.py` | **The core engine.** `query()` async generator yields `LoopEvent`s (types: `text`, `tool_start`, `tool_done`, `turn_complete`, `done`). Autonomous loop: stream response → check for tool calls → permission check for non-read-only tools → execute → append results → next turn. Max turns capped to prevent infinite loops. Returns `LoopResult` with termination reason (`completed` / `max_turns` / `error`). |
| `tools/edit_file.py` | `EditFileTool(Tool)` — exact string replacement editor (`read_only = False`). Enforces unique `old_string` occurrence to prevent ambiguous edits. |
| `tools/index.py` | Extended registry: `ListFilesTool`, `ReadFileTool`, `EditFileTool` |
| `ui/app.py` | `App` class with `permission_check(name, input_data) -> bool` — interactive CLI hook that prompts `(y/N)` before allowing non-read-only tool execution. Rich-formatted output consuming `LoopEvent`s. |
| `main.py` | Entry point running `App` |
| `smoke.py` | Tests agentic loop with `auto_allow` callback (bypasses permission for automated testing) |

**Key patterns**:
- **Pull-based event stream**: The agent loop produces `LoopEvent`s via `yield`, completely decoupled from the UI layer.
- **Human-in-the-loop safety**: `PermissionCheck = Callable[[str, Dict[str, Any]], Awaitable[bool]]` — non-read-only tools require explicit user approval before execution.
- **Re-entrant autonomous loop**: The loop continues calling the model until it decides no more tool calls are needed, or max turns are reached.

## Key Design Patterns

1. **AsyncGenerator as core primitive** — Both streaming (`stream_message`) and the agentic loop (`query`) use async generator functions. Consumers pull events on demand with `async for`. This provides natural backpressure, clean control flow, and composability.

2. **Structured events over raw text** — `StreamEvent` (Lab 01) and `LoopEvent` (Lab 04) separate agent logic from presentation. The loop knows nothing about the UI; the UI knows nothing about LLM calls.

3. **Progressive module reuse** — Lab 02 re-exports Lab 01's client and streaming modules. Lab 04 extends Lab 03's tool system with `EditFileTool`. Each lab is a delta, not a rewrite.

4. **Abstract Tool base class** — `Tool(ABC)` with `read_only` flag enables the permission system in Lab 04. Tools declare their own schemas via `to_gemini_declaration()`.

5. **Human-in-the-loop permission** — Non-read-only tools require explicit approval. The permission callback is injected into `query()`, keeping the loop testable (use `auto_allow` for smoke tests).

6. **No framework dependency** — The agentic loop is built with pure Python. This is intentional — it teaches that the core loop is simple enough to own directly without LangChain/CrewAI.

7. **Smoke tests everywhere** — Every lab has a `smoke.py` that can be run standalone to validate functionality.

## Coding Conventions

- **SQL**: All keywords, table names, and column names in lowercase.
- **YAML**: All keys in lowercase.
- **Python style**: Follow PEP 8. Use `snake_case` for functions and variables, `PascalCase` for classes, `UPPER_SNAKE_CASE` for constants.
- **Module organization**: Each lab is a Python package with `__init__.py`. Tools live in a `tools/` subpackage with an `index.py` registry.
- **Cross-lab imports**: Labs may import from earlier labs (e.g., Lab 02 imports from `lab01_streaming_llm`).
- **Dataclasses**: Used for structured data types (`StreamEvent`, `StreamResult`, `ToolResult`, `LoopEvent`, `LoopResult`).

## Running Labs

```bash
# Run any lab interactively
python py_labs/lab01_streaming_llm/main.py
python py_labs/lab02_terminal_ui/main.py
python py_labs/lab03_first_tool/main.py
python py_labs/lab04_agentic_loop/main.py

# Run smoke tests
python py_labs/lab01_streaming_llm/smoke.py
python py_labs/lab02_terminal_ui/smoke.py
python py_labs/lab03_first_tool/smoke.py
python py_labs/lab04_agentic_loop/smoke.py
```

## Contribution Guidelines

- **Welcome**: Bug fixes, dependency bumps, broken link fixes, better free resources (with justification).
- **Not accepted**: Link swaps, sponsored placements, self-promotional additions, structural changes without prior issue discussion.
- **Ground rule**: Only list resources you'd genuinely recommend. Every addition needs a one-line justification in the PR.
- **Before submitting**: Ensure smoke tests pass for any lab changes.

## Roadmap Content Stages (Reference)

| Stage | Topic | Architecture Layer |
|---|---|---|
| 0 | Foundations (Function Calling, RAG, Prompt evolution) | Communication |
| 1 | Build Your First Agent (Labs 01–04) | All layers |
| 2 | Mastering Production Agents (Claude Code, Cursor, Codex) | Orchestration |
| 3 | Context Engineering (System Prompt, Memory, Token budget) | Orchestration + Core Loop |
| 4 | MCP & Skills (Model Context Protocol, tool extension) | Tools |
| 5 | Harness Engineering (Agent-ready codebases, sandboxing) | Tools + Interaction |
| 6 | Loop Engineering (Autonomous loops, verifiers, workflows) | Core Loop + Orchestration |
| 7 | Production (Evaluation, Reliability, Cost control) | All layers |
