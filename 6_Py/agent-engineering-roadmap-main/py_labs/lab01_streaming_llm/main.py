import asyncio
import os
import sys

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../..")))

from py_labs.lab01_streaming_llm.stream_message import stream_message

async def main():
    prompt = (
        " ".join(sys.argv[1:])
        or "Explain in two sentences why terminal coding agents stream their responses."
    )

    print(f"> {prompt}\n")

    result = None
    async for event in stream_message(contents=[prompt]):
        if event.type == "text":
            sys.stdout.write(event.text)
            sys.stdout.flush()
        elif event.type == "tool_use_start":
            sys.stdout.write(f"\n[tool requested: {event.name}]\n")
            sys.stdout.flush()
        elif event.type == "message_done":
            result = event.result

    print(
        f"\n\n[{result.usage['input_tokens']} tokens in / {result.usage['output_tokens']} out, stop: {result.stop_reason}]"
    )

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except Exception as err:
        sys.stderr.write(f"Error: {err}\n")
        sys.exit(1)
