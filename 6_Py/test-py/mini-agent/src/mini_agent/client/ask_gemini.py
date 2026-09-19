from google.genai import types
from collections.abc import AsyncGenerator
from dataclasses import dataclass
from typing import Literal, Any
from mini_agent.client.client import get_client
from mini_agent.config.settings import setting



@dataclass(frozen=True)
class Model_Result:
    full_text: str
    stop_reason: str
    calls: list[types.FunctionCall]
    usage: dict[str, Any]
    parts: list[types.Part]

@dataclass(frozen=True)
class ModelEvent:
    type:Literal["begin","end","tool","text"]
    tool_name:str = ""
    tool_id:str = ""
    text:str=""
    result:Model_Result|None = None

async def ask_gemini(
    contents: list[types.Content],
    system_prompt:str,
    tools: list[types.Tool] | None = None,
) -> AsyncGenerator[ModelEvent]:
    client = get_client()

    config = types.GenerateContentConfig(
        max_output_tokens=4999, tools=tools, system_instruction=system_prompt
    )
    response = await client.aio.models.generate_content_stream(
        model=setting.model.name,
        contents=contents,
        config=config,
    )
    full_text: str = ""
    calls: list[types.FunctionCall] = []
    usage: dict[str, Any] = {}
    full_parts = []
    stop:str = ""

    async for chunk in response:
        if chunk.usage_metadata is not None:
            usage["input"] = chunk.usage_metadata.prompt_token_count
            usage["output"] = chunk.usage_metadata.candidates_token_count
        if chunk.candidates and chunk.candidates[0]:
            candidate = chunk.candidates[0]
            if candidate.finish_reason is not None:
                stop = str(candidate.finish_reason)
            content = candidate.content
            if content and content.parts:
                parts = content.parts
                for part in parts:
                    full_parts.append(part)
                    if part.text:
                        full_text += part.text
                        yield ModelEvent(type="text", text=part.text)
                    if part.function_call:
                        yield ModelEvent(type="tool")
                        fc = part.function_call
                        calls.append(fc)
                        name = fc.name or ""
                        id = fc.id or ""
                        yield ModelEvent(
                            tool_name=name,
                            tool_id=id,
                            type="tool",
                        )

    result = Model_Result(
        full_text=full_text,
        calls=calls,
        parts=full_parts,
        usage=usage,
        sotp_reason=stop,
    )

    yield ModelEvent(type="end", result=result)
