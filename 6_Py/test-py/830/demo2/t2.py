from prompt_toolkit import prompt
from prompt_toolkit.cursor_shapes import CursorShape

while True:
    name = prompt(
        "请输入：",
        is_password=False,
        default="你好",
        bottom_toolbar="提示：按 Ctrl+C 退出",
        rprompt="测试信息",
        cursor=CursorShape.UNDERLINE,
        multiline=True,
    )
    print(f"得到结果{name}")
    if "exit" == name:
        break
