from google.genai import types
from google import genai
from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client
from rich.console import Console
from rich.markdown import Markdown
from rich.live import Live
import os
import sys
import asyncio

console = Console()


def to_function_declarations(t) -> types.FunctionDeclaration:
    return types.FunctionDeclaration(
        name=t.name, description=t.description, parameters=t.inputSchema
    )


async def ask_llm(
    content: list[types.Content],
    declarations: list[types.FunctionDeclaration],
    session: ClientSession,
):
    api_key = os.getenv("GEMINI_API_KEY")
    if api_key is None:
        sys.stderr.write("没有设置key")
        return
    client = genai.Client(api_key=api_key)

    config = types.GenerateContentConfig(
        tools=[types.Tool(function_declarations=declarations)]
    )

    while True:
        full_text: str = ""
        full_calls: list[types.FunctionCall] = []
        full_parts: list[types.Part] = []

        with Live(console=console, refresh_per_second=12) as live:
            stream = await client.aio.models.generate_content_stream(
                model="gemini-3.5-flash-lite", contents=content, config=config
            )
            async for chunk in stream:
                if not chunk.candidates:
                    continue
                candidate = chunk.candidates[0]
                if not (candidate.content and candidate.content.parts):
                    continue
                for part in candidate.content.parts:
                    full_parts.append(part)
                    if part.text:
                        full_text += part.text
                        live.update(Markdown(full_text))
                    if part.function_call:
                        full_calls.append(part.function_call)

        if not full_calls:
            return full_text

        content.append(types.Content(role="model", parts=full_parts))

        response_parts: list[types.Part] = []
        for call in full_calls:
            args = dict(call.args or {})
            console.print(f"[MCP] 调用工具: {call.name}({args})")
            result = await session.call_tool(name=call.name or "", arguments=args)
            tmp = "".join(
                c.text for c in result.content if getattr(c, "type", "") == "text"
            )
            if getattr(result, "isError", False):
                tmp = f"工具错误:{call.name}: {tmp}"
            console.print(f"[MCP] → {tmp}")
            response_parts.append(
                types.Part(
                    function_response=types.FunctionResponse(
                        id=call.id,
                        name=call.name,
                        response={"result": tmp},
                    )
                )
            )
        content.append(types.Content(role="user", parts=response_parts))


async def main():
    params = StdioServerParameters(command=sys.executable, args=["server.py"])

    async with stdio_client(params) as (read, write):
        async with ClientSession(read, write) as session:
            await session.initialize()

            tools_result = await session.list_tools()
            declarations = [to_function_declarations(t) for t in tools_result.tools]

            content = []
            user_content = types.Content(
                role="user",
                parts=[
                    types.Part.from_text(text="查询一下当前的本地时间，使用已有的函数")
                ],
            )
            content.append(user_content)
            await ask_llm(content=content, declarations=declarations, session=session)


if __name__ == "__main__":
    asyncio.run(main())
    print("测试完毕")
