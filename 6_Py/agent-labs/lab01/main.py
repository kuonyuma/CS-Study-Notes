from stream_message import stream_message
import sys
import asyncio


async def main():

    prompt: str = " ".join(sys.argv[1:]) or "你好"
    result = None

    async for event in stream_message(contents=prompt):
        if event.type == "text":
            sys.stdout.write(event.text)
        elif event.type == "tool_use_start":
            sys.stdout.write(f"使用了工具{event.name}")
        elif event.type == "message_done":
            result = event.result

    print(
        f"\n\n[{result.usage['input_tokens']} tokens in / {result.usage['output_tokens']} out, stop: {result.stop_reason}]"
    )


if __name__ == "__main__":
    try:
        asyncio.run(main())
    except Exception as e:
        sys.stderr.write(f"错误:{e}")
        sys.exit(1)
