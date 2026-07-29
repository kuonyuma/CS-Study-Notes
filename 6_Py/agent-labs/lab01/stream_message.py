from dataclasses import dataclass, field
from typing import Literal, Union, Optional, Any, AsyncGenerator
from google.genai import types
from client import get_client


@dataclass
class stream_result:
    contents: str
    stop_reason: str
    usage: dict
    function_calls: list = field(default_factory=list)


@dataclass
class stream_event:
    type: Literal["text", "message_start", "tool_use_start", "message_done"]
    text: str = ""
    name: str = ""
    id: str = ""
    result: Union[None, "stream_result"] = None


async def stream_message(
    contents: Any,
    max_tokens: Optional[int] = None,
    system_prompt: Optional[str] = "你是一个coding助手",
    tools: Optional[list[Any]] = None,
) -> AsyncGenerator[stream_event, None]:

    # 拿到客户端
    client = get_client()
    # 建立通信
    config = types.GenerateContentConfig(
        max_output_tokens= max_tokens or 4096,
        system_instruction=system_prompt
    )

    config.tools = tools

    response = await client.aio.models.generate_content_stream(
        model="gemini-3.6-flash", contents=contents, config=config
    )

    # 通知开始接收数据
    yield stream_event(type="message_start")

    input_len = 0
    output_len = 0
    full_contents: str = ""
    calls = []
    stop_reason = "STOP"
    async for chunk in response:
        if chunk.usage_metadata:
            input_len = getattr(chunk.usage_metadata, "prompt_token_count", input_len)
            output_len = getattr(
                chunk.usage_metadata, "candidates_token_count", output_len
            )

        if chunk.candidates:
            first_ret = chunk.candidates[0]
            if first_ret.finish_reason:
                stop_reason = first_ret.finish_reason

            if first_ret.content and first_ret.content.parts:
                for part in first_ret.content.parts:
                    if part.text:
                        full_contents += part.text
                        yield stream_event(type="text", text=part.text)
                    if part.function_call:
                        fc = part.function_call
                        calls.append(fc)
                        yield stream_event(
                            type="tool_use_start",
                            id=getattr(fc, "id", fc.name),
                            name=fc.name,
                        )
    result = stream_result(
        contents= full_contents,
        stop_reason= "tool_use" if calls else stop_reason,
        usage={"input_tokens":input_len,"output_tokens":output_len},
        function_calls=calls
    )

    yield stream_event(type="message_done",result=result)
