import os
from typing import Dict, Any
from .base import Tool, ToolResult

class ListFilesTool(Tool):
    name = "list_files"
    description = (
        "List the files and directories at a given path. Directories are "
        "marked with a trailing '/'. Defaults to the current working directory."
    )
    input_schema = {
        "type": "OBJECT",
        "properties": {
            "path": {
                "type": "STRING",
                "description": "Directory to list. Defaults to '.'",
            },
        },
        "required": [],
    }

    async def run(self, input_data: Dict[str, Any]) -> ToolResult:
        path = str(input_data.get("path") or ".")
        try:
            target_path = os.path.abspath(path)
            if not os.path.exists(target_path):
                return ToolResult(content=f"Error listing {path}: Path does not exist.", is_error=True)

            entries = os.listdir(target_path)
            if not entries:
                return ToolResult(content=f"{path} is empty.")

            formatted = []
            for entry in entries:
                full_item = os.path.join(target_path, entry)
                if os.path.isdir(full_item):
                    formatted.append(f"{entry}/")
                else:
                    formatted.append(entry)

            formatted.sort()
            listing = "\n".join(formatted)
            return ToolResult(content=f"Contents of {path}:\n{listing}")
        except Exception as err:
            return ToolResult(content=f"Error listing {path}: {err}", is_error=True)
