# AI Agent Framework

A custom-built AI Agent framework implemented in pure Python from the ground up, utilizing the `google-genai` SDK. This project focuses on building a deep understanding of the core LLM execution loop, streaming responses, and function calling without relying on external abstraction libraries.

## Architecture & Logic

The system is designed with a 4-layer architecture, emphasizing the decoupling of client connection, tool execution, terminal UI, and the core agentic loop.

### 1. Streaming & Client Initialization (`client/`)
- **API Connection**: Manages the singleton client setup using the `GEMINI_API_KEY`.
- **Async Event Stream**: The interaction with the Gemini API (`gemini-3.6-flash`) is encapsulated in an `AsyncGenerator`. As data arrives, it parses out textual chunks and function calling declarations, packaging them into structured `StreamEvent` and `StreamResult` objects. This allows the UI to render tokens instantly while deferring tool execution logic to the engine.

### 2. Core Agentic Loop (`core/agentic_loop.py`)
- **State Machine**: The `query()` generator drives the autonomous decision-making process. It operates in a `while` loop (capped at 10 turns to prevent infinite recursive calls).
- **Execution Flow**:
  1. Forward context history and tools to the LLM.
  2. Stream back the LLM's response.
  3. If no function is called, the loop yields a completion event and terminates.
  4. If a function is called, the loop delegates the permission check to the `check` callback.
  5. Upon approval, the loop invokes `execute_tools` to execute operations locally and appends the `tool_content` back to the context history.
  6. The loop iterates, letting the LLM review the tool's result.

### 3. Tool Registration & Execution (`tools/`)
- **Abstract Design**: Every tool implements the `Tool` base class, defining its `name`, `description`, `input_schema`, and a `read_only` boolean flag.
- **Dynamic Declarations**: The system automatically iterates over registered tools (like `ListFiles` and `EditFile`) to generate `google.genai.types.Tool` schemas for the Gemini API context.

### 4. Terminal Interface & Security (`ui/app.py`)
- **Human-in-the-Loop**: Write operations (where `read_only = False`) such as `EditFile` must trigger a terminal prompt. The user is presented with the tool name and exact parameters and must type `y` to approve the action. If rejected, the LLM is informed of the rejection via simulated tool output.
- **Rich Display**: Built with `prompt_toolkit` for async command input and `rich.Live` for real-time markdown rendering.
