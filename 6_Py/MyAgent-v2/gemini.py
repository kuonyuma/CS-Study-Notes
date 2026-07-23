
from google import genai
from google.genai import types
from typing import Optional

def generate(client: genai.Client,
             model_name:str,
             prompt:str)->Optional[str]:
    try:
        response = client.models.generate_content(
            model = model_name,
            contents = prompt,
            config=types.GenerateContentConfig(
                temperature=0.0,  # 0.0 表示输出最严谨精确（ Agent 必备）
                max_output_tokens=8192,  # 最大输出字数限制
            )
        )

        if response and response.text:
            return response.text
        return None
    except Exception as e:
        print(f"连接 Gemini 发生错误: {e}")
        return None