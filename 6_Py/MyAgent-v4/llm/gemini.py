from google import genai
from google.genai import types
from typing import Optional, Union, List

from Tools.write import write
from Tools.terminal import terminal
from Tools.read import read


def generate(
    client: genai.Client,
    model_name: str,
    contents: Union[str, List[types.Content]],
    system_instruction: Optional[str] = None,
    use_tools: bool = True,
) -> Optional[types.GenerateContentResponse]:
    try:
        config = types.GenerateContentConfig(
            temperature=1.0,
            max_output_tokens=8192,
            tools=[read, terminal, write] if use_tools else [],
            system_instruction=system_instruction,
        )

        response = client.models.generate_content(
            model=model_name, contents=contents, config=config
        )

        return response
    except Exception as e:
        print(f"连接 Gemini 发生错误: {e}")
        return None
