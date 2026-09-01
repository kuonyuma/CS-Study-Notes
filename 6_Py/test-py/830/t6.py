import time

from rich.console import Console
from rich.live import Live
from rich.markdown import Markdown


def fake_stream():
    chunks = [
        "# 流式 Markdown\n\n",
        "这是一段正在生成的文字，",
        "它会随着文本到达而不断更新。\n\n",
        "## 代码示例\n\n",
        "```python\n",
        "print('hello')\n",
        "```\n",
    ]

    for chunk in chunks:
        time.sleep(0.2)
        yield chunk


console = Console()
parts = []

with Live(
    Markdown(""),
    console=console,
    refresh_per_second=12,
) as live:
    for chunk in fake_stream():
        parts.append(chunk)

        full_text = "".join(parts)
        live.update(Markdown(full_text))