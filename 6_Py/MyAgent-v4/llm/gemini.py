from google import genai
from google.genai import types
from typing import Optional, Union
from dataclasses import dataclass, field
from typing import Literal, AsyncGenerator
import logging


@dataclass
class stream_result:
    used: dict
    stop_reason: str
    full_text: str
    function_calls: list = field(default_factory=list)
    model_content: types.Content | None = None


@dataclass
class stream_event:
    type: Literal["text", "start", "tool_use", "end", "error"]
    result: None | stream_result = None
    text: str = ""
    name: str = ""


async def generate(
    client: genai.Client,
    model_name: str,
    contents: Union[str, list[types.Content]],
    system_instruction: Optional[str] = None,
    function_calls: Optional[list[types.Tool]] = None,
) -> AsyncGenerator[stream_event, None]:
    """流式调用 Gemini，并把跨 chunk 拆分的函数调用合并为完整调用。"""

    pending: types.FunctionCall | None = None
    calls: list[types.FunctionCall] = []
    full_parts: list[types.Part] = []

    def flush_pending() -> stream_event | None:
        """把尚未完成的函数调用收尾：写入结果并发出 tool_use 事件。"""
        nonlocal pending
        if pending is None:
            return None
        fc = pending
        pending = None
        calls.append(fc)
        full_parts.append(types.Part(function_call=fc))
        return stream_event(type="tool_use", name=fc.name or "")

    try:
        config = types.GenerateContentConfig(
            temperature=1.0,
            max_output_tokens=8192,
            system_instruction=system_instruction,
            tools=function_calls,
        )
        response = await client.aio.models.generate_content_stream(
            model=model_name,
            contents=contents,
            config=config,
        )

        input_len = 0
        output_len = 0
        stop = "end"
        full_text: str = ""
        async for chunk in response:
            if chunk.usage_metadata:
                input_len = getattr(
                    chunk.usage_metadata, "prompt_token_count", input_len
                )
                output_len = getattr(
                    chunk.usage_metadata, "candidates_token_count", output_len
                )

            if chunk.candidates:
                candidates = chunk.candidates[0]
                if candidates.finish_reason:
                    finish_reason = candidates.finish_reason
                    stop = getattr(finish_reason, "value", finish_reason)
                if candidates.content and candidates.content.parts:
                    for part in candidates.content.parts:
                        if part.text:
                            full_text += part.text
                            full_parts.append(part)
                            yield stream_event(type="text", text=part.text)
                        elif part.function_call:
                            fc = part.function_call
                            fc_name = fc.name or ""
                            fc_args = fc.args or {}

                            if fc_name and not fc_args:
                                # 新函数调用的“头”：只有名字，参数可能在后续 chunk
                                if pending is None or pending.name != fc_name or pending.args:
                                    event = flush_pending()
                                    if event:
                                        yield event
                                    pending = types.FunctionCall(name=fc_name, args={})
                            elif fc_name and fc_args:
                                if (
                                    pending is not None
                                    and pending.name == fc_name
                                    and not pending.args
                                ):
                                    # 补全上一条拆分调用的参数
                                    pending.args = {**(pending.args or {}), **fc_args}
                                    event = flush_pending()
                                    if event:
                                        yield event
                                else:
                                    event = flush_pending()
                                    if event:
                                        yield event
                                    calls.append(fc)
                                    full_parts.append(types.Part(function_call=fc))
                                    yield stream_event(type="tool_use", name=fc_name)
                            elif fc_args:
                                # 没有名字的参数块，合并到待处理的调用
                                if pending is None:
                                    pending = types.FunctionCall(name="", args={})
                                pending.args = {**(pending.args or {}), **fc_args}

        event = flush_pending()
        if event:
            yield event

        model_content = (
            types.Content(role="model", parts=full_parts) if full_parts else None
        )

        result = stream_result(
            full_text=full_text,
            stop_reason="tool_use" if calls else stop,
            used={"input_tokens": input_len, "output_tokens": output_len},
            function_calls=calls,
            model_content=model_content,
        )

        yield stream_event(type="end", result=result)
    except Exception as e:
        logging.error(f"连接 Gemini 发生错误: {e}")
        yield stream_event(type="error", text=str(e))
