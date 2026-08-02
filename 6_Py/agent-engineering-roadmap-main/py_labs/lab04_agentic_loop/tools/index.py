from typing import List, Optional
from google.genai import types
from py_labs.lab03_first_tool.tools.base import Tool
from py_labs.lab03_first_tool.tools.list_files import ListFilesTool
from py_labs.lab03_first_tool.tools.read_file import ReadFileTool
from .edit_file import EditFileTool

ALL_TOOLS: List[Tool] = [
    ListFilesTool(),
    ReadFileTool(),
    EditFileTool(),
]

def find_tool(name: str) -> Optional[Tool]:
    for t in ALL_TOOLS:
        if t.name == name:
            return t
    return None

def get_gemini_tools() -> List[types.Tool]:
    declarations = [t.to_gemini_declaration() for t in ALL_TOOLS]
    return [types.Tool(function_declarations=declarations)]
