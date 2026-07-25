import json
import os
from pathlib import Path
import yaml
from google import genai
from google.genai import types  # 补全 types 导入
from llm import gemini
from Tools.read import read as read_file
from Tools.terminal import terminal as terminal
from Tools.write import write as write_file

BASE_DIR = Path(__file__).resolve().parent.parent
PROMPT_PATH = BASE_DIR / "data" / "swe-prompt.txt"


class Agent:
    def __init__(self, client):
        self.client = client
        self.mode_name = "gemini-3.6-flash"
        self.tools: Dict[str, Callable] = {}
        self.contents: list[types.Content] = []
        self.max_tier = 10
        self.system_prompt = self.load_template()

    def load_template(self):
        return read_file(str(PROMPT_PATH))

    def execute(self, request: str) -> str:
        """Agent 执行入口"""
        # 1. 每次新请求时重置对话历史
        self.contents = []

        # 2. 将用户的初始请求封装为标准的 types.Content
        self.contents.append(
            types.Content(role="user", parts=[types.Part.from_text(text=request)])
        )

        # 3. 开启思考循环
        return self.think(tier=0)

    def register(self, func: Callable):
        self.tools[func.__name__] = func

    def think(self, tier: int = 0) -> str:
        """原生 Function Calling 核心思考循环"""
        if tier >= self.max_tier:
            return "已达到最大思考轮数 (max_tier)，停止思考。"

        # 1. 发送包含完整 Content 历史的请求
        response = gemini.generate(
            self.client,
            self.mode_name,
            contents=self.contents,
            system_instruction=self.system_prompt,
        )

        if not response or not response.candidates:
            return "错误: Gemini API 未返回有效结果。"

        # 2. 记录模型的 Content 到历史中
        model_content = response.candidates[0].content
        self.contents.append(model_content)

        # 3. 检查是否有工具调用
        function_calls = response.function_calls
        if function_calls:
            for call in function_calls:
                func_name = call.name
                func_args = call.args or {}

                # 执行本地工具
                if func_name in self.tools:
                    try:
                        result = self.tools[func_name](**func_args)
                    except Exception as e:
                        result = f"工具 {func_name} 执行异常: {e}"
                else:
                    result = f"错误: 未注册名为 {func_name} 的工具"

                # 4. 将工具执行结果作为 user role 存入历史
                self.contents.append(
                    types.Content(
                        role="user",
                        parts=[
                            types.Part.from_function_response(
                                name=func_name, response={"result": str(result)}
                            )
                        ],
                    )
                )

            # 5. 带着工具结果递归进入下一轮思考
            return self.think(tier=tier + 1)

        # 6. 没有工具调用，返回最终答案
        return response.text if response.text else "(模型未返回文本)"


def creat_agent() -> Agent:
    my_api_key: str = os.getenv("GEMINI_API_KEY")

    if not my_api_key or my_api_key.startswith("your_"):
        CONFIG_YAML = BASE_DIR / "config" / "config.yaml"
        with open(CONFIG_YAML, "r", encoding="utf-8") as f:
            config = yaml.safe_load(f)
            my_api_key = config.get("gemini", {}).get("key", "")

    if not my_api_key or my_api_key.startswith("your_"):
        raise ValueError("key为空")

    client = genai.Client(api_key=my_api_key)
    agent = Agent(client)

    agent.register(read_file)
    agent.register(write_file)
    agent.register(terminal)
    return agent


if __name__ == "__main__":
    agent = creat_agent()
    while True:
        request = input("User: ")
        if request.strip() == "exit":
            break
        response = agent.execute(request)
        print(f"AI: {response}\n")
