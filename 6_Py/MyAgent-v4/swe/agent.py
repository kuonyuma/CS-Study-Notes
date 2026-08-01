import sys
from pathlib import Path

# 1. 优先设置 Path
BASE_DIR = Path(__file__).resolve().parent.parent
if str(BASE_DIR) not in sys.path:
    sys.path.append(str(BASE_DIR))

# 2. 再导入项目内部的包
import asyncio
from collections.abc import Callable
from google.genai import types
from llm import gemini
from llm.gemini import stream_result, stream_event
from memory.memory_manager import compress_contents
from Tools.read import read as read_file
from typing import AsyncGenerator

PROMPT_PATH = BASE_DIR / "data" / "swe-prompt.txt"
DEFAULT_MODEL_NAME = "gemini-3.6-flash"


class Agent:
    def __init__(self, client, model_name: str | None = None):
        self.client = client
        self.model_name = model_name or DEFAULT_MODEL_NAME
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
                "output": f"未注册的工具: {call.name}",
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
            # 对话历史过长时先压缩，避免超出上下文窗口
            self.contents = await compress_contents(
                self.contents, self.client, self.model_name
            )
            result: stream_result | None = None
            last_error_text = ""
            tools = None
            if self.tools:
                tools = [
                    types.Tool(
                        function_declarations=[
                            types.FunctionDeclaration.from_callable_with_api_option(
                                callable=fn
                            )
                            for fn in self.tools.values()
                        ]
                    )
                ]

            # 1. 发送包含完整 Content 历史的请求
            async for event in gemini.generate(
                self.client,
                self.model_name,
                contents=self.contents,
                system_instruction=self.system_prompt,
                function_calls=tools,
            ):
                if event.type == "end":
                    result = event.result
                elif event.type == "error":
                    last_error_text = event.text or last_error_text
                else:
                    yield event

            if result is None:
                # 连续失败达到预算后放弃，否则保留历史重试
                self.consecutive_errors += 1
                if self.consecutive_errors >= self.max_retry_budget:
                    yield stream_event(
                        type="error", text=last_error_text or "模型连续调用失败"
                    )
                    return
                continue

            self.consecutive_errors = 0

            # 将模型的输出保存为历史
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

        yield stream_event(
            type="error", text=f"达到最大迭代次数 {self.max_tier}，任务未完成"
        )
