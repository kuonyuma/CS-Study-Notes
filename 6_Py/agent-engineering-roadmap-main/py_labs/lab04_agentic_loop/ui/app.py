import asyncio
import sys
from typing import List, Dict, Any
from google.genai import types
from rich.console import Console
from prompt_toolkit import PromptSession
from prompt_toolkit.patch_stdout import patch_stdout
from py_labs.lab01_streaming_llm.client import MODEL
from py_labs.lab04_agentic_loop.core.agentic_loop import query, LoopEvent, LoopResult

console = Console()

class App:
    def __init__(self):
        self.contents: List[types.Content] = []
        self.session = PromptSession()

    async def permission_check(self, name: str, input_data: Dict[str, Any]) -> bool:
        console.print(f"\n[bold red]Permission Prompt:[/bold red] Agent wants to execute non-readonly tool [yellow]{name}[/yellow]")
        console.print(f"Arguments: {input_data}")
        try:
            with patch_stdout():
                answer = await self.session.prompt_async("Allow execution? (y/N): ")
            return answer.strip().lower() in ("y", "yes")
        except Exception:
            return False

    async def run(self):
        console.print(f"[bold cyan]Mini Coding Agent (Python + Gemini)[/bold cyan] [dim]({MODEL})[/dim]")
        console.print("[dim]Type your command. Enter to send, Ctrl+C to exit.[/dim]\n")

        while True:
            try:
                with patch_stdout():
                    user_input = await self.session.prompt_async("> ")
                user_text = user_input.strip()
                if not user_text:
                    continue

                self.contents.append(types.Content(role="user", parts=[types.Part.from_text(text=user_text)]))

                console.print()
                result: LoopResult = None
                async for event in query(
                    contents=self.contents,
                    can_use_tool=self.permission_check,
                ):
                    if event.type == "text":
                        console.print(event.text, end="", highlight=False)
                    elif event.type == "tool_start":
                        console.print(f"\n[bold yellow]🔧 Tool Call: {event.name}[/bold yellow] [dim]args: {event.input}[/dim]")
                    elif event.type == "tool_done":
                        color = "red" if event.is_error else "green"
                        console.print(f"[{color}]✔ Tool Finished ({event.name})[/{color}]")
                        console.print(f"[dim]{event.result[:200]}...[/dim]\n" if len(event.result) > 200 else f"[dim]{event.result}[/dim]\n")
                    elif event.type == "done":
                        result = event.loop_result

                console.print()
                if result:
                    self.contents = result.contents
                    console.print(
                        f"[dim]Turns: {result.turns} | Reason: {result.termination_reason} | "
                        f"Tokens: {result.usage['input_tokens']} in / {result.usage['output_tokens']} out[/dim]\n"
                    )

            except (KeyboardInterrupt, EOFError):
                console.print("\n[dim]Exiting... Goodbye![/dim]")
                break

async def main():
    app = App()
    await app.run()

if __name__ == "__main__":
    asyncio.run(main())
