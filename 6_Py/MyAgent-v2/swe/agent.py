import json
import os
from pathlib import Path
import yaml
from enum import Enum,auto
from typing import Dict, Callable
from google import genai
from llm import gemini
from Tools.read import read as read_file
from Tools.terminal import terminal as terminal
from Tools.write import write as write_file

BASE_DIR = Path(__file__).resolve().parent.parent


PROMPT_PATH = {"path":str(BASE_DIR / "data" / "swe-prompt.txt")}

class Name(Enum):
    READ_FILE = auto()
    WRITE_FILE = auto()
    TERMINAL = auto()
    NONE = auto()

class Tool:

    def __init__(self,name:str,func:Callable[[str],str]):
        self.name = name
        self.func = func

    def use(self,parameter)-> str:
        return self.func(parameter)

class Agent:

    def __init__(self,client):
        self.client = client
        self.mode_name = "gemini-3.6-flash"
        self.tools: Dict[Name, Tool] = {} # 导入工具
        self.messages:list[Dict[str,str]] = []
        self.tier = 0
        self.max_tier = 10
        self.system_prompt = self.load_template()

    def load_template(self):
        return read_file(PROMPT_PATH)

    def get_history(self) -> str:
        return "\n".join([f"{msg['role']}:{msg['content']}" for msg in self.messages])


    def execute(self,request:str):
        self.curr_query = request
        self.tier = 0
        history_str = self.get_history()
        if not history_str:
            history_str = "暂无历史"
        self.trace("user",request)
        prompt = self.system_prompt.format(
            query = request,
            history = history_str,
            cwd=os.getcwd()
        )
        return self.think(prompt)

    def register(self, name:Name, func):
            self.tools[name] = Tool(name,func)

    def trace(self, role, content):
        self.messages.append({"role":role,"content":content})
        HISTORY_PATH = BASE_DIR / "data"/ "history_message.txt"
        with open(HISTORY_PATH, 'a', encoding="utf-8") as f:
            f.write(f"{role}:{content}\n")

    def think(self,request:str):

        if self.tier < self.max_tier:
            self.tier += 1
            response = gemini.generate(self.client, self.mode_name, request)
            if response is None:
                msg = "Gemini 未返回有效响应，请重试。"
                self.trace("assistant", msg)
                return msg
            self.trace("assistant", response)
            return self.decide(response)
        else:
            msg = f"已达到最大思考次数({self.max_tier})，无法继续推理。"
            self.trace("assistant", msg)
            return msg



    def decide(self, response:str):

        cleared = response.strip()
        if cleared.startswith("```"):
            liens = cleared.split("\n")
            liens = [l for l in liens if not l.strip().startswith("```")]
            cleared = "\n".join(liens)

        try:
            parsed = json.loads(cleared)

            if "action" in parsed:
                name = parsed.get("action").get("name")
                input = parsed.get("action").get("input")
                return self.act(name,input)

            elif "answer" in parsed:
                answer = parsed["answer"]
                self.trace("assistant", answer)
                return answer
            else:
                answer = parsed.get("response") or parsed.get("message") or str(parsed)
                self.trace("assistant", answer)
                return answer
        except json.JSONDecodeError:
            self.trace("assistant", cleared)
            return cleared


    def act(self, name:str, tool_input):
        tool_name = name.upper()
        try:
            tool = self.tools[Name[tool_name]]

            response = tool.use(tool_input)
            self.trace("observation",f"[{tool_name}] 返回: \n{response}")
            history_str = self.get_history()
            prompt = self.system_prompt.format(
                query = self.curr_query,
                history = history_str,
                cwd=os.getcwd()
            )
            return self.think(prompt)
        except  KeyError as e:
            return f"未找到该工具{tool_name},{e}"

def creat_agent() ->Agent:
    # 获取key
    my_api_key:str = os.getenv("Gemini")

    if not my_api_key or my_api_key.startswith("your_"):
        CONFIG_YAML = BASE_DIR / "config"/"config.yaml"
        with open(CONFIG_YAML, 'r', encoding="utf-8") as f:
            config = yaml.safe_load(f)

        my_api_key = config.get("gemini",{}).get("key","")

    if not my_api_key or my_api_key.startswith("your_"):
        raise ValueError("key为空")

    # 创建gemini 客户端
    client = genai.Client(api_key = my_api_key)
    agent = Agent(client)

    agent.register(Name.READ_FILE,read_file)
    agent.register(Name.WRITE_FILE,write_file)
    agent.register(Name.TERMINAL,terminal)
    return agent


if __name__ == "__main__":

    agent = creat_agent()
    while True:
        request = input()
        if request.strip() == "exit":
            break
        response = agent.execute(request)
        print(f"AI: {response}")



