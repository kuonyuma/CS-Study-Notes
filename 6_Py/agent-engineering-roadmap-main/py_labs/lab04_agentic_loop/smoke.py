import asyncio
import os
import sys
from google.genai import types

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../..")))

from py_labs.lab04_agentic_loop.core.agentic_loop import query

async def auto_allow(name: str, input_data: dict) -> bool:
    return True

async def smoke_test():
    print("Testing Agentic Loop with Gemini...")
    contents = [
        types.Content(
            role="user",
            parts=[types.Part.from_text(text="List the files in the current working directory.")]
        )
    ]

    result = None
    async for event in query(contents=contents, can_use_tool=auto_allow, max_turns=3):
        if event.type == "text":
            sys.stdout.write(event.text)
            sys.stdout.flush()
        elif event.type == "tool_start":
            print(f"\n[Tool: {event.name}]")
        elif event.type == "done":
            result = event.loop_result

    assert result is not None, "Loop did not return a result"
    assert result.turns >= 1, "Loop did not execute any turns"
    print(f"\n[Turns: {result.turns}, Reason: {result.termination_reason}]")

if __name__ == "__main__":
    try:
        asyncio.run(smoke_test())
        print("SMOKE OK")
    except Exception as err:
        sys.stderr.write(f"SMOKE FAIL: {err}\n")
        sys.exit(1)
