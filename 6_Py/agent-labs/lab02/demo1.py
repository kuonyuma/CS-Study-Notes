from google.genai import types
from google import genai
import os
import sys
import asyncio

key = os.getenv(key="GEMINI_API_KEY", default="")

if key == "":
    print("未配置密钥")

client = genai.Client(api_key=key)

config = types.GenerateContentConfig(
    max_output_tokens=4000, system_instruction="你是一位编程助手", tools=None
)

contents: list[types.Content] = []


async def generate():

    contents.append(
        types.Content(
            role="user", parts=[types.Part.from_text(text="你好，请介绍一下你自己")]
        )
    )

    response = await client.aio.models.generate_content_stream(
        model="gemini-3.6-flash", config=config, contents=contents
    )

    async for part in response:
        if part.text:
            sys.stdout.write(part.text)
        if part.function_calls:
            pass
    print()


if __name__ == "__main__":
    asyncio.run(generate())
    print("脚本运行结束")
