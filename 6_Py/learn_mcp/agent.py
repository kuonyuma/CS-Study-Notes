import asyncio
import os
import sys

from google import genai
from google.genai import types
from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client

MODEL = os.environ.get("GEMINI_MODEL", "gemini-3.6-flash")
SERVER_SCRIPT = "server.py"

try:
    sys.stdout.reconfigure(encoding="utf-8")
except AttributeError:
    pass


def to_function_declaration(tool) -> types.FunctionDeclaration:
    return types.FunctionDeclaration(
        name=tool.name,
        description=tool.description,
        parameters=tool.inputSchema,
    )


async def ask_llm(client, history, declarations, session) -> str:
    while True:
        response = await client.aio.models.generate_content(
            model=MODEL,
            contents=history,
            config=types.GenerateContentConfig(
                tools=[types.Tool(function_declarations=declarations)]
            ),
        )
        if not response.candidates or not response.candidates[0].content.parts:
            return "（模型无输出）"
        parts = response.candidates[0].content.parts
        history.append(types.Content(role="model", parts=parts))

        calls = [p.function_call for p in parts if p.function_call]
        if not calls:
            return "".join(p.text for p in parts if p.text) or "（模型无文本输出）"

        response_parts = []
        for call in calls:
            args = dict(call.args or {})
            print(f"  [MCP] 调用工具: {call.name}({args})")
            result = await session.call_tool(name=call.name, arguments=args)
            text = "".join(
                c.text for c in result.content if getattr(c, "type", "") == "text"
            )
            if getattr(result, "isError", False):
                text = f"[工具错误] {text}"
            print(f"  [MCP] 返回结果: {text}")
            response_parts.append(
                types.Part(
                    function_response=types.FunctionResponse(
                        id=call.id, name=call.name, response={"result": text}
                    )
                )
            )
        history.append(types.Content(role="user", parts=response_parts))


async def main():
    api_key = os.environ.get("GEMINI_API_KEY")
    if not api_key:
        print("未找到 GEMINI_API_KEY 环境变量")
        return

    client = genai.Client(api_key=api_key)
    params = StdioServerParameters(command=sys.executable, args=[SERVER_SCRIPT])

    async with stdio_client(params) as (read, write):
        async with ClientSession(read, write) as session:
            await session.initialize()
            tools_result = await session.list_tools()
            print(f"[MCP] 工具发现完成，共 {len(tools_result.tools)} 个:")
            for t in tools_result.tools:
                print(f"  - {t.name}: {t.description}")

            declarations = [to_function_declaration(t) for t in tools_result.tools]
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
                history.append(
                    types.Content(role="user", parts=[types.Part(text=prompt)])
                )
                answer = await ask_llm(client, history, declarations, session)
                print(f"Gemini > {answer}")


if __name__ == "__main__":
    asyncio.run(main())
