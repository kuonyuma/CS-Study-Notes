import asyncio
import os
import sys

from google import genai
from google.genai import types
from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client

MODEL = os.environ.get("GEMINI_MODEL", "gemini-3.5-flash")
SERVER_SCRIPT = "server.py"

try:
    sys.stdout.reconfigure(encoding="utf-8")
except AttributeError:
    pass


class DeepCopySafeClientSession(ClientSession):
    def __deepcopy__(self, memo):
        return self


async def main():
    api_key = os.environ.get("GEMINI_API_KEY")
    if not api_key:
        print("未找到 GEMINI_API_KEY 环境变量")
        return

    client = genai.Client(api_key=api_key)
    params = StdioServerParameters(command=sys.executable, args=[SERVER_SCRIPT])

    async with stdio_client(params) as (read, write):
        async with DeepCopySafeClientSession(read, write) as session:
            await session.initialize()
            tools_result = await session.list_tools()
            print(f"[MCP] 工具发现完成，共 {len(tools_result.tools)} 个:")
            for t in tools_result.tools:
                print(f"  - {t.name}: {t.description}")
            print("[提示] 本文件把 mcp.ClientSession 直接塞进 tools=[session]，"
                  "工具发现/调用/回传全部由 SDK 自动完成")

            history: list[types.Content] = []
            print("输入你的问题（exit 退出）")
            while True:
                try:
                    prompt = input("你 > ")
                except (EOFError, KeyboardInterrupt):
                    break
                prompt = prompt.strip()
                if not prompt:
                    continue
                if prompt in ("exit", "quit", "q"):
                    break
                history.append(types.Content(role="user", parts=[types.Part(text=prompt)]))
                response = await client.aio.models.generate_content(
                    model=MODEL,
                    contents=history,
                    config=types.GenerateContentConfig(tools=[session]),
                )
                if response.candidates:
                    history.append(
                        types.Content(role="model", parts=response.candidates[0].content.parts)
                    )
                print(f"Gemini > {response.text or '（模型无输出）'}")


if __name__ == "__main__":
    asyncio.run(main())
