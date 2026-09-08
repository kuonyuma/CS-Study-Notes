from time import sleep
from rich.live import Live
from rich.markdown import Markdown
with Live("准备开始") as live:
    sleep(1)
    live.update("正在处理")
    sleep(1)
    live.update("处理完成")

with Live("开始",auto_refresh=True,transient=True) as live:
    sleep(1)
    live.update("第二帧")
    sleep(1)
    live.update(Markdown("# 第三帧"))
