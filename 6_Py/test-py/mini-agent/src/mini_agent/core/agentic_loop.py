
from collections.abc import AsyncGenerator
from dataclasses import dataclass
from mini_agent.client.ask_gemini import ask_gemini
from mini_agent.client.ask_gemini import Event
from mini_agent.client.ask_gemini import Result
from google.genai import types
from typing import Literal,Any
import json
from hashlib import sha256
@dataclass
class LoopEvent:
    type:Literal["message","tool","done"]
    text:str = ""

@dataclass
class LoopReuslt:
    pass

@dataclass(frozen=True)
class ToolApprovalRequest:
    name:str
    id:str
    arguments:dict[str,Any]
    uuid:str

    @classmethod
    def create(cls,call_id,name,arguments):
        tmp = json.dumps(
            {
                "id":call_id,
                "name":name,
                "arguments":arguments,
            },
            ensure_ascii=False
        )
        return cls(
            id=call_id,
            name=tool_name,
            arguments=dict(arguments),
            request_hash=sha256(canonical.encode("utf-8")).hexdigest(),
        )

async def query(
    contents:list[types.Content],
    tools:list[types.Tool] |None = None,
    system_prompt:str ="",
    tier:int = 10,
)->AsyncGenerator[LoopEvent|None]:
    for _ in range(tier):
        #传递字符串累计工具
        async for event in ask_gemini(
            contents=contents,
            tools=tools,
            system_prompt=system_prompt,
        ):
            result: Result|None = None
            if event.type =="text":
                yield LoopEvent(text=event.text,type="message")
            if event.type == "done":
                result = event.result
        if result is None:
            print("未获取到result")

        if not result.calls:
            yield LoopEvent(type="done")
            return

        named_calls:list[str,str,types.FunctionCall,ToolApprovalRequest|None]=[]

        

                

        

        



