import os
import sys
from pathlib import Path
from llm.gemini import stream_event

# 1. 优先设置 Path
BASE_DIR = Path(__file__).resolve().parent.parent
if str(BASE_DIR) not in sys.path:
    sys.path.append(str(BASE_DIR))

# 2. 再导入项目内部的包
from memory.memory_manager import compress_contents
from collections.abc import Callable
from google import genai
from google.genai import types  # 补全 types 导入
from llm import gemini
from Tools.read import read as read_file
from Tools.terminal import terminal
from Tools.write import write as write_file
from llm.gemini import stream_result, stream_event, generate
import asyncio
from typing import AsyncGenerator


PROMPT_PATH = BASE_DIR / "data" / "swe-prompt.txt"


class Agent:
    def __init__(self, client):
        self.client = client
        self.mode_name = "gemini-3.6-flash"
        self.tools: dict[str, Callable] = {}
        self.contents: list[types.Content] = []
        self.max_tier = 10
        self.tier = 0
        self.system_prompt = self.load_template()
        self.consecutive_errors = 0
        self.max_retry_budget = 3

    def load_template(self):
        return read_file(str(PROMPT_PATH))

    def register(self, func: Callable):
        self.tools[func.__name__] = func

    async def execute(self, request: str) -> AsyncGenerator[stream_event, None]:
        """Agent 执行入口"""
        # 1. 每次新请求时重置对话历史
        self.contents = []
        self.tier = 0
        self.consecutive_errors = 0

        # 2. 将用户的初始请求封装为标准的 types.Content
        self.contents.append(
            types.Content(role="user", parts=[types.Part.from_text(text=request)])
        )

        yield stream_event("start")

        # 3. 开启思考循环
        async for event in self.think():
            yield event

    async def execute_tool(self, call: types.FunctionCall):
        tool = self.tools.get(call.name or "")

        if tool is None:
            formatted_response = {
                "status": "FAILED",
                "error_message": f"未注册的工具: {call.name}",
            }
        else:
            try:
                args = dict(call.args) if call.args else {}
                result = await asyncio.to_thread(tool, **args)
                formatted_response = {"status": "SUCCESS", "output": str(result)}
            except Exception as e:
                formatted_response = {"status": "FAILED", "output": str(e)}

        return types.Part.from_function_response(
            name=call.name or "", response=formatted_response
        )

    async def think(self):
        while self.tier <= self.max_tier:
            self.tier += 1
            # self.contents = await compress_contents(
            #     self.contents, self.client, self.mode_name
            # )
            result: stream_result | None = None
            # 1. 发送包含完整 Content 历史的请求
            async for event in gemini.generate(
                self.client,
                self.mode_name,
                contents=self.contents,
                system_instruction=self.system_prompt,
            ):
                if event.type == "end":
                    result = event.result
                else:
                    yield event

            if result is None:
                yield stream_event(type="error")
                return
            # 将模型的输full_text保存
            if result.model_content:
                self.contents.append(result.model_content)

            function_calls = result.function_calls

            if not function_calls:
                yield stream_event(type="end", result=result)
                return
            tool_response_parts = []
            for call in function_calls:
                part = await self.execute_tool(call)
                tool_response_parts.append(part)
            self.contents.append(types.Content(role="user", parts=tool_response_parts))
