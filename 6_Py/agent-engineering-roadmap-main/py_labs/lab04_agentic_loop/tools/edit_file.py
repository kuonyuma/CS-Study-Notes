import os
from typing import Dict, Any
from py_labs.lab03_first_tool.tools.base import Tool, ToolResult

class EditFileTool(Tool):
    name = "edit_file"
    description = (
        "Edit a file by replacing an exact string with a new string. "
        "old_string must appear exactly once in the file — include enough "
        "surrounding lines to make it unique. Read the file first so "
        "old_string matches the current content exactly."
    )
    input_schema = {
        "type": "OBJECT",
        "properties": {
            "path": {
                "type": "STRING",
                "description": "Path to the file to edit.",
            },
            "old_string": {
                "type": "STRING",
                "description": "The exact text to replace. Must occur exactly once.",
            },
            "new_string": {
                "type": "STRING",
                "description": "The text to replace it with.",
            },
        },
        "required": ["path", "old_string", "new_string"],
    }
    read_only = False

    async def run(self, input_data: Dict[str, Any]) -> ToolResult:
        path = str(input_data.get("path") or "")
        old_string = str(input_data.get("old_string") or "")
        new_string = str(input_data.get("new_string") or "")

        if not old_string:
            return ToolResult(content="old_string must not be empty.", is_error=True)

        try:
            target_path = os.path.abspath(path)
            if not os.path.exists(target_path):
                return ToolResult(content=f"Error editing {path}: File does not exist.", is_error=True)

            with open(target_path, "r", encoding="utf-8") as f:
                content = f.read()

            occurrences = content.count(old_string)
            if occurrences == 0:
                return ToolResult(
                    content=(
                        f"old_string was not found in {path}. "
                        "Read the file again and copy the exact current text."
                    ),
                    is_error=True,
                )
            if occurrences > 1:
                return ToolResult(
                    content=(
                        f"old_string appears {occurrences} times in {path}. "
                        "Add more surrounding context so it matches exactly once."
                    ),
                    is_error=True,
                )

            new_content = content.replace(old_string, new_string, 1)
            with open(target_path, "w", encoding="utf-8") as f:
                f.write(new_content)

            return ToolResult(content=f"Edited {path}: replaced 1 occurrence.")
        except Exception as err:
            return ToolResult(content=f"Error editing {path}: {err}", is_error=True)
