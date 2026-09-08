from prompt_toolkit import PromptSession
from prompt_toolkit.history import FileHistory
from prompt_toolkit.auto_suggest import AutoSuggestFromHistory

session = PromptSession(
    history=FileHistory(".context.txt"),
    auto_suggest=AutoSuggestFromHistory(),
)

while True:
    try:
        query = session.prompt("请输入> ")
        if query == "exit":
            break
        print(query)
    except KeyboardInterrupt:
        print("取消输入")
        continue
    except EOFError:
        print("已退出程序")
        break
print("测试结束")
