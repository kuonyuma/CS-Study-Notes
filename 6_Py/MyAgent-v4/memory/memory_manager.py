from google.genai import types
from google import genai
from llm import gemini
import logging


async def compress_contents(
    contents: list[types.Content],
    client: genai.Client,
    model_name: str,
    max_len: int = 5,
) -> list[types.Content]:

    if len(contents) >= max_len:
        first_content = contents[0]
        recently_content = contents[-2:]
        compress_content = contents[1:-2]

        summary_prompt = (
            "请将上述对话历史总结为一份极简的【前情提要】，包含以下信息：\n"
            "1. 已尝试或执行过的操作/工具\n"
            "2. 读取或修改过的文件及关键行\n"
            "3. 当前遇到的核心报错或最新状态\n"
            "字数控制在 150 字以内，保持精炼。"
        )

        if not compress_content:
            return contents

        query = compress_content + [
            types.Content(
                role="user", parts=[types.Part.from_text(text=summary_prompt)]
            )
        ]

        tmp_text = ""
        try:
            async for ev in gemini.generate(
                client=client,
                model_name=model_name,
                contents=query,
                system_instruction=(
                    "你是一个严谨的上下文压缩助手，负责提取任务执行的核心状态."
                ),
            ):
                if ev.type == "end" and ev.result:
                    tmp_text = ev.result.full_text or ""
        except Exception as e:
            logging.warning(f"上下文压缩失败，保持原历史: {e}")
            return contents

        if not tmp_text:
            logging.warning("上下文压缩未获得摘要，保持原历史")
            return contents

        summary_content = types.Content(
            role="user",
            parts=(first_content.parts or [])
            + [types.Part.from_text(text=f"【系统前情提要 / 历史记忆】:\n{tmp_text}")],
        )

        new_contents = [summary_content] + recently_content

        return new_contents
    else:
        return contents
