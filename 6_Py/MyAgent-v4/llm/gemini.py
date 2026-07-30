from google import genai
from google.genai import types
from typing import Optional, Union, List
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
    contents: Union[str, List[types.Content]],
    system_instruction: Optional[str] = None,
    function_calls: Optional[list[types.Tool]] = None,
) -> AsyncGenerator[stream_event, None]:
    try:
        config = types.GenerateContentConfig(
            temperature=1.0,
            max_output_tokens=8192,
            system_instruction=system_instruction,
        )
        response = await client.aio.models.generate_content_stream(
            model=model_name, contents=contents, config=config,
            tools=function_calls,
        )

        input_len = 0
        output_len = 0
        stop = "end"
        calls = []
        full_text: str = " "
        full_parts = []
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
                    stop = candidates.finish_reason
                if candidates.content and candidates.content.parts:
                    for part in candidates.content.parts:
                        full_parts.append(part)
                        if part.text:
                            full_text += part.text
                            yield stream_event(type="text", text=part.text)
                        if part.function_call:
                            fc = part.function_call
                            calls.append(fc)
                            yield stream_event(type="tool_use", name=fc.name)

        model_content = types.Content(role="model", parts=full_parts)

        result = stream_result(
            full_text=full_text,
            stop_reason="tool_use" if calls else stop,
            used={"input_tokens": input_len, "output": output_len},
            function_calls=calls,
            model_content=model_content,
        )

        yield stream_event(type="end", result=result)
    except Exception as e:
        logging.error(f"连接 Gemini 发生错误: {e}")
        return
