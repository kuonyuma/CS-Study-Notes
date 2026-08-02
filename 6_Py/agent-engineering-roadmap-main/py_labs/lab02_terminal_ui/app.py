import asyncio
import sys
from typing import List
from google.genai import types
from rich.console import Console
from prompt_toolkit import PromptSession
from prompt_toolkit.patch_stdout import patch_stdout
from .client import MODEL
from .stream_message import stream_message

console = Console()

class App:
    """
    The interaction layer in Python.
    Owns UI state and keyboard input. Consumes stream_message async generator.
    """
    def __init__(self):
        self.contents: List[types.Content] = []
        self.session = PromptSession()

    async def run(self):
        console.print(f"[bold cyan]Mini Agent[/bold cyan] [dim]({MODEL}) — Enter to send, Ctrl+C to quit[/dim]\n")

        while True:
            try:
                with patch_stdout():
                    user_input = await self.session.prompt_async("> ")
                user_text = user_input.strip()
                if not user_text:
                    continue
                await self.run_turn(user_text)
            except (KeyboardInterrupt, EOFError):
                console.print("\n[dim]Exiting... Bye![/dim]")
                break

    async def run_turn(self, user_text: str):
        # 1. Append user content
        user_content = types.Content(
            role="user",
            parts=[types.Part.from_text(text=user_text)]
        )
        self.contents.append(user_content)

        # 2. Stream assistant response
        full_text = ""
        result = None

        console.print()
        try:
            async for event in stream_message(contents=self.contents):
                if event.type == "text":
                    full_text += event.text
                    console.print(event.text, end="", highlight=False)
                elif event.type == "message_done":
                    result = event.result
        except Exception as err:
            console.print(f"\n[red]Error: {err}[/red]")
            return

        console.print() # newline after response

        # 3. Save assistant message into conversation history
        assistant_content = types.Content(
            role="model",
            parts=[types.Part.from_text(text=full_text)]
        )
        self.contents.append(assistant_content)

        if result and result.usage:
            console.print(
                f"[dim]tokens: {result.usage['input_tokens']} in / {result.usage['output_tokens']} out[/dim]\n"
            )

async def main():
    app = App()
    await app.run()

if __name__ == "__main__":
    asyncio.run(main())
