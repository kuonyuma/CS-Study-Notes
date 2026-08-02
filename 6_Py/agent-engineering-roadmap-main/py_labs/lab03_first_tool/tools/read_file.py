import os
from typing import Dict, Any
from .base import Tool, ToolResult

class ReadFileTool(Tool):
    name = "read_file"
    description = (
        "Read the contents of a file at a given path. Returns the file's "
        "content with line numbers."
    )
    input_schema = {
        "type": "OBJECT",
        "properties": {
            "path": {
                "type": "STRING",
                "description": "Path to the file to read.",
            },
        },
        "required": ["path"],
    }

    async def run(self, input_data: Dict[str, Any]) -> ToolResult:
        path = str(input_data.get("path") or "")
        try:
            target_path = os.path.abspath(path)
            if not os.path.exists(target_path):
                return ToolResult(content=f"Error reading {path}: File does not exist.", is_error=True)
            if os.path.isdir(target_path):
                return ToolResult(
                    content=f"Error reading {path}: Path is a directory, not a file. Use list_files instead.",
                    is_error=True,
                )

            with open(target_path, "r", encoding="utf-8") as f:
                lines = f.readlines()

            numbered = [f"{i + 1:4d} | {line}" for i, line in enumerate(lines)]
            return ToolResult(content="".join(numbered))
        except Exception as err:
            return ToolResult(content=f"Error reading {path}: {err}", is_error=True)
