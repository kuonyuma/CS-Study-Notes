

from rich.table import Table

agents = [
    {"name": "researcher", "status": "running", "tasks": 3},
    {"name": "coder", "status": "idle", "tasks": 0},
    {"name": "reviewer", "status": "failed", "tasks": 1},
]


table = Table()
table.add_column("name")
table.add_column("status")
table.add_column("tasks")

for task in agents:
    table.add_row(task["name"],task["status"],str(task["tasks"]))

from rich.console import Console

console = Console()
console.print(table)

from rich.live import Live
import time
with Live(table) as live:
    for i in range(1,5):
        time.sleep(1)
        