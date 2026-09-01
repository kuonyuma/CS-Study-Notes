from time import sleep
from rich.console import Console

console = Console()

with console.status(
    "[bold cyan]正在连接服务器...",
    spinner="dots",
    speed=12,
    refresh_per_second=14,
) as status:
    
    sleep(3)
    status.update("[bold cyan]服务器接收到相应..")
    sleep(3)


console.print("连接完成", style="green")
