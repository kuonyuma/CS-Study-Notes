from google.genai import types
from google import genai
from collections.abc import AsyncGenerator
from dataclasses import dataclass
from typing import Literal,Any
import os

@dataclass(frozen=True)
class Model_Event:
    type:Literal["start","text","tool_begin","done"]
    text:str = ""
    tool_id:str = ""
    tool_name:str = ""
    result:Model_Result|None = None

@dataclass(frozen=True)
class Model_Result:
    full_text :str
    sotp_reason:str
    calls:list[types.FunctionCall]
    usege:dict[str,Any]
    parts:types.Part
    

async def ask_gemini(
        contents:list[types.Content],
        tools:list[types.Tool] | None = None,
        system_prompt:str = "你是一位coding助手"
)->AsyncGenerator[Event|None]:
    key = os.getenv("GEMINI_API_KEY",default="") 
    client = genai.Client(api_key=key)
    config = types.GenerateContentConfig(
        max_output_tokens=4999,
        tools=tools,
        system_instruction=system_prompt
    )
    response = await client.aio.models.generate_content_stream(
        model="gemini-3.8-flash",
        contents=contents,
        config=config,
    )
    full_text : str =""
    calls:list[types.FunctionCall] = []
    async for chunk in response:
        if chunk.candidates and chunk.candidates[0].content:
            content = chunk.candidates[0].content
            if content.parts:
                parts = content.parts
                for part in parts:
                    if part.text:
                        full_text += part.text
                        yield Event(type="text",text=part.text)
                    if part.function_call:
                        fc = part.function_call
                        calls.append(fc)
                        name = fc.name or ""
                        id = fc.id or ""
                        yield Event(
                            tool_name=name,
                            tool_id=id,
                            type="tool",
                        )

    result = Result(full_text=full_text,calls=calls)

    yield Event(type="done",result=result)




        



