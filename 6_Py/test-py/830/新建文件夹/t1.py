from prompt_toolkit import prompt, PromptSession

# 方式一：一次性输入（简单脚本）
name = prompt("请输入你的名字: ")
print(f"你好, {name}!")

# 方式二：连续会话（推荐用于 REPL / 交互循环）
session = PromptSession()
while True:
    try:
        cmd = session.prompt("my-cli > ")
        if cmd == "exit":
            break
        print(f"执行命令: {cmd}")
    except (KeyboardInterrupt, EOFError):
        break