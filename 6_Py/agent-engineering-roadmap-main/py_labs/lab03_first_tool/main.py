import asyncio
import os
import sys
from google.genai import types
from rich.console import Console
from prompt_toolkit import PromptSession
from prompt_toolkit.patch_stdout import patch_stdout

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../..")))

from py_labs.lab01_streaming_llm.client import MODEL
from py_labs.lab01_streaming_llm.stream_message import stream_message
from py_labs.lab03_first_tool.tools.index import get_gemini_tools
from py_labs.lab03_first_tool.execute_tools import execute_tools

console = Console()


async def main():
    console.print(
        f"[bold cyan]Tool-Enabled Agent[/bold cyan] [dim]({MODEL}) — Enter to send, Ctrl+C to quit[/dim]\n"
    )
    contents: list[types.Content] = []
    session = PromptSession()
    tools = get_gemini_tools()

    while True:
        try:
            with patch_stdout():
                user_input = await session.prompt_async("> ")
            user_text = user_input.strip()
            if not user_text:
                continue

            contents.append(
                types.Content(role="user", parts=[types.Part.from_text(text=user_text)])
            )

            # Stream API call
            full_text = ""
            result = None

            console.print()
            async for event in stream_message(contents=contents, tools=tools):
                if event.type == "text":
                    full_text += event.text
                    console.print(event.text, end="", highlight=False)
                elif event.type == "tool_use_start":
                    console.print(
                        f"\n[bold yellow][Tool Call: {event.name}][/bold yellow]"
                    )
                elif event.type == "message_done":
                    result = event.result

            console.print()

            # Handle Tool Response if model requested tools
            if result and result.function_calls:
                contents.append(types.Content(role="model", parts=result.raw_parts))

                console.print("[dim]Executing tool...[/dim]")
                tool_response_content = await execute_tools(result.function_calls)
                contents.append(tool_response_content)

                # Stream second turn to let model synthesize result
                async for ev in stream_message(contents=contents, tools=tools):
                    if ev.type == "text":
                        console.print(ev.text, end="", highlight=False)
                console.print()

        except (KeyboardInterrupt, EOFError):
            console.print("\n[dim]Exiting... Bye![/dim]")
            break


if __name__ == "__main__":
    try:
        asyncio.run(main())
    except Exception as err:
        sys.stderr.write(f"Error: {err}\n")
        sys.exit(1)
