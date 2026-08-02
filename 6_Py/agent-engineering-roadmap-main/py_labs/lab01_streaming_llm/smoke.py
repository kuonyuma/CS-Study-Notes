import asyncio
import os
import sys

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../..")))

from py_labs.lab01_streaming_llm.stream_message import stream_message

async def main():
    text = ""
    result = None
    async for event in stream_message(
        contents=["What is 2 + 2? Answer in one short sentence."],
        max_tokens=512,
    ):
        if event.type == "text":
            text += event.text
            sys.stdout.write(event.text)
            sys.stdout.flush()
        elif event.type == "message_done":
            result = event.result

    print(f"\n[usage: {result.usage['input_tokens']} in / {result.usage['output_tokens']} out]")

    if not text.strip():
        sys.stderr.write("SMOKE FAIL: empty response\n")
        sys.exit(1)
    print("SMOKE OK")

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except Exception as err:
        sys.stderr.write(f"SMOKE FAIL: {err}\n")
        sys.exit(1)
