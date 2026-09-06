"""
使用rich相关的模块来打印markdown
"""

from rich.markdown import Markdown
from rich.live import Live

chunks = [
    "# 实时 Markdown 演示\n\n",
    "这是正在流式生成的**加粗文本**。\n\n",
    "- 列表项 1\n",
    "- 列表项 2\n",
]

full_text = ""
import time

with Live(
    Markdown(full_text),
    auto_refresh=True,
    refresh_per_second=12,
) as live:
    for chunk in chunks:
        full_text += chunk
        live.update(Markdown(full_text))
        time.sleep(0.4)
