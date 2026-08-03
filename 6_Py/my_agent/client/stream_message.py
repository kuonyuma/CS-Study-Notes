from dataclasses import dataclass, field
from google.genai import types
from typing import Any, Literal, Optional, AsyncGenerator
from client.client import get_client


@dataclass
class Stream_Result:
    contents: str
    stop_reason: str
    usage: dict[str, Any]
    function_calls: list = field(default_factory=list)
    raw_parts: list = field(default_factory=list)


@dataclass
class Stream_Event:
    type: Literal["text_start", "text_done", "tool_start", "tool_done", "text"]
    text: str = ""
    id: str = ""
    name: str = ""
    result: Stream_Result | None = None


async def stream_message(
    contents: Any,
    system_prompt: str | None = "你是以为编程助手",
    max_tokens: int | None = None,
    tools: list[Any] | None = None,
) -> AsyncGenerator[Stream_Event, None]:
    client = get_client()

    config = types.GenerateContentConfig(
        system_instruction=system_prompt, max_output_tokens=max_tokens, tools=tools
    )
    response = await client.aio.models.generate_content_stream(
        model="gemini-3.6-flash", contents=contents, config=config
    )

    yield Stream_Event(type="text_start")

    full_text: str = ""
    full_parts: list = []
    calls: list = []
    input: int = 0
    output: int = 0
    stop: str = "message_done"

    async for chunk in response:
        if chunk.usage_metadata:
            input = getattr(chunk.usage_metadata, "prompt_token_count", input)
            output = getattr(chunk.usage_metadata, "candidates_token_count", output)
        if chunk.candidates:
            candidate = chunk.candidates[0]
            if candidate.finish_reason:
                stop = str(candidate.finish_reason)
            if candidate.content and candidate.content.parts:
                parts = candidate.content.parts
                full_parts.append(parts)
                for part in candidate.content.parts:
                    if part.text:
                        full_text += part.text
                        yield Stream_Event(type="text", text=part.text)
                    if part.function_call:
                        fc = part.function_call
                        calls.append(fc)
                        yield Stream_Event(
                            type="tool_start",
                            id=fc.id or "",
                            name=fc.name or "",
                        )

    result = Stream_Result(
        contents=full_text,
        stop_reason="tool_use" if calls else stop,
        usage={"input_len": input, "output_len": output},
        function_calls=calls,
        raw_parts=full_parts,
    )

    yield Stream_Event(type="text_done", result=result)
