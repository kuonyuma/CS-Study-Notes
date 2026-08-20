from collections.abc import AsyncGenerator
import os

from google.genai import Client, types


async def ask_gemini(
    history: list[dict[str, str]],
) -> AsyncGenerator[str, None]:

    # 获取密钥与创建客户端
    key = os.getenv("GEMINI_API_KEY") or os.getenv("GOOGLE_GENERATIVE_AI_API_KEY")
    if not key:
        raise RuntimeError("请设置 GEMINI_API_KEY")
    client = Client(api_key=key)

    # 从数据库中获取历史对话
    content: list[types.Content] = []
    for bean in history:
        role = bean["role"]
        if role not in {"user", "model"}:
            raise ValueError(f"Unsupported message role: {role}")
        content.append(
            types.Content(
                role=role,
                parts=[types.Part.from_text(text=bean["content"])],
            )
        )
    # 请求模型
    response = await client.aio.models.generate_content_stream(
        model="gemini-3.5-flash-lite",
        contents=content,
        config=types.GenerateContentConfig(
            tools=None,
            max_output_tokens=4096,
        ),
    )
    # 遍历chunk,发送str
    async for chunk in response:
        candidates = chunk.candidates or []
        if not candidates:
            continue

        candidate = candidates[0]
        if not candidate.content or not candidate.content.parts:
            continue

        for part in candidate.content.parts:
            if part.text:
                yield part.text
