from dataclasses import dataclass, field
from typing import AsyncGenerator, Literal, Any, List, Optional
from google.genai import types
from .client import get_client, MODEL, DEFAULT_MAX_TOKENS

@dataclass
class StreamResult:
    content: str
    stop_reason: str
    usage: dict
    function_calls: list = field(default_factory=list)
    raw_parts: list = field(default_factory=list)

@dataclass
class StreamEvent:
    type: Literal["message_start", "text", "tool_use_start", "message_done"]
    text: str = ""
    id: str = ""
    name: str = ""
    result: Optional[StreamResult] = None

async def stream_message(
    contents: Any,
    system_prompt: Optional[str] = "You are Gemini, a highly capable AI assistant developed by Google. You must never claim to be Claude, ChatGPT, or any other model.",
    max_tokens: Optional[int] = None,
    tools: Optional[List[Any]] = None,
) -> AsyncGenerator[StreamEvent, None]:
    """
    One API call to Gemini, streamed.
    Yields StreamEvents as chunks arrive, and sends the final StreamResult on 'message_done'.
    """
    client = get_client()
    config = types.GenerateContentConfig(
        system_instruction=system_prompt,
        max_output_tokens=max_tokens or DEFAULT_MAX_TOKENS,
    )
    if tools:
        config.tools = tools

    response_stream = await client.aio.models.generate_content_stream(
        model=MODEL,
        contents=contents,
        config=config,
    )

    yield StreamEvent(type="message_start")

    full_text = ""
    function_calls = []
    raw_parts = []
    finish_reason = "STOP"
    input_tokens = 0
    output_tokens = 0

    async for chunk in response_stream:
        if chunk.usage_metadata:
            input_tokens = getattr(chunk.usage_metadata, "prompt_token_count", input_tokens)
            output_tokens = getattr(chunk.usage_metadata, "candidates_token_count", output_tokens)

        if chunk.candidates:
            candidate = chunk.candidates[0]
            if candidate.finish_reason:
                finish_reason = str(candidate.finish_reason)

            if candidate.content and candidate.content.parts:
                for part in candidate.content.parts:
                    raw_parts.append(part)
                    if part.text:
                        full_text += part.text
                        yield StreamEvent(type="text", text=part.text)
                    if part.function_call:
                        fc = part.function_call
                        function_calls.append(fc)
                        yield StreamEvent(
                            type="tool_use_start",
                            id=getattr(fc, "id", fc.name),
                            name=fc.name,
                        )

    result = StreamResult(
        content=full_text,
        stop_reason="tool_use" if function_calls else finish_reason,
        usage={"input_tokens": input_tokens, "output_tokens": output_tokens},
        function_calls=function_calls,
        raw_parts=raw_parts,
    )
    yield StreamEvent(type="message_done", result=result)
