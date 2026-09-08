from prompt_toolkit import PromptSession
from rich.live import Live
from rich.markdown import Markdown
session = PromptSession()
while True:
    query = session.prompt("请输入> ").strip()
    if query == "exit":
        break 
    elif query == "":
        print("请重新输入")
        continue
    print(f"你好：{query}")
print("prompt_toolkit测试结束")

