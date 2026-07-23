import json
import os
import sys
from pathlib import Path
from venv import create
import yaml
from enum import Enum,auto
from typing import Dict, Callable
from google import genai
import gemini
from Tools.read import read as read_file_tool
from Tools.terminal import terminal as terminal_tool
from Tools.write import write as write_file_tool
from gemini import generate

BASE_DIR = Path(__file__).resolve().parent


PROMPT_PATH = BASE_DIR / "data" / "swe-prompt.txt"

class Name(Enum):
    READ_FILE = auto()
    WRITE_FILE = auto()
    TERMINA_TOOL = auto()
    NONE_TOOL = auto()

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
        return read_file_tool(PROMPT_PATH)

    def get_history(self) -> str:
        return "\n".join([f"{msg['role']}:{msg['content']}" for msg in self.messages])


    def execute(self,request:str):
        self.tier = 0
        history_str = self.get_history()
        if not history_str:
            history_str = "暂无历史"
        self.trace("user",request)
        prompt = self.system_prompt.format(
            query = request,
            history = history_str
        )
        return self.think(prompt)

    def register(self, name:Name, func):
            self.tools[name] = Tool(name,func)

    def trace(self, role, content):
        self.messages.append({"role":role,"content":content})
        with open("history_message.txt",'a',encoding="utf-8") as f:
            f.write(f"{role}:{content}\n")

    def think(self,request:str):

        if self.tier < self.max_tier:
            self.tier += 1
            response = gemini.generate(self.client,self.mode_name,request)
            return self.decide(response)
        else:
            print("达到思考上限")


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


    def act(self, tool_name:str, tool_input):
        tool = self.tools[Name[tool_name.upper()]]
        response = tool.use(tool_input)
        return self.think(response)

def creat_agent() ->Agent:
    # 获取key
    my_api_key:str = os.getenv("Gemini")

    if not my_api_key or my_api_key.startswith("your_"):
        with open("config.yaml",'r',encoding="utf-8") as f:
            config = yaml.safe_load(f)

        my_api_key = config.get("gemini",{}).get("key","")

    if not my_api_key or my_api_key.startswith("your_"):
        raise ValueError("key为空")

    # 创建gemini 客户端
    client = genai.Client(api_key = my_api_key)
    agent = Agent(client)

    agent.register(Name.READ_FILE,read_file_tool)
    agent.register(Name.WRITE_FILE,write_file_tool)
    agent.register(Name.TERMINA_TOOL,terminal_tool)
    return agent


if __name__ == "__main__":

    agent = creat_agent()
    while True:
        request = input("用户: ")
        if request.strip() == "exit":
            break
        response = agent.execute(request)
        print(f"AI: {response}")



