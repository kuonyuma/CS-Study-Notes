import sys
import os
import asyncio

# 将当前文件的上一级目录（也就是项目根目录）强行加入到 Python 的搜索路径中
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

# 下面再写你原本的导入代码：
from google.genai import types
from prompt_toolkit import PromptSession
from prompt_toolkit.patch_stdout import patch_stdout
from rich.console import Console
from py_labs.lab01_streaming_llm.stream_message import stream_message

# ... 后面的 App 代码保持不变
console = Console()


class App:
    def __init__(self) -> None:
        self.contents: list[types.Content] = []
        self.session = PromptSession()

    async def run(self) -> str:

        while True:
            with patch_stdout():
                suer_input = await self.session.prompt_async("> ")
            user_text = suer_input.strip()
            if not user_text:
                continue
            await self.run_turn(user_text)

    async def run_turn(self, user_text: str):

        content = types.Content(
            role="user", parts=[types.Part.from_text(text=user_text)]
        )
        self.contents.append(content)

        full_text: str = ""
        result = None
        async for event in stream_message(contents=self.contents):
            if event.type == "text":
                full_text += event.text
                console.print(event.text, end="", highlight=False)
            if event.type == "message_done":
                result = event.result

        print()

        assistant_content = types.Content(
            role="model", parts=[types.Part.from_text(text=full_text)]
        )
        self.contents.append(assistant_content)

        if result and result.usage:
            print(result.usage["input_tokens"], result.usage["output_tokens"])


if __name__ == "__main__":
    app = App()
    asyncio.run(app.run())
