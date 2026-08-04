from dataclasses import dataclass
from typing import Literal, AsyncGenerator, Callable
from google.genai import types
from client.stream_message import stream_message, StreamResult, StreamEvent
import sys
from tools.executor import execute_tools
from tools.index import get_tool


@dataclass
class LoopResult:
    reason: Literal["completed", "max_turns", "error"]


@dataclass
class LoopEvent:
    type: Literal["text", "tool_start", "tool_done", "turn_complete"]
    text: str = ""
    result: LoopResult | None = None


async def query(
    contents: list[types.Content],
    tools: list[types.Tool],
    check: Callable | None = None,
) -> AsyncGenerator[LoopEvent, None]:
    teir = 1
    while teir <= 10:
        teir += 1
        result: StreamResult | None = None
        async for event in stream_message(contents=contents, tools=tools):
            if event.type == "text":
                yield LoopEvent(type="text", text=event.text)
            if event.type == "message_done":
                result = event.result

        if result is None:
            sys.stderr.write("在agentic_loop未收到result")
            sys.exit(1)
        model_content = types.Content(role="model", parts=result.raw_parts)
        contents.append(model_content)

        if result.function_calls:
            yield LoopEvent(type="tool_start")

            is_denied = False
            for fc in result.function_calls:
                t = get_tool(fc.name)

                if t and not t.read_only:
                    if check:
                        allowed = await check(fc.name, fc.args)
                        if not allowed:
                            is_denied = True
                            break

            if is_denied:
                parts = []
                for fc in result.function_calls:
                    parts.append(
                        types.Part.from_function_response(
                            name=fc.name,
                            response={"error": "user拒绝了你的修改请求。"},
                        )
                    )
                tool_content = types.Content(role="user", parts=parts)
            else:
                tool_content = await execute_tools(result.function_calls)
            contents.append(tool_content)
            yield LoopEvent(type="tool_done")
        else:
            # 模型不再调用工具，正常结束
            yield LoopEvent(
                type="turn_complete",
                result=LoopResult(reason="completed"),
            )
            return

    # while 循环耗尽（超过最大轮次）
    yield LoopEvent(
        type="turn_complete",
        result=LoopResult(reason="max_turns"),
    )
