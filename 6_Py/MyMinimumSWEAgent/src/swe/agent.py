"""
★ SWE Agent 核心模块 ★

这是整个项目最核心的文件。它实现了 ReAct（Reasoning + Acting）循环：

    用户提问 -> think() 思考 -> decide() 决策 -> act() 执行工具
                  ↑                                      |
                  |______________________________________|
                            观察结果反馈回来，继续思考

与 react-from-scratch 的对比：
┌────────────────────┬──────────────────────┬──────────────────────┐
│       维度         │  react-from-scratch  │   本项目 (SWE)        │
├────────────────────┼──────────────────────┼──────────────────────┤
│ 工具               │ Google / Wikipedia   │ read_file / write    │
│                    │                      │ _file / terminal     │
│ Tool.use() 参数    │ query: str (单参数)   │ params: dict (多参数) │
│ Name 枚举          │ WIKIPEDIA / GOOGLE   │ READ_FILE / WRITE    │
│                    │                      │ _FILE / TERMINAL     │
└────────────────────┴──────────────────────┴──────────────────────┘
"""
import sys
import os
import yaml
import json
from pathlib import Path
from enum import Enum, auto
from typing import Callable, List, Dict, Union
from pydantic import BaseModel, Field

# 将项目根目录添加到 sys.path，确保直接运行 python src/swe/agent.py 时不会报 ModuleNotFoundError
project_root = str(Path(__file__).resolve().parent.parent.parent)
if project_root not in sys.path:
    sys.path.insert(0, project_root)

from google import genai
from src.tools.read_file import read as read_file_tool
from src.tools.write_file import write as write_file_tool
from src.tools.terminal import run as terminal_tool
from src.utils.io import read_file, write_to_file
from src.config.logging import logger
from src.config.setup import config
from src.llm.gemini import generate


# ============================================================
# 类型定义
# ============================================================

Observation = Union[str, Exception]

PROMPT_TEMPLATE_PATH = "./data/input/swe_prompt.txt"
OUTPUT_TRACE_PATH = "./data/output/trace.txt"


class Name(Enum):
    """
    Agent 可用的工具名称枚举

    对比原项目：WIKIPEDIA / GOOGLE / NONE
    本项目改为：READ_FILE / WRITE_FILE / TERMINAL / NONE
    """
    READ_FILE = auto()
    WRITE_FILE = auto()
    TERMINAL = auto()
    NONE = auto()

    def __str__(self) -> str:
        return self.name.lower()


class Message(BaseModel):
    """对话消息：记录每一轮的角色和内容"""
    role: str = Field(..., description="消息发送者的角色")
    content: str = Field(..., description="消息内容")


# ============================================================
# Tool 类
# ============================================================

class Tool:
    """
    工具封装类

    对比原项目的关键变化：
    - 原项目: func 签名是 Callable[[str], str]      -> 只接收一个 query 字符串
    - 本项目: func 签名是 Callable[[dict], str]      -> 接收一个参数字典

    为什么改成 dict？
    因为 SWE 工具需要多个参数。比如 write_file 需要 path 和 content 两个参数，
    用一个 dict 就能灵活传递任意多个参数。
    """

    def __init__(self, name: Name, func: Callable[[dict], str]):
        self.name = name
        self.func = func

    def use(self, params: dict) -> Observation:
        """执行工具，传入参数字典"""
        try:
            return self.func(params)
        except Exception as e:
            logger.error(f"工具 {self.name} 执行出错: {e}")
            return str(e)


# ============================================================
# Agent 类（核心！）
# ============================================================

class Agent:
    """
    SWE Agent —— 实现 ReAct 循环

    生命周期:
        1. __init__()    : 初始化（加载 prompt 模板）
        2. register()    : 注册工具
        3. execute()     : 开始执行用户请求（入口方法）
           ↓
        4. think()       : 拼装 prompt，调用 LLM 思考
        5. decide()      : 解析 LLM 的 JSON 回复，判断是要用工具还是给出最终答案
        6. act()         : 执行工具，把结果作为 Observation 记录下来
           ↓
        7. 回到 think()  : 带着新的 Observation 继续思考（循环）
    """

    def __init__(self, client: genai.Client, model_name: str) -> None:
        self.client = client
        self.model_name = model_name
        self.tools: Dict[Name, Tool] = {}        # 已注册的工具
        self.messages: List[Message] = []         # 对话历史
        self.query = ""                           # 当前用户请求
        self.max_iterations = 10                  # 最大循环次数（防止无限循环）
        self.current_iteration = 0
        self.template = self._load_template()

    def _load_template(self) -> str:
        """加载 prompt 模板文件"""
        return read_file(PROMPT_TEMPLATE_PATH)

    def register(self, name: Name, func: Callable[[dict], str]) -> None:
        """注册一个工具到 Agent"""
        self.tools[name] = Tool(name, func)

    # --------------------------------------------------------
    # 对话历史管理
    # --------------------------------------------------------

    def trace(self, role: str, content: str) -> None:
        """
        记录一条对话消息

        同时做两件事：
        1. 存到内存列表 self.messages（用于构建 prompt 的 history）
        2. 追加写到 trace.txt 文件（用于事后查看 Agent 的完整推理过程）
        """
        if role != "system":
            self.messages.append(Message(role=role, content=content))
        write_to_file(path=OUTPUT_TRACE_PATH, content=f"{role}: {content}\n")

    def get_history(self) -> str:
        """获取对话历史（拼成字符串，塞进 prompt）"""
        return "\n".join([f"{msg.role}: {msg.content}" for msg in self.messages])

    # --------------------------------------------------------
    # ReAct 循环的三个核心方法：think -> decide -> act
    # --------------------------------------------------------

    def think(self) -> None:
        """
        ★ 第一步：思考

        1. 把用户请求 + 历史对话 + 工具列表 拼进 prompt 模板
        2. 调用 LLM 获取回复
        3. 把回复交给 decide() 处理
        """
        self.current_iteration += 1
        logger.info(f"===== 第 {self.current_iteration} 轮思考 =====")
        write_to_file(
            path=OUTPUT_TRACE_PATH,
            content=f"\n{'=' * 50}\n第 {self.current_iteration} 轮\n{'=' * 50}\n"
        )

        # 安全检查：防止无限循环
        if self.current_iteration > self.max_iterations:
            logger.warning("已达最大迭代次数，停止。")
            self.trace("assistant", "已达到最大迭代次数，停止执行。目前的进展：" + self.get_history())
            return

        # 拼装 prompt（用模板的 format 方法填入变量）
        prompt = self.template.format(
            query=self.query,
            history=self.get_history() if self.messages else "（暂无历史）"
        )

        # 调用 LLM
        response = self._ask_llm(prompt)
        logger.info(f"LLM 回复: {response[:200]}...")  # 只打印前 200 字符
        self.trace("assistant", f"Thought: {response}")

        # 交给 decide() 解析
        self.decide(response)

    def decide(self, response: str) -> None:
        """
        ★ 第二步：决策

        解析 LLM 返回的 JSON，判断两种情况：
        1. 包含 "action" -> LLM 想使用某个工具 -> 调用 act()
        2. 包含 "answer" -> LLM 已有最终答案 -> 记录并结束
        """
        try:
            # 清理 LLM 回复中可能包含的 markdown 代码块标记
            cleaned = response.strip()
            if cleaned.startswith("```"):
                lines = cleaned.split("\n")
                lines = [l for l in lines if not l.strip().startswith("```")]
                cleaned = "\n".join(lines)

            parsed = json.loads(cleaned)

            if "action" in parsed:
                # LLM 想要使用工具
                action = parsed["action"]
                tool_name_str = action["name"].upper()
                tool_name = Name[tool_name_str]

                if tool_name == Name.NONE:
                    self.think()  # 不需要工具，继续思考
                else:
                    tool_input = action.get("input", {})
                    self.trace("assistant", f"Action: 使用 {tool_name} 工具")
                    self.act(tool_name, tool_input)

            elif "answer" in parsed:
                # LLM 给出了最终答案
                self.trace("assistant", f"最终回答: {parsed['answer']}")

            else:
                raise ValueError("JSON 中既没有 'action' 也没有 'answer'")

        except json.JSONDecodeError as e:
            logger.error(f"JSON 解析失败: {e}\n原始回复: {response}")
            self.trace("assistant", "JSON 解析失败，重新尝试...")
            self.think()

        except KeyError as e:
            logger.error(f"工具名无效: {e}")
            self.trace("assistant", f"工具名无效: {e}，重新尝试...")
            self.think()

        except Exception as e:
            logger.error(f"decide() 出错: {e}")
            self.trace("assistant", "处理出错，重新尝试...")
            self.think()

    def act(self, tool_name: Name, params: dict) -> None:
        """
        ★ 第三步：执行

        对比原项目的关键变化：
        - 原项目: act(self, tool_name, query: str) -> tool.use(query)
        - 本项目: act(self, tool_name, params: dict) -> tool.use(params)

        执行完后，把结果作为 Observation 记录到历史，然后回到 think()
        """
        tool = self.tools.get(tool_name)
        if tool:
            logger.info(f"执行工具: {tool_name}, 参数: {params}")
            result = tool.use(params)
            observation = f"[{tool_name} 的结果]: {result}"
            self.trace("system", observation)
            # 把观察结果也加入消息历史，这样 LLM 下次能看到
            self.messages.append(Message(role="system", content=observation))
            self.think()  # ← 带着新的观察结果，继续思考
        else:
            logger.error(f"工具未注册: {tool_name}")
            self.trace("system", f"错误：工具 {tool_name} 未注册")
            self.think()

    # --------------------------------------------------------
    # 入口方法
    # --------------------------------------------------------

    def execute(self, query: str) -> str:
        """
        Agent 的入口方法

        调用这个方法就启动了整个 ReAct 循环。
        """
        self.query = query
        self.trace(role="user", content=query)
        self.think()
        # 循环结束后，返回最后一条消息作为结果
        return self.messages[-1].content if self.messages else "无结果"

    def _ask_llm(self, prompt: str) -> str:
        """调用 LLM 获取回复"""
        response = generate(self.client, self.model_name, prompt)
        return str(response) if response else "LLM 无响应"


# ============================================================
# 入口函数
# ============================================================

def run(query: str) -> str:
    """
    创建 Agent 并执行查询

    API Key 查找顺序：
    1. 环境变量 GEMINI_API_KEY
    2. config/config.yml 中的 gemini.key
    """
    # 1. 获取 API Key
    api_key = os.environ.get("GEMINI_API_KEY")

    if not api_key:
        try:
            with open("./config/config.yml", "r", encoding="utf-8") as f:
                data = yaml.safe_load(f)
                key = data.get("gemini", {}).get("key", "")
                if key and not key.startswith("YOUR_"):
                    api_key = key
        except Exception:
            pass

    if not api_key:
        raise ValueError(
            "缺少 API Key！请设置环境变量 GEMINI_API_KEY "
            "或在 config/config.yml 中填写 gemini.key"
        )

    # 2. 创建 Gemini 客户端
    client = genai.Client(api_key=api_key)

    # 3. 创建 Agent 并注册工具
    agent = Agent(client=client, model_name=config.MODEL_NAME)
    agent.register(Name.READ_FILE, read_file_tool)
    agent.register(Name.WRITE_FILE, write_file_tool)
    agent.register(Name.TERMINAL, terminal_tool)

    # 4. 确保输出目录存在
    # 检查电脑里有没有 ./data/output 这个文件夹（用来保存机器人的思考轨迹 trace.txt）。
    # 如果有就忽略，如果没有就自动新建一个。
    os.makedirs("./data/output", exist_ok=True)

    # 5. 执行！
    answer = agent.execute(query)
    return answer


if __name__ == "__main__":
    user_query = "你好，你是我写的第一个agent！"
    if len(sys.argv) > 1:
        user_query = " ".join(sys.argv[1:])

    logger.info(f"用户请求: {user_query}")
    final_answer = run(user_query)
    print("\n" + "=" * 50)
    print(f"最终结果: {final_answer}")
    print("=" * 50)
