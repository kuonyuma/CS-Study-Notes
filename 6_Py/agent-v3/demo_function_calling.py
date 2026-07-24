import os
from pathlib import Path
import yaml
from google import genai
from google.genai import types

# 1. 定义一个普通的 Python 函数
def get_current_weather(location: str) -> str:
    """
    获取指定位置的当前天气。
    Args:
        location: 城市名称，比如 "北京", "上海"
    """
    print(f"\n[本地代码正在执行...] 正在查询 {location} 的天气...")
    return f"{location} 的当前天气是：晴朗，25度"

def main():
    # 强制清理环境中的 Vertex/ADC 变量，确保使用 YAML API Key
    os.environ.pop("GOOGLE_APPLICATION_CREDENTIALS", None)
    os.environ.pop("GOOGLE_CLOUD_PROJECT", None)
    
    api_key = os.environ.get("Gemini")
    model_name = "gemini-3.6-flash"
    
    # 纯粹读取 MyAgent-v2 的 yaml 配置文件
    config_path = Path(r"a:\Root_Code\Github_Workspace\6_Py\MyAgent-v2\config\config.yaml")
    if config_path.exists():
        with open(config_path, "r", encoding="utf-8") as f:
            config = yaml.safe_load(f)
            if not api_key:
                api_key = config.get("gemini", {}).get("key", "")

    if not api_key or api_key.startswith("your_"):
        print("请先在 config.yaml 中配置有效的 API Key！")
        return

    # 纯粹使用 yaml 中的 API Key 初始化客户端
    client = genai.Client(api_key=api_key)

    query = "北京今天天气怎么样？"
    print(f"用户提问: {query}\n")

    print(f"模型 ({model_name}) 正在思考...")
    response = client.models.generate_content(
        model=model_name,
        contents=query,
        config=types.GenerateContentConfig(
            tools=[get_current_weather],
            temperature=1.0
        )
    )

    print("\n--- 模型的原生返回内容 ---")
    if response.function_calls:
        for tool_call in response.function_calls:
            print("触发了 Function Calling!")
            print(f"想调用的函数名 (name): {tool_call.name}")
            print(f"想传入的参数 (args): {tool_call.args}")
            if tool_call.name == "get_current_weather":
                result = get_current_weather(**tool_call.args)
                print(f"\n函数最终返回结果: {result}")
    else:
        print("模型直接回答了文本：", response.text)

if __name__ == "__main__":
    import sys
    if hasattr(sys.stdout, 'reconfigure'):
        sys.stdout.reconfigure(encoding='utf-8')
    main()
