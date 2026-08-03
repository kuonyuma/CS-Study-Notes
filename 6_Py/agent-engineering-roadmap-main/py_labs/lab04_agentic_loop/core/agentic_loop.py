from dataclasses import dataclass, field
from typing import (
    AsyncGenerator,
    Callable,
    Awaitable,
    List,
    Dict,
    Any,
    Optional,
    Literal,
)
from google.genai import types
from py_labs.lab01_streaming_llm.stream_message import stream_message
from py_labs.lab04_agentic_loop.tools.index import find_tool, get_gemini_tools

PermissionCheck = Callable[[str, Dict[str, Any]], Awaitable[bool]]
TerminationReason = Literal["completed", "max_turns", "error"]


@dataclass
class LoopResult:
    contents: List[types.Content]
    termination_reason: TerminationReason
    turns: int
    usage: Dict[str, int]
    error_message: Optional[str] = None


@dataclass
class LoopEvent:
    type: Literal["text", "tool_start", "tool_done", "turn_complete", "done"]
    text: str = ""
    id: str = ""
    name: str = ""
    input: Dict[str, Any] = field(default_factory=dict)
    result: str = ""
    is_error: bool = False
    turn: int = 0
    loop_result: Optional[LoopResult] = None


async def query(
    contents: List[types.Content],
    system_prompt: Optional[
        str
    ] = "You are Gemini, a highly capable AI assistant developed by Google. You must never claim to be Claude, ChatGPT, or any other model.",
    max_turns: int = 10,
    max_tokens: Optional[int] = None,
    can_use_tool: Optional[PermissionCheck] = None,
) -> AsyncGenerator[LoopEvent, None]:
    """
    CORE LAYER.
    query() is the engine of the agent: a multi-step tool-calling loop,
    packaged as an AsyncGenerator. It orchestrates communication and tool execution.
    It yields events as they occur, sending the final LoopResult in the 'done' event.
    """
    history_contents = list(contents)
    usage = {"input_tokens": 0, "output_tokens": 0}
    turns = 0
    tools = get_gemini_tools()

    def make_finish(
        reason: TerminationReason, err_msg: Optional[str] = None
    ) -> LoopResult:
        return LoopResult(
            contents=history_contents,
            termination_reason=reason,
            turns=turns,
            usage=usage,
            error_message=err_msg,
        )

    while turns < max_turns:
        turns += 1

        api_result = None
        full_text = ""
        try:
            async for event in stream_message(
                contents=history_contents,
                system_prompt=system_prompt,
                max_tokens=max_tokens,
                tools=tools,
            ):
                if event.type == "text":
                    full_text += event.text
                    yield LoopEvent(type="text", text=event.text)
                elif event.type == "message_done":
                    api_result = event.result
        except Exception as err:
            res = make_finish("error", str(err))
            yield LoopEvent(type="done", loop_result=res)
            return

        if not api_result:
            res = make_finish("error", "No API result returned.")
            yield LoopEvent(type="done", loop_result=res)
            return

        usage["input_tokens"] += api_result.usage["input_tokens"]
        usage["output_tokens"] += api_result.usage["output_tokens"]

        # 2. Append assistant response into history
        if api_result.raw_parts:
            history_contents.append(
                types.Content(role="model", parts=api_result.raw_parts)
            )

        # 3. Check if done (no tool calls)
        if not api_result.function_calls:
            yield LoopEvent(type="turn_complete", turn=turns)
            res = make_finish("completed")
            yield LoopEvent(type="done", loop_result=res)
            return

        # 4. Execute tool calls sequentially
        response_parts = []
        for fc in api_result.function_calls:
            name = fc.name
            args = dict(fc.args) if fc.args else {}
            call_id = getattr(fc, "id", name)
            tool = find_tool(name)

            yield LoopEvent(type="tool_start", id=call_id, name=name, input=args)

            allowed = True
            if tool and not tool.read_only and can_use_tool:
                allowed = await can_use_tool(name, args)

            if not tool:
                res_content = f"Error: Tool '{name}' not found."
                is_err = True
            elif not allowed:
                res_content = f"Permission denied by user to run tool '{name}'."
                is_err = True
            else:
                try:
                    tool_res = await tool.run(args)
                    res_content = tool_res.content
                    is_err = tool_res.is_error
                except Exception as err:
                    res_content = f"Error executing tool '{name}': {err}"
                    is_err = True

            yield LoopEvent(
                type="tool_done",
                id=call_id,
                name=name,
                result=res_content,
                is_error=is_err,
            )
        yield LoopEvent(type="turn_complete", turn=turns)

    res = make_finish("max_turns")
    yield LoopEvent(type="done", loop_result=res)
