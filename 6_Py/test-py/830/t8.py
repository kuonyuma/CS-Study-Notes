from rich.live import Live
from rich.table import Table

table = Table()
table.add_column("状态")
table.add_row("处理中")

with Live(table) as live:
    live.console.print("任务已启动")
    table.add_row("第一阶段完成")
