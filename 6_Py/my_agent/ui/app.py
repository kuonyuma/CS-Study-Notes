from google.genai import types
from prompt_toolkit import PromptSession
from prompt_toolkit.patch_stdout import patch_stdout
from rich.console import Console
from client.stream_message import stream_message, StreamResult
from rich.markdown import Markdown
from rich.live import Live
import sys
from tools.index import get_function_declarations
from tools.executor import execute_tools

console = Console()


class App:
    def __init__(self) -> None:
        self.contents: list[types.Content] = []
        self.session = PromptSession()

    async def run(self):

        while True:
            with patch_stdout():
                user_input: str = await self.session.prompt_async(">")
                user_text = user_input.strip()
            if not user_text:
                continue

            await self.run_turn(user_text)

    async def run_turn(self, query: str):

        user_content = types.Content(
            role="user", parts=[types.Part.from_text(text=query)]
        )
        self.contents.append(user_content)

        while True:
            result: StreamResult | None = None
            full_text: str = ""
            with Live(
                Markdown(full_text), console=console, refresh_per_second=15
            ) as live:
                async for event in stream_message(
                    contents=self.contents, tools=get_function_declarations()
                ):
                    if event.type == "text":
                        full_text += event.text
                        # console.print(event.text, end="", highlight=False)
                        live.update(Markdown(full_text))
                    if event.type == "message_done":
                        result = event.result

            if result is None:
                sys.stderr.write("在app.py未收到result")
                sys.exit(1)
            model_content = types.Content(role="model", parts=result.raw_parts)
            self.contents.append(model_content)

            if result.function_calls:
                console.print("开始调用工具...")

                result = await execute_tools(result.function_calls)
                self.contents.append(result)
                continue
            else:
                break
