import os

from google import genai
from google.genai import types
import yaml
from pathlib import Path
# 一个简单的加法函数
def add(a: int, b: int) -> int:
    """计算两个整数的和。"""
    return a + b

BASE_DIR = Path(__file__).resolve().parent

def main(query: str):
    gemini_key: str = None
    model_name = "gemini-3.6-flash"

    # 注意这里的 r 前缀，它防止了路径中的 \6 被错误转义！
    config_path = BASE_DIR /"config"/"config.yaml"
    with open(config_path, 'r', encoding="utf-8") as f:
        config = yaml.safe_load(f)
        # 按照 config.yaml 的层级，读取 gemini 下面的 key
        gemini_key = config.get("gemini", {}).get("key", "")

    if not gemini_key:
        print("错误：未能成功从 config.yaml 读取到 Key！")
        return

    client = genai.Client(api_key=gemini_key)
    
    response = client.models.generate_content(
        model=model_name,
        contents=query,
        config=types.GenerateContentConfig(
            tools=[add],
            temperature=0.0,
            # ANY 模式：强制模型必须调用工具，不允许直接用文字回答
            tool_config=types.ToolConfig(
                function_calling_config=types.FunctionCallingConfig(
                    mode="ANY"
                )
            )
        )
    )

    result = None
    if response.function_calls:
        for call in response.function_calls:
            if call.name == "add":
                result = add(**call.args)
                print(f"本地函数计算结果: {result}")
    else:
        print(f"AI 直接回答: {response.text}")

if __name__ == "__main__":
    print("请输入（例如：15 加 27 等于多少？）：")
    query = input()
    main(query)